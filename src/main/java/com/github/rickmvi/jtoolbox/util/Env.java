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
package com.github.rickmvi.jtoolbox.util;

import io.github.cdimascio.dotenv.DotenvBuilder;
import com.github.rickmvi.jtoolbox.control.If;
import io.github.cdimascio.dotenv.Dotenv;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility class for managing and accessing application environment variables,
 * primarily loaded from a {@code .env} file using the Dotenv library.
 * <p>
 * This class provides a centralized mechanism for configuring the application
 * by either using a fluid {@link EnvBuilder} for custom loading paths/files,
 * or using the static access method {@link #get(String)} after loading.
 * The underlying library is configured to ignore the .env file if it is missing.
 *
 * @author Rick M. Viana
 * @version 1.3
 * @since 2025
 */
@UtilityClass
public final class Env {

    private static Dotenv                     dotenv;
    private static final String              FAILURE;
    private static final String DEFAULT_ENV_FILENAME;

    static {
        FAILURE = "Dotenv was not loaded. Use Env.builder().directory(...).filename(...).load() first.";
        DEFAULT_ENV_FILENAME = ".env";
    }

    /**
     * Creates a new {@link EnvBuilder} instance to configure and load environment variables.
     *
     * @return a new, non-null {@code EnvBuilder} instance.
     */
    @Contract(" -> new")
    public static @NotNull EnvBuilder builder() {
        return new EnvBuilder();
    }

    /**
     * Retrieves the value of the environment variable specified by the key.
     *
     * @param key the name of the environment variable.
     * @return the value of the environment variable as a String.
     * @throws IllegalStateException if the Dotenv configuration has not been loaded yet
     * (i.e., if {@code builder().load()} or {@code builder().fastload()}
     * has not been called).
     */
    public static String get(String key) {
        ensureDotenvLoaded();
        return dotenv.get(Objects.requireNonNull(key));
    }

    /**
     * Retrieves an {@link Optional} containing the value of the environment variable
     * specified by the provided key, or an empty {@link Optional} if the variable
     * is not found or the value is null.
     *
     * @param key the name of the environment variable to retrieve. Must not be null.
     * @return an {@link Optional} containing the variable value if it exists and is not null,
     *         otherwise an empty {@link Optional}.
     * @throws IllegalStateException if the environment configuration has not been loaded.
     * @throws NullPointerException if the provided key is null.
     */
    public static @NotNull Optional<String> optional(String key) {
        ensureDotenvLoaded();
        return Optional.ofNullable(dotenv.get(Objects.requireNonNull(key)));
    }

    private static void ensureDotenvLoaded() {
        If.ThrowWhen(dotenv == null, () -> new IllegalStateException(FAILURE));
    }

    /**
     * Builder class for configuring and loading environment variables from a .env file.
     * <p>
     * Allows customization of the directory and file name before loading.
     */
    public static class EnvBuilder {

        @Getter(value = AccessLevel.PRIVATE)
        private final DotenvBuilder dotenvBuilder;

        EnvBuilder() {
            this.dotenvBuilder = Dotenv.configure().ignoreIfMissing();
        }

        /**
         * Specifies the directory path where the .env file is located.
         * Default file name is set to ".env".
         *
         * @param path the path to the directory containing the .env file. Cannot be null.
         * @return the current builder instance for method chaining.
         * @throws NullPointerException if the directory path is null.
         */
        public EnvBuilder directory(String path) {
            this.getDotenvBuilder().directory(Objects.requireNonNull(path, "The directory path cannot be null."));
            this.getDotenvBuilder().filename(DEFAULT_ENV_FILENAME);
            return this;
        }

        /**
         * Specifies the name of the environment file to load (e.g., ".env.test").
         *
         * @param name the file name. Cannot be null.
         * @return the current builder instance for method chaining.
         * @throws NullPointerException if the file name is null.
         */
        public EnvBuilder filename(String name) {
            this.getDotenvBuilder().filename(Objects.requireNonNull(name, "The file name cannot be null."));
            return this;
        }

        /**
         * Loads the environment variables using the current configuration.
         * This method must be called to initialize the {@code Env} utility class.
         */
        public void load() {
            Env.dotenv = this.getDotenvBuilder().load();
        }

        /**
         * A shortcut method to load the default ".env" file located in the current
         * directory ("./").
         * This method is equivalent to {@code Env.builder().directory("./").filename(".env").load()}.
         */
        public void fastload() {
            Env.dotenv = this.getDotenvBuilder()
                    .directory("./")
                    .filename(DEFAULT_ENV_FILENAME)
                    .load();
        }
    }
}