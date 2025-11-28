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
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h2>FileHandler</h2>
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
 * FileHandler
 * .at("data/output.txt")
 * .createDirectoriesIfNeeded()
 * .createIfNotExists()
 * .write("Hello, World!")
 * .append("\nMore text here.")
 * .copyTo("backup/output_copy.txt")
 * .delete();
 * }</pre>
 *
 * <h3>Example 2:</h3>
 * <pre>{@code
 * String ext = FileHandler.getExtension("report.pdf"); // "pdf"
 * String mime = FileHandler.getMimeType("report.pdf"); // "application/pdf"
 * String hash = FileHandler.getFileHash("data/output.txt", "SHA-256");
 * }</pre>
 *
 * <h3>Example 3:</h3>
 * <pre>{@code
 * // 1. Renomeia o arquivo, retornando uma nova instância FileHandler para o novo caminho
 * FileHandler renamedFile = FileHandler
 * .at("logs/temp.log")
 * .createIfNotExists()
 * .write("Line 1\nLine 2\nLine 3")
 * .renameTo("logs/app.log");
 * * // 2. Lê todas as linhas do arquivo renomeado
 * List<String> allLines = renamedFile.readAllLines(); // ["Line 1", "Line 2", "Line 3"]
 * * // 3. Processa o conteúdo como um Stream (ótimo para arquivos grandes)
 * long lineCount = renamedFile.lines().count(); // 3
 * * // 4. Limpa o novo arquivo
 * renamedFile.delete();
 * }</pre>
 *
 * @see Directory
 * @see FileWatcher
 * @see java.nio.file.Files
 *
 * @author Rick M. Viana
 * @version 1.1
 * @since 2025
 */
public class Files {

    @Getter
    private final Path path;
    private Charset charset = StandardCharsets.UTF_8;

    private Files(String filePath) {
        this.path = Paths.get(filePath);
    }

    /**
     * Creates a new {@code FileHandler} instance for the specified file path.
     * This method initializes a {@code FileHandler} to provide operations on the
     * file located at the given {@code filePath}.
     *
     * @param filePath the path to the file for which the {@code FileHandler}
     *                 instance is to be created. This should represent an
     *                 existing or intended file location.
     * @return a {@code FileHandler} instance configured for the given file path.
     * @throws NullPointerException if {@code filePath} is {@code null}.
     */
    @Contract("_ -> new")
    public static @NotNull Files at(String filePath) {
        return new Files(Objects.requireNonNull(filePath));
    }

    /**
     * Sets the character set to be used for file operations, such as reading and writing.
     * The specified {@code charset} will override any previously configured character set for this {@code FileHandler}.
     * This method follows a fluent interface design, allowing method chaining.
     *
     * @param charset the {@code Charset} to be used for file operations; must not be {@code null}.
     * @return the current {@code FileHandler} instance.
     * @throws NullPointerException if the provided {@code charset} is {@code null}.
     */
    public Files charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public Files createIfNotExists() {
        if (java.nio.file.Files.notExists(path)) {
            if (path.getParent() != null) Try.runThrowing(() -> java.nio.file.Files.createDirectories(path.getParent())).orThrow();
            Try.ofThrowing(() -> java.nio.file.Files.createFile(path)).orThrow();
        }
        return this;
    }

    public Files createDirectoriesIfNeeded() {
        if (path.getParent() != null) Try.ofThrowing(() -> java.nio.file.Files.createDirectories(path.getParent())).orThrow();
        return this;
    }

    /**
     * Writes the specified content to the file using the specified mode (overwrite or append).
     *
     * @param content the content to write.
     * @param append if true, the content is appended to the end of the file; if false, the file is overwritten.
     * @return the current {@code FileHandler} instance.
     */
    public Files write(@NotNull String content, boolean append) {
        StandardOpenOption mode = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;
        Try.runThrowing(() -> java.nio.file.Files.writeString(path, content, charset, StandardOpenOption.CREATE, mode)).orThrow();
        return this;
    }

    /**
     * Writes the specified content to the file, overwriting existing content (default behavior).
     *
     * @param content the content to write.
     * @return the current {@code FileHandler} instance.
     */
    public Files write(@NotNull String content) {
        return write(content, false);
    }

    /**
     * Appends the specified content to the end of the file.
     *
     * @param content the content to append.
     * @return the current {@code FileHandler} instance.
     */
    public Files append(@NotNull String content) {
        return write(content, true);
    }

    public String readAllText() {
        return Try.ofThrowing(() -> java.nio.file.Files.readString(path, charset)).orThrow();
    }

    public byte[] readAllBytes() {
        return Try.ofThrowing(() -> java.nio.file.Files.readAllBytes(path)).orThrow();
    }

