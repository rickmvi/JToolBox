/*
 * Console API - Utilitarian library for input, output and formatting on the console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.rickmvi.jtoolbox.file.util;

import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * <h2>PathUtils</h2>
 *
 * A collection of static helper methods for working with {@link java.nio.file.Path}
 * and file path operations. Provides utilities for building, normalizing, and converting
 * file paths into different formats (absolute, URI, URL, filename extraction, etc.).
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * Path path = PathUtil.combine("data", "logs", "output.txt");
 * System.out.println(PathUtil.getFileName(path));        // output.txt
 * System.out.println(PathUtil.getParentDirectory(path)); // data/logs
 * System.out.println(PathUtil.toAbsolutePath(path));     // /home/user/project/data/logs/output.txt
 * System.out.println(PathUtil.toUri(path));              // file:///home/user/project/data/logs/output.txt
 * }</pre>
 *
 * @see FileHandler
 * @see Directory
 * @author Rick M. Viana
 * @version 1.1
 * @since 2025
 */
@UtilityClass
public final class PathUtil {

    public static @NotNull Path get(@NotNull String path) {
        return Paths.get(path).normalize();
    }

    /**
     * Combines a base path with additional segments to create a new normalized {@link Path}.
     * This method uses {@link Paths#get(String, String...)} to combine the strings into a path
     * and removes any redundant elements, such as "." or "..", using {@link Path#normalize()}.
     *
     * @param first the base path segment; must not be null
     * @param more  additional path segments to combine with the base; may be empty but not null
     * @return a normalized {@link Path} representing the combined path; never null
     * @throws NullPointerException if {@code first} or {@code more} is null
     */
    public static @NotNull Path combine(@NotNull String first, @NotNull String... more) {
        return Paths.get(first, more).normalize();
    }

    /**
     * Normalizes the provided {@link Path} by removing redundant elements such as
     * "." or ".." from the path hierarchy. This method delegates the normalization
     * process to the {@link Path#normalize()} method of the input {@code Path}.
     *
     * @param path the {@link Path} to be normalized; must not be null
     * @return a normalized {@link Path} with redundant elements removed; never null
     * @throws NullPointerException if {@code path} is null
     */
    @Contract(pure = true)
    public static @NotNull Path normalize(@NotNull Path path) {
        return path.normalize();
    }

    /**
     * Retrieves the name of the file represented by the specified {@link Path}.
     * If the {@link Path} does not have a file name component, {@code null} is returned.
     *
     * @param path the {@link Path} from which to extract the file name; must not be null
     * @return the name of the file as a {@link String}, or {@code null} if there is no file name component
     * @throws NullPointerException if {@code path} is null
     */
    public static @Nullable String getFileName(@NotNull Path path) {
        Path fileName = path.getFileName();
        return (fileName != null) ? fileName.toString() : null;
    }

    /**
     * Retrieves the parent directory of the specified {@link Path}.
     * If the given path has no parent directory, returns {@code null}.
     *
     * @param path the {@link Path} for which to retrieve the parent directory; must not be null
     * @return the parent directory as a {@link String}, or {@code null} if the path has no parent directory
     * @throws NullPointerException if {@code path} is null
     */
    public static @Nullable String getParentDirectory(@NotNull Path path) {
        Path parent = path.getParent();
        return (parent != null) ? parent.toString() : null;
    }

    /**
     * Converts the provided {@link Path} to an absolute and normalized path.
     * This method ensures the returned {@link Path} is an absolute representation
     * of the input path, eliminating any redundant elements.
     *
     * @param path the {@link Path} to be converted to an absolute path; must not be null
     * @return a {@link Path} representing the absolute and normalized version of the input path; never null
     * @throws NullPointerException if {@code path} is null
     */
    public static @NotNull Path toAbsolutePath(@NotNull Path path) {
        return path.toAbsolutePath().normalize();
    }

    /**
     * Converts the specified {@link Path} to a {@link URI}.
     * This method delegates the conversion process to the {@code toUri} method
     * of the provided {@link Path} instance.
     *
     * @param path the {@link Path} to be converted to a {@link URI}; must not be null
     * @return a {@link URI} representing the given {@link Path}; never null
     * @throws NullPointerException if {@code path} is null
     */
    @Contract(pure = true)
    public static @NotNull URI toUri(@NotNull Path path) {
        return path.toUri();
    }

