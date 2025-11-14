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
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

/**
 * <h2>FileUtil</h2>
 *
 * A fluent and beginner-friendly utility class for managing file operations in Java using
 * the {@link java.nio.file} API. It provides a clean, chainable interface for common
 * tasks such as creating, reading, writing, appending, copying, moving, and deleting files.
 *
 * <p>This class simplifies file handling by wrapping NIO methods in a high-level API.
 * Designed to be part of the JToolbox library for easy, modern file manipulation.</p>
 *
 * <h3>Example 1:</h3>
 * <pre>{@code
 * FileUtil
 *     .at("data/output.txt")
 *     .createDirectoriesIfNeeded()
 *     .createIfNotExists()
 *     .write("Hello, World!")
 *     .append("\nMore text here.")
 *     .copyTo("backup/output_copy.txt")
 *     .delete();
 * }</pre>
 *
 * <h3>Example 2:</h3>
 * <pre>{@code
 * String ext = FileUtil.getExtension("report.pdf"); // "pdf"
 * String mime = FileUtil.getMimeType("report.pdf"); // "application/pdf"
 * String hash = FileUtil.getFileHash("data/output.txt", "SHA-256");
 * }</pre>
 *
 * @see Directory
 * @see FileWatcher
 * @see Files
 * @author Rick M. Viana
 * @version 1.0
 * @since 2025
 */
public class FileUtil {

    @Getter
    private final Path path;
    private Charset charset = StandardCharsets.UTF_8;

    private FileUtil(String filePath) {
        this.path = Paths.get(filePath);
    }

    @Contract("_ -> new")
    public static @NotNull FileUtil at(String filePath) {
        return new FileUtil(filePath);
    }

    public FileUtil charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public FileUtil createIfNotExists() {
        if (Files.notExists(path)) {
            if (path.getParent() != null) {
                Try.runThrowing(() -> Files.createDirectories(path.getParent())).orThrow();
            }
            Try.ofThrowing(() -> Files.createFile(path)).orThrow();
        }
        return this;
    }

    public FileUtil createDirectoriesIfNeeded() {
        if (path.getParent() != null) {
            Try.ofThrowing(() -> Files.createDirectories(path.getParent())).orThrow();
        }
        return this;
    }

    public FileUtil write(@NotNull String content) {
        Try.runThrowing(() -> Files.writeString(path, content, charset,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)).orThrow();
        return this;
    }

    public FileUtil append(@NotNull String content) {
        Try.runThrowing(() -> Files.writeString(path, content, charset,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)).orThrow();
        return this;
    }

    public String readAllText() {
        return Try.ofThrowing(() -> Files.readString(path, charset)).orThrow();
    }

    public byte[] readAllBytes() {
        return Try.ofThrowing(() -> Files.readAllBytes(path)).orThrow();
    }

    public List<String> readAllLines() {
        return Try.ofThrowing(() -> Files.readAllLines(path, charset)).orThrow();
    }

    public Stream<String> lines() {
        return Try.ofThrowing(() -> Files.lines(path, charset)).orThrow();
    }

    public FileUtil copyTo(String targetPath) {
        Try.runThrowing(() -> Files.copy(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    public FileUtil moveTo(String targetPath) {
        Try.runThrowing(() -> Files.move(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    public FileUtil delete() {
        Try.runThrowing(() -> Files.deleteIfExists(path)).orThrow();
        return this;
    }

    public boolean exists() {
        return Files.exists(path);
    }

    public long size() {
        return Try.ofThrowing(() -> Files.size(path)).orThrow();
    }

    public static @NotNull String getExtension(@NotNull String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1);
    }

    public static String getMimeType(String filePath) {
        Path path = Paths.get(filePath);
        return Try.ofThrowing(() -> Files.probeContentType(path)).orThrow();
    }

    public static String getFileHash(String filePath, String algorithm) {
        return Try.ofThrowing(() -> {
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        }).orElseThrow(e -> new IllegalArgumentException("Error generating hash: " + e.getMessage(), e));
    }

}
