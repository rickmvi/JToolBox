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
package com.github.rickmvi.jtoolbox.file;

import com.github.rickmvi.jtoolbox.file.util.Directory;
import com.github.rickmvi.jtoolbox.file.util.FileUtil;
import com.github.rickmvi.jtoolbox.file.util.PathUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * A central utility interface providing a collection of static, user-friendly methods
 * for common file system and path manipulation operations.
 * <p>
 * This interface acts as a facade, aggregating functionalities from core utilities
 * such as {@link PathUtil}, {@link FileUtil}, and {@link Directory}, offering
 * a simplified and expressive entry point for file-related tasks.
 *
 * @see PathUtil
 * @see FileUtil
 * @see Directory
 * @author Rick M. Viana
 * @version 1.0
 * @since 2025
 */
public interface File {

    /**
     * Creates a {@link FileUtil} object for the specified file path.
     *
     * @param filePath the path of the file for which the {@code FileUtil} object is to be created.
     *                 This must not be {@code null}.
     * @return a {@code FileUtil} instance representing the specified file.
     * @throws IllegalArgumentException if the given file path is invalid or cannot be resolved.
     */
    @Contract("_ -> new")
    static @NotNull FileUtil at(String filePath) {
        return FileUtil.at(filePath);
    }

    /**
     * Creates a {@link Directory} object for the specified directory path.
     *
     * @param dirPath the path of the directory for which the {@code Directory} object is to be created.
     *                This must not be {@code null}.
     * @return a {@code Directory} instance representing the specified directory.
     * @throws IllegalArgumentException if the given directory path is invalid or cannot be resolved.
     */
    @Contract("_ -> new")
    static @NotNull Directory directory(String dirPath) {
        return Directory.at(dirPath);
    }

    /**
     * Retrieves a {@link Path} object for the specified string representation of a file or directory path.
     *
     * @param path the string representation of the file or directory path.
     *             This must not be {@code null}.
     * @return a {@link Path} object representing the specified path.
     * @throws IllegalArgumentException if the specified path is invalid or cannot be resolved.
     */
    static @NotNull Path get(String path) {
        return PathUtil.get(path);
    }

    /**
     * Combines one or more path segments into a single normalized {@link Path}.
     *
     * @param first the initial path segment. This segment must not be {@code null}.
     * @param more additional path segments to combine with the first segment. These segments
     *             must not be {@code null}.
     * @return a {@link Path} instance representing the combined and normalized result of the given path segments.
     * @throws IllegalArgumentException if any of the path segments are invalid or cannot be resolved.
     * @throws NullPointerException if the {@code first} parameter or any of the {@code more} parameters are {@code null}.
     */
    static @NotNull Path combine(String first, String... more) {
        return PathUtil.combine(first, more);
    }

    /**
     * Converts the provided {@link Path} into an absolute path.
     * If the given path is already absolute, it will be normalized and returned.
     * Otherwise, it will be converted into an absolute path relative to
     * the current working directory and then normalized.
     *
     * @param path the {@link Path} to be converted to an absolute path. This must not be {@code null}.
     * @return the absolute and normalized form of the given {@link Path}.
     * @throws NullPointerException if the {@code path} parameter is {@code null}.
     */
    static @NotNull Path toAbsolutePath(Path path) {
        return PathUtil.toAbsolutePath(path);
    }

    /**
     * Extracts the file name from the given {@link Path}.
     * This method uses {@link PathUtil#getFileName(Path)} internally to retrieve the file name.
     *
     * @param path the {@link Path} from which to extract the file name.
     *             This must not be {@code null}.
     * @return the name of the file as a {@code String}, or {@code null} if the given path
     *         does not have a file name (e.g., if the path represents a root directory).
     * @throws NullPointerException if the {@code path} parameter is {@code null}.
     */
    static String getFileName(Path path) {
        return PathUtil.getFileName(path);
    }

    /**
     * Extracts the file extension from the specified file name.
     * The file extension is defined as the substring following the last period ('.')
     * in the provided file name. If the file name does not contain a period,
     * an empty string will be returned.
     *
     * @param fileName the name of the file from which to extract the extension.
     *                 This must not be {@code null}.
     * @return the file extension as a {@code String}, or an empty string if the file name
     *         does not contain an extension.
     * @throws NullPointerException if the {@code fileName} parameter is {@code null}.
     */
    static @NotNull String getExtension(String fileName) {
        return FileUtil.getExtension(fileName);
    }

    /**
     * Generates a hash for the specified file using the provided hashing algorithm.
     * The hash is computed based on the file's content and returned as a hexadecimal string.
     *
     * @param filePath the path to the file for which the hash is to be generated.
     *                 This must not be {@code null} or invalid.
     * @param algorithm the name of the hashing algorithm to use (e.g., "MD5", "SHA-256").
     *                  This must not be {@code null} or unsupported.
     * @return a {@code String} representing the computed hash in hexadecimal format.
     * @throws IllegalArgumentException if the file path is invalid, unreadable, or if the
     *                                  specified algorithm is unsupported.
     */
    static String getFileHash(String filePath, String algorithm) {
        return FileUtil.getFileHash(filePath, algorithm);
    }

    /**
     * Reads the content of a file specified by the given file path.
     *
     * @param filePath the path to the file to be read. This must not be {@code null}
     *                 or reference an invalid file path.
     * @return a {@code String} containing the entire content of the file.
     * @throws IllegalArgumentException if the file path is invalid or the file cannot be read.
     */
    static String read(String filePath) {
        return FileUtil.at(filePath).readAllText();
    }

    /**
     * Reads the content of the specified file and returns it as a byte array.
     *
     * @param filePath the path to the file to be read. This must not be {@code null}
     *                 or reference an invalid file path.
     * @return a byte array containing the content of the specified file.
     * @throws IllegalArgumentException if the file path is invalid or the file cannot be read.
     */
    static byte[] readBytes(String filePath) {
        return FileUtil.at(filePath).readAllBytes();
    }

    /**
     * Writes the specified content to the file at the given file path.
     * If the file does not exist, it will be created. If it already exists,
     * its contents will be overwritten with the specified content.
     *
     * @param filePath the path to the file where the content is to be written.
     *                 This must not be {@code null} or reference an invalid file path.
     * @param content the content to be written to the file. This must not be {@code null}.
     * @throws IllegalArgumentException if the file path is invalid or cannot be resolved.
     */
    static void write(String filePath, String content) {
        FileUtil.at(filePath).write(content);
    }

}