    public List<String> readAllLines() {
        return Try.ofThrowing(() -> java.nio.file.Files.readAllLines(path, charset)).orThrow();
    }

    /**
     * Streams the lines of the file specified by the {@code path} field using the given {@code charset}.
     * This method provides a lazy and memory-efficient way to process the lines of the file as a stream.
     *
     * @return a {@link Stream} of strings, where each string represents a line from the file.
     * @throws RuntimeException if an error occurs while opening the file or reading its lines.
     */
    public Stream<String> lines() {
        return Try.ofThrowing(() -> java.nio.file.Files.lines(path, charset)).orThrow();
    }

    /**
     * Copies the current file to the specified target path.
     * The file at the target path will be replaced if it already exists.
     *
     * @param targetPath the path to which the file should be copied.
     *                   This should include the full target location, including the file name.
     * @return the current {@code FileHandler} instance.
     * @throws RuntimeException if an error occurs during the file copy operation.
     */
    public Files copyTo(String targetPath) {
        Try.runThrowing(() -> java.nio.file.Files.copy(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    /**
     * Moves the current file to the specified target path. If a file already exists at the target path,
     * it will be replaced.
     *
     * @param targetPath the path to which the file should be moved. This should be the full
     *                   target location, including file name if applicable.
     * @return the current {@code FileHandler} instance, representing the file at its new location.
     * @throws RuntimeException if an error occurs while moving the file.
     */
    public Files moveTo(String targetPath) {
        Try.runThrowing(() -> java.nio.file.Files.move(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    /**
     * Renames the file to the specified new file name within the same directory.
     * This method returns a new {@code FileHandler} instance representing the new file path.
     *
     * @param newFileName the new name for the file (e.g., "report-new.pdf").
     * @return a new {@code FileHandler} instance representing the renamed file.
     * @throws IllegalArgumentException if the file cannot be renamed.
     */
    public Files renameTo(String newFileName) {
        Path parent = path.getParent();
        Path newPath = (parent != null) ? parent.resolve(newFileName) : Paths.get(newFileName);

        Try.runThrowing(() -> java.nio.file.Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING)).orThrow();

        return Files.at(newPath.toString());
    }

    public Files delete() {
        Try.runThrowing(() -> java.nio.file.Files.deleteIfExists(path)).orThrow();
        return this;
    }

    public boolean exists() {
        return java.nio.file.Files.exists(path);
    }

    public long size() {
        return Try.ofThrowing(() -> java.nio.file.Files.size(path)).orThrow();
    }

    /**
     * Extracts and returns the file extension from the provided file name.
     * If the file name does not contain a period (`.`), an empty string is returned.
     *
     * @param fileName the name of the file from which the extension is to be extracted; must not be null.
     * @return the file extension as a non-null string. Returns an empty string if no extension is present.
     * @throws NullPointerException if {@code fileName} is null.
     */
    public static @NotNull String getExtension(@NotNull String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1);
    }

    /**
     * Determines the MIME type of a file based on its file path.
     * This method utilizes the {@code Files.probeContentType()} method to identify
     * the content type of the file.
     *
     * @param filePath the path to the file whose MIME type is to be determined.
     *                 The file path should point to an existing and accessible file.
     * @return a {@code String} representing the MIME type of the file.
     *         For example, "text/plain" or "image/jpeg".
     * @throws NullPointerException if the {@code filePath} is {@code null}.
     * @throws RuntimeException if an underlying error occurs while probing
     *         the content type, such as an I/O error or unsupported content type.
     */
    public static String getMimeType(String filePath) {
        Path path = Paths.get(filePath);
        return Try.ofThrowing(() -> java.nio.file.Files.probeContentType(path)).orThrow();
    }

    /**
     * Generates a hash value for the specified file using the given hash algorithm.
     *
     * @param filePath the path to the file whose hash is to be generated.
     *                 The file path must point to an existing, readable file.
     * @param algorithm the name of the hash algorithm to be used (e.g., "SHA-256", "MD5").
     *                  Refer to the {@link MessageDigest} documentation for supported algorithms.
     * @return a string representing the hash value of the file in hexadecimal format.
     * @throws IllegalArgumentException if an error occurs while reading the file or generating the hash,
     *                                  or if the specified algorithm is invalid.
     */
    public static String getFileHash(String filePath, String algorithm) {
        return Try.ofThrowing(() -> {
            Path path = Paths.get(filePath);
            byte[] data = java.nio.file.Files.readAllBytes(path);
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        }).orElseThrow(e -> new IllegalArgumentException("Error generating hash: " + e.getMessage(), e));
    }
}