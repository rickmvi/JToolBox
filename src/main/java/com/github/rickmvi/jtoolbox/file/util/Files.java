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

import com.github.rickmvi.jtoolbox.text.Stringifier;
import com.github.rickmvi.jtoolbox.util.Try;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
 * <h3>Example 1: Basic Operations</h3>
 * <pre>{@code
 * FileHandler
 *     .at("data/output.txt")
 *     .createDirectoriesIfNeeded()
 *     .createIfNotExists()
 *     .write("Hello, World!")
 *     .append("\nMore text here.")
 *     .copyTo("backup/output_copy.txt")
 *     .delete();
 * }</pre>
 *
 * <h3>Example 2: File Metadata</h3>
 * <pre>{@code
 * String ext = FileHandler.getExtension("report.pdf"); // "pdf"
 * String mime = FileHandler.getMimeType("report.pdf"); // "application/pdf"
 * String hash = FileHandler.getFileHash("data/output.txt", "SHA-256");
 * }</pre>
 *
 * <h3>Example 3: Advanced Operations</h3>
 * <pre>{@code
 * FileHandler
 *     .at("data/large.txt")
 *     .filter(line -> line.contains("ERROR"))
 *     .saveTo("errors.txt")
 *     .compress("errors.txt.gz");
 *
 * FileHandler
 *     .at("config.json")
 *     .setReadOnly()
 *     .setLastModifiedTime(Instant.now());
 * }</pre>
 *
 * @see Directory
 * @see FileWatcher
 * @see java.nio.file.Files
 *
 * @author Rick M. Viana
 * @version 2.0
 * @since 2025
 */
@SuppressWarnings("unused")
public class Files {

    @Getter
    private final Path    path;
    private       Charset charset = StandardCharsets.UTF_8;

    private Files(String filePath) {
        this.path = Paths.get(filePath);
    }

    /**
     * Creates a new {@code FileHandler} instance for the specified file path.
     */
    @Contract("_ -> new")
    public static @NotNull Files at(String filePath) {
        return new Files(Objects.requireNonNull(filePath));
    }

    /**
     * Creates a new {@code FileHandler} instance for the specified Path.
     */
    @Contract("_ -> new")
    public static @NotNull Files at(Path path) {
        return new Files(Objects.requireNonNull(path).toString());
    }

    /**
     * Sets the character set to be used for file operations.
     */
    public Files charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    // ==================== CREATION & DELETION ====================

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


    public Files create() {
        return createDirectoriesIfNeeded().createIfNotExists();
    }

    public Files delete() {
        Try.runThrowing(() -> java.nio.file.Files.deleteIfExists(path)).orThrow();
        return this;
    }

    public Files deleteIf(@NotNull Predicate<Path> condition) {
        if (condition.test(path)) {
            delete();
        }
        return this;
    }

    public Files clear() {
        return write("");
    }

    public Files write(@NotNull String content, boolean append) {
        StandardOpenOption mode = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;
        Try.runThrowing(() -> java.nio.file.Files.writeString(path, content, charset, StandardOpenOption.CREATE, mode)).orThrow();
        return this;
    }

    public Files write(@NotNull String content) {
        return write(content, false);
    }

    public Files append(@NotNull String content) {
        return write(content, true);
    }

    public Files writeLines(List<String> lines) {
        Try.runThrowing(() -> java.nio.file.Files.write(path, lines, charset)).orThrow();
        return this;
    }

    public Files writeBytes(byte[] bytes) {
        Try.runThrowing(() -> java.nio.file.Files.write(path, bytes)).orThrow();
        return this;
    }

