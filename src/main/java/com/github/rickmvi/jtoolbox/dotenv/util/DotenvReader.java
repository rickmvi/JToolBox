package com.github.rickmvi.jtoolbox.dotenv.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

/**
 * A class responsible for reading `.env` configuration files from a specified
 * directory and filename. Supports loading files from either the filesystem
 * or the classpath, with automatic directory normalization and handling of
 * file location paths.
 */
@RequiredArgsConstructor
public class DotenvReader {

    private final String directory;
    private final String filename;

    private static final String BACKSLASH_PATTERN = "\\\\";
    private static final String FORWARD_SLASH = "/";
    private static final String DOTENV_EXTENSION_PATTERN = "\\.env$";
    private static final String EMPTY_STRING = "";
    private static final String TRAILING_SLASH_PATTERN = "/$";

    public List<String> read() throws IOException {
        String normalizedDir = normalizeDirectory(directory);
        String location = normalizedDir + filename;

        Path path = getPath(location).orElseThrow();
        if (Files.exists(path)) {
            return Files.readAllLines(path);
        }

        return ClasspathResourceLoader.loadFileFromClasspath(location.replaceFirst("^\\./", "/")).toList();
    }

    private @NotNull String normalizeDirectory(String dir) {
        return dir.replaceAll(BACKSLASH_PATTERN, FORWARD_SLASH)
                .replaceAll(DOTENV_EXTENSION_PATTERN, EMPTY_STRING)
                .replaceFirst(TRAILING_SLASH_PATTERN, EMPTY_STRING)
                + FORWARD_SLASH;
    }

    @Contract("_ -> new")
    private @NotNull Optional<Path> getPath(String location) {
        return Optional.of(
                location.startsWith("file:") ||
                location.startsWith("jimfs:") ||
                location.startsWith("android.resource:")
                ? Paths.get(URI.create(location))
                : Paths.get(location)
        );
    }

}