    /**
     * Converts a {@link Path} to a {@link URL}.
     * This method attempts to convert the provided {@code Path} object
     * to its corresponding {@link URL}. If the conversion fails, an exception
     * is thrown with a descriptive error message.
     *
     * @param path the {@link Path} to be converted to a {@link URL}; must not be null
     * @return a {@link URL} representing the given {@link Path}; never null
     * @throws IllegalArgumentException if the {@code path} cannot be converted to a {@link URL}
     * @throws NullPointerException if {@code path} is null
     */
    public static @NotNull URL toUrl(@NotNull Path path) {
        return Try.ofThrowing(() -> path.toUri().toURL())
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Failed to convert path to URL: " + throwable.getMessage(), throwable));
    }

    /**
     * Converts a {@link URI} to a {@link Path}.
     * This method attempts to create a {@link Path} object from the provided {@code URI}
     * and throws an exception if the conversion fails.
     *
     * @param uri the {@link URI} to be converted to a {@link Path}; must not be null
     * @return a {@link Path} representing the given {@link URI}; never null
     * @throws IllegalArgumentException if the URI cannot be converted to a {@link Path}
     * @throws NullPointerException if {@code uri} is null
     */
    public static @NotNull Path fromUri(@NotNull URI uri) {
        return Try.of(() -> Paths.get(uri))
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Invalid URI for path conversion", throwable));
    }

    /**
     * Converts the given file system path (as a string) to a {@link URI}.
     * This method constructs a {@link File} instance from the input string, then
     * converts it to its corresponding {@link URI}.
     *
     * @param path the file system path to be converted to a URI; must not be null
     * @return a {@link URI} representing the given path; never null
     * @throws NullPointerException if {@code path} is null
     */
    public static @NotNull URI toUri(String path) {
        return new File(Objects.requireNonNull(path, "path can't be null")).toURI();
    }

    /**
     * Converts the given file system path (as a string) to a {@link URL}.
     * This method constructs a {@link File} instance from the input string,
     * then converts it to a {@link URL}. It throws an exception if the conversion fails.
     *
     * @param path the file system path to be converted to a URL; must not be null
     * @return a {@link URL} representing the given path; never null
     * @throws IllegalArgumentException if the file path cannot be converted to a URL
     * @throws NullPointerException if {@code path} is null
     */
    public static @NotNull URL toUrl(String path) {
        return Try.ofThrowing(() -> new File(Objects.requireNonNull(path, "path can't be null")).toURI().toURL())
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Invalid file path for URL conversion", throwable));
    }

    /**
     * Converts a target path to a relative path based on a given base path.
     * This method uses the {@code relativize} method to compute the relative path
     * from the {@code base} path to the {@code target} path.
     *
     * @param base   the base path against which the relative path will be calculated; must not be null
     * @param target the target path to convert into a relative path; may be null
     * @return a {@link Path} representing the relative path from the base to the target; never null
     * @throws NullPointerException if {@code base} is null
     * @throws IllegalArgumentException if the paths cannot be relativized due to differing root components
     */
    @Contract(pure = true)
    public static @NotNull Path toRelative(@NotNull Path base, Path target) {
        return base.relativize(target);
    }

    /**
     * Determines whether the specified {@link Path} is absolute.
     * An absolute path is one that starts from the root element
     * and does not depend on the working directory or any other context.
     *
     * @param path the path to check; must not be null
     * @return {@code true} if the specified path is absolute, {@code false} otherwise
     * @throws NullPointerException if {@code path} is null
     */
    @Contract(pure = true)
    public static boolean isAbsolute(@NotNull Path path) {
        return path.isAbsolute();
    }

    /**
     * Joins two path segments into a single {@link Path} and normalizes the result.
     * The resulting path is created by combining the {@code base} path segment with the {@code child} path segment.
     *
     * @param base  the base path segment to which the child segment will be appended; must not be null
     * @param child the child path segment to append to the base; must not be null
     * @return a normalized {@link Path} representing the combined path; never null
     * @throws NullPointerException if {@code base} or {@code child} is null
     */
    public static @NotNull Path join(String base, String child) {
        return Paths.get(base, child).normalize();
    }

    public static @NotNull String getBaseFileName(Path path) {
        String fileName = getFileName(path);
        Objects.requireNonNull(fileName, "Path must not be null");
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    public static @NotNull String getBaseFileName(@NotNull String path) {
        return getBaseFileName(get(path));
    }
}