    public Files writeObject(Serializable object) {
        Try.runThrowing(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(java.nio.file.Files.newOutputStream(path))) {
                oos.writeObject(object);
            }
        }).orThrow();
        return this;
    }

    // ==================== READ OPERATIONS ====================

    public String readAllText() {
        return Try.ofThrowing(() -> java.nio.file.Files.readString(path, charset)).orThrow();
    }

    public byte[] readAllBytes() {
        return Try.ofThrowing(() -> java.nio.file.Files.readAllBytes(path)).orThrow();
    }

    public List<String> readAllLines() {
        return Try.ofThrowing(() -> java.nio.file.Files.readAllLines(path, charset)).orThrow();
    }

    public Stream<String> lines() {
        return Try.ofThrowing(() -> java.nio.file.Files.lines(path, charset)).orThrow();
    }

    /**
     * Reads an object from the file using deserialization.
     */
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> type) {
        return Try.ofThrowing(() -> {
            try (ObjectInputStream ois = new ObjectInputStream(java.nio.file.Files.newInputStream(path))) {
                return (T) ois.readObject();
            }
        }).orThrow();
    }

    /**
     * Reads the first N lines from the file.
     */
    public List<String> readFirstLines(int n) {
        return lines().limit(n).collect(Collectors.toList());
    }

    /**
     * Reads the last N lines from the file (useful for log files).
     */
    public List<String> readLastLines(int n) {
        List<String> allLines = readAllLines();
        int size = allLines.size();
        return allLines.subList(Math.max(0, size - n), size);
    }

    // ==================== FILTERING & PROCESSING ====================

    /**
     * Filters lines based on a predicate and returns them as a list.
     */
    public List<String> filter(Predicate<String> predicate) {
        return lines().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Filters lines and saves them to a new file.
     */
    public Files filterAndSave(Predicate<String> predicate, String targetPath) {
        List<String> filtered = filter(predicate);
        Files.at(targetPath).writeLines(filtered);
        return this;
    }

    /**
     * Searches for lines matching a regex pattern.
     */
    public List<String> grep(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return filter(line -> pattern.matcher(line).find());
    }

    /**
     * Replaces all occurrences of a pattern with a replacement string.
     */
    public Files replaceAll(String regex, String replacement) {
        String content = readAllText();
        String replaced = content.replaceAll(regex, replacement);
        return write(replaced);
    }

    /**
     * Processes each line with a consumer function.
     */
    public Files processLines(Consumer<String> processor) {
        lines().forEach(processor);
        return this;
    }

    /**
     * Counts the number of lines in the file.
     */
    public long countLines() {
        return lines().count();
    }

    /**
     * Counts occurrences of a specific string in the file.
     */
    public long count(String searchString) {
        return lines()
                .mapToLong(line -> (line.length() - line.replace(searchString, "").length()) / searchString.length())
                .sum();
    }

    // ==================== FILE OPERATIONS ====================

    public Files copyTo(String targetPath) {
        Try.runThrowing(() -> java.nio.file.Files.copy(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    /**
     * Copies the file only if the target doesn't exist.
     */
    public Files copyToIfNotExists(String targetPath) {
        Path target = Paths.get(targetPath);
        if (java.nio.file.Files.notExists(target)) {
            copyTo(targetPath);
        }
        return this;
    }

    public Files moveTo(String targetPath) {
        Try.runThrowing(() -> java.nio.file.Files.move(path, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return this;
    }

    public Files renameTo(String newFileName) {
        Path parent = path.getParent();
        Path newPath = (parent != null) ? parent.resolve(newFileName) : Paths.get(newFileName);
        Try.runThrowing(() -> java.nio.file.Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING)).orThrow();
        return Files.at(newPath.toString());
    }

    /**
     * Creates a backup copy of the file with a timestamp.
     */
    public Files backup() {
        String backupName = Stringifier.format("{}.{}.bak", getFileName(), System.currentTimeMillis());
        copyTo(path.getParent().resolve(backupName).toString());
        return this;
    }

    /**
     * Creates a hard link to this file.
     */
    public Files createHardLink(String linkPath) {
        Try.runThrowing(() -> java.nio.file.Files.createLink(Paths.get(linkPath), path)).orThrow();
        return this;
    }

    /**
     * Creates a symbolic link to this file.
     */
    public Files createSymbolicLink(String linkPath) {
        Try.runThrowing(() -> java.nio.file.Files.createSymbolicLink(Paths.get(linkPath), path)).orThrow();
        return this;
    }

    // ==================== COMPRESSION ====================

    /**
     * Compresses the file using GZIP.
     */
    public Files compress(String targetPath) {
        Try.runThrowing(() -> {
            try (InputStream in = java.nio.file.Files.newInputStream(path);
                 GZIPOutputStream out = new GZIPOutputStream(java.nio.file.Files.newOutputStream(Paths.get(targetPath)))) {
                in.transferTo(out);
            }
        }).orThrow();
        return this;
    }

    /**
     * Decompresses a GZIP file.
     */
    public Files decompress(String targetPath) {
        Try.runThrowing(() -> {
            try (GZIPInputStream in = new GZIPInputStream(java.nio.file.Files.newInputStream(path));
                 OutputStream out = java.nio.file.Files.newOutputStream(Paths.get(targetPath))) {
                in.transferTo(out);
            }
        }).orThrow();
        return this;
    }

    // ==================== ATTRIBUTES & METADATA ====================

    public boolean exists() {
        return java.nio.file.Files.exists(path);
    }

    public long size() {
        return Try.ofThrowing(() -> java.nio.file.Files.size(path)).orThrow();
    }

    /**
     * Returns human-readable file size (e.g., "1.5 MB").
     */
    public String sizeFormatted() {
        long size = size();
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }

    public boolean isRegularFile() {
        return java.nio.file.Files.isRegularFile(path);
    }

    public boolean isDirectory() {
        return java.nio.file.Files.isDirectory(path);
    }

    public boolean isSymbolicLink() {
        return java.nio.file.Files.isSymbolicLink(path);
    }

    public boolean isHidden() {
        return Try.ofThrowing(() -> java.nio.file.Files.isHidden(path)).orElse(false);
    }

    public boolean isReadable() {
        return java.nio.file.Files.isReadable(path);
    }

    public boolean isWritable() {
        return java.nio.file.Files.isWritable(path);
    }

    public boolean isExecutable() {
        return java.nio.file.Files.isExecutable(path);
    }

    /**
     * Gets the file name without path.
     */
    public String getFileName() {
        return path.getFileName().toString();
    }

    /**
     * Gets the file name without extension.
     */
    public String getFileNameWithoutExtension() {
        String name = getFileName();
        int dotIndex = name.lastIndexOf('.');
        return dotIndex > 0 ? name.substring(0, dotIndex) : name;
    }

    /**
     * Gets the file extension.
     */
    public String getExtension() {
        return getExtension(getFileName());
    }

    /**
     * Gets the absolute path as a string.
     */
    public String getAbsolutePath() {
        return path.toAbsolutePath().toString();
    }

    /**
     * Gets the parent directory path.
     */
    public String getParent() {
        Path parent = path.getParent();
        return parent != null ? parent.toString() : null;
    }

    /**
     * Gets the last modified time.
     */
    public Instant getLastModifiedTime() {
        return Try.ofThrowing(() -> java.nio.file.Files.getLastModifiedTime(path).toInstant()).orThrow();
    }

    /**
     * Sets the last modified time.
     */
    public Files setLastModifiedTime(Instant instant) {
        Try.runThrowing(() -> java.nio.file.Files.setLastModifiedTime(path, FileTime.from(instant))).orThrow();
        return this;
    }

    /**
     * Gets the creation time.
     */
    public Instant getCreationTime() {
        return Try.ofThrowing(() -> {
            BasicFileAttributes attrs = java.nio.file.Files.readAttributes(path, BasicFileAttributes.class);
            return attrs.creationTime().toInstant();
        }).orThrow();
    }

    /**
     * Gets the owner of the file.
     */
    public String getOwner() {
        return Try.ofThrowing(() -> java.nio.file.Files.getOwner(path).getName()).orThrow();
    }

    /**
     * Sets the owner of the file.
     */
    public Files setOwner(String owner) {
        Try.runThrowing(() -> {
            UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(owner);
            java.nio.file.Files.setOwner(path, user);
        }).orThrow();
        return this;
    }

    /**
     * Gets POSIX file permissions (Unix/Linux).
     */
    public Set<PosixFilePermission> getPosixPermissions() {
        return Try.ofThrowing(() -> java.nio.file.Files.getPosixFilePermissions(path)).orThrow();
    }

    /**
     * Sets POSIX file permissions (Unix/Linux).
     */
    public Files setPosixPermissions(Set<PosixFilePermission> permissions) {
        Try.runThrowing(() -> java.nio.file.Files.setPosixFilePermissions(path, permissions)).orThrow();
        return this;
    }

    /**
     * Sets the file to read-only.
     */
    public Files setReadOnly() {
        Try.runThrowing(() -> {
            File file = path.toFile();
            file.setWritable(false);
            file.setExecutable(false);
        }).orThrow();
        return this;
    }

    /**
     * Makes the file writable.
     */
    public Files setWritable() {
        Try.runThrowing(() -> path.toFile().setWritable(true)).orThrow();
        return this;
    }

    /**
     * Makes the file executable.
     */
    public Files setExecutable() {
        Try.runThrowing(() -> path.toFile().setExecutable(true)).orThrow();
        return this;
    }

    // ==================== COMPARISON ====================

    /**
     * Compares this file with another file byte by byte.
     */
    public boolean contentEquals(String otherPath) {
        return Try.ofThrowing(() -> {
            byte[] content1 = readAllBytes();
            byte[] content2 = Files.at(otherPath).readAllBytes();
            return Arrays.equals(content1, content2);
        }).orElse(false);
    }

    /**
     * Checks if this file is newer than another file.
     */
    public boolean isNewerThan(String otherPath) {
        return getLastModifiedTime().isAfter(Files.at(otherPath).getLastModifiedTime());
    }

    /**
     * Checks if this file is older than another file.
     */
    public boolean isOlderThan(String otherPath) {
        return getLastModifiedTime().isBefore(Files.at(otherPath).getLastModifiedTime());
    }

    // ==================== STATIC UTILITIES ====================

    public static @NotNull String getExtension(@NotNull String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1);
    }

    public static String getMimeType(String filePath) {
        Path path = Paths.get(filePath);
        return Try.ofThrowing(() -> java.nio.file.Files.probeContentType(path)).orThrow();
    }

    public static String getFileHash(String filePath, String algorithm) {
        return Try.ofThrowing(() -> {
            Path path = Paths.get(filePath);
            byte[] data = java.nio.file.Files.readAllBytes(path);
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        }).orElseThrow(e -> new IllegalArgumentException("Error generating hash: " + e.getMessage(), e));
    }

    /**
     * Creates a temporary file.
     */
    public static Files createTemp(String prefix, String suffix) {
        return Try.ofThrowing(() -> {
            Path temp = java.nio.file.Files.createTempFile(prefix, suffix);
            return Files.at(temp);
        }).orThrow();
    }

    /**
     * Checks if two files are the same (same inode on Unix).
     */
    public static boolean isSameFile(String path1, String path2) {
        return Try.ofThrowing(() -> java.nio.file.Files.isSameFile(Paths.get(path1), Paths.get(path2))).orElse(false);
    }
}