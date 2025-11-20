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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2>Directory</h2>
 *
 * A fluent utility for working with directories using the {@link java.nio.file} API.
 * Supports creation, listing, deletion, and recursive cleanup of folders.
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * Directory
 *     .at("data/logs")
 *     .createIfNotExists()
 *     .listFiles()
 *     .deleteRecursively();
 * }</pre>
 *
 * @see FileHandler
 * @author Rick M. Viana
 * @version 1.0
 * @since 2025
 */
public class Directory {

    @Getter
    private final Path directory;

    private Directory(@NotNull String dirPath) {
        this.directory = Paths.get(dirPath);
    }

    /**
     * Creates a new {@code Directory} instance associated with the specified file path.
     * The created {@code Directory} object allows various directory operations
     * such as creation, listing its files, or deleting it recursively.
     *
     * @param dirPath the path of the directory to associate this instance with; must not be null
     * @return a {@code Directory} instance initialized with the specified path; never null
     * @throws NullPointerException if {@code dirPath} is null
     */
    @Contract("_ -> new")
    public static @NotNull Directory at(@NotNull String dirPath) {
        return new Directory(dirPath);
    }

    public Directory createIfNotExists() {
        if (Files.notExists(directory)) {
            Try.runThrowing(() -> Files.createDirectories(directory)).orThrow();
        }
        return this;
    }

    public boolean exists() {
        return Files.exists(directory);
    }

    public List<Path> listFiles() {
        try (Stream<Path> paths = Try.ofThrowing(() -> Files.list(directory)).orThrow()) {
            return paths.collect(Collectors.toList());
        }
    }

    public Directory deleteRecursively() {
        if (Files.exists(directory)) {
            try (Stream<Path> walk = Try.ofThrowing(() -> Files.walk(directory)).orThrow()) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> Try.runThrowing(() -> Files.deleteIfExists(path)));
            }
        }
        return this;
    }
    
}
