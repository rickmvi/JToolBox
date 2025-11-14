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
 * @see FileUtil
 * @see Directory
 * @author Rick M. Viana
 * @version 1.0
 * @since 2025
 */
@UtilityClass
public final class PathUtil {

    public static @NotNull Path get(String path) {
        return Paths.get(path).normalize();
    }

    public static @NotNull Path combine(String first, String... more) {
        return Paths.get(first, more).normalize();
    }

    @Contract(pure = true)
    public static @NotNull Path normalize(@NotNull Path path) {
        return path.normalize();
    }

    public static @Nullable String getFileName(@NotNull Path path) {
        Path fileName = path.getFileName();
        return (fileName != null) ? fileName.toString() : null;
    }

    public static @Nullable String getParentDirectory(@NotNull Path path) {
        Path parent = path.getParent();
        return (parent != null) ? parent.toString() : null;
    }

    public static @NotNull Path toAbsolutePath(@NotNull Path path) {
        return path.toAbsolutePath().normalize();
    }

    @Contract(pure = true)
    public static @NotNull URI toUri(@NotNull Path path) {
        return path.toUri();
    }

    public static @NotNull URL toUrl(Path path) {
        return Try.ofThrowing(() -> path.toUri().toURL())
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Failed to convert path to URL: " + throwable.getMessage(), throwable));
    }

    public static @NotNull Path fromUri(URI uri) {
        return Try.of(() -> Paths.get(uri))
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Invalid URI for path conversion", throwable));
    }

    public static @NotNull URI toUri(String path) {
        return new File(path).toURI();
    }

    public static @NotNull URL toUrl(String path) {
        return Try.ofThrowing(() -> new File(path).toURI().toURL())
                .orElseThrow(throwable ->
                        new IllegalArgumentException("Invalid file path for URL conversion", throwable));
    }

    @Contract(pure = true)
    public static @NotNull Path toRelative(@NotNull Path base, Path target) {
        return base.relativize(target);
    }

    @Contract(pure = true)
    public static boolean isAbsolute(@NotNull Path path) {
        return path.isAbsolute();
    }

    public static @NotNull Path join(String base, String child) {
        return Paths.get(base, child).normalize();
    }

    public static @Nullable String getFileNameWithoutExtension(Path path) {
        String fileName = getFileName(path);
        if (fileName == null) return null;
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
