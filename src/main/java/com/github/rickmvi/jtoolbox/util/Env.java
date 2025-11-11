package com.github.rickmvi.jtoolbox.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utility class for managing environment variables using a dotenv configuration file.
 * Provides mechanisms to load dotenv files and retrieve environment variable values.
 */
@UtilityClass
public final class Env {

    private static Dotenv dotenv;

    @Contract(" -> new")
    public static @NotNull EnvBuilder builder() {
        return new EnvBuilder();
    }

    public static String get(String key) {
        if (dotenv == null) {
            throw new IllegalStateException("Dotenv was not loaded. Use Env.builder().load() first.");
        }
        return dotenv.get(key);
    }

    public static class EnvBuilder {
        private final DotenvBuilder dotenvBuilder;

        EnvBuilder() {
            this.dotenvBuilder = Dotenv.configure().ignoreIfMissing();
        }

        public EnvBuilder directory(String path) {
            this.dotenvBuilder.directory(Objects.requireNonNull(path, "The directory path cannot be null."));
            this.dotenvBuilder.filename(".env");
            return this;
        }

        public EnvBuilder filename(String name) {
            this.dotenvBuilder.filename(Objects.requireNonNull(name, "The file name cannot be null."));
            return this;
        }

        public Env load() {
            Env.dotenv = this.dotenvBuilder.load();
            return new Env();
        }
    }
}