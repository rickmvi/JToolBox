package com.github.rickmvi.jtoolbox.dotenv.util;

import com.github.rickmvi.jtoolbox.dotenv.exceptions.DotenvException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

@UtilityClass
public final class ClasspathResourceLoader {

    static @NotNull Stream<String> loadFileFromClasspath(String location) {
        Objects.requireNonNull(location, "location must not be null");

        InputStream inputStream = getResourceAsStream(location);
        if (inputStream == null) {
            throw new DotenvException("Could not find " + location + " on the classpath");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().toList().stream();
        } catch (IOException e) {
            throw new DotenvException("Failed to read file: " + location, e);
        }

    }

    private static InputStream getResourceAsStream(String location) {
        InputStream inputStream = ClasspathResourceLoader.class.getResourceAsStream(location);
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(location);
        }
        return inputStream;
    }

}
