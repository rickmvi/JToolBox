package com.github.rickmvi.jtoolbox.dotenv;

import com.github.rickmvi.jtoolbox.dotenv.exceptions.DotenvException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The {@code Dotenv} interface provides methods to interact with environment variables
 * and entries from a `.env` file. This interface enables the retrieval of individual
 * environment variable values, access to all available entries, and functionality
 * to apply filters to those entries.
 */
public interface Dotenv {

    enum Filter {
        IN_ENV_FILE,
    }

    /**
     * Creates and returns a new instance of {@link DotenvBuilder} configured with default settings.
     * This builder can be further customized to define specific behaviors for loading and
     * parsing `.env` files and environment variables.
     *
     * @return a new {@link DotenvBuilder} instance with default configuration.
     */
    @Contract(value = " -> new", pure = true)
    static @NotNull DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    /**
     * Loads and returns an instance of {@link Dotenv} by parsing the environment
     * variables and the `.env` file. This method uses a default configuration and combines
     * both system environment variables and `.env` file entries into a single {@link Dotenv} instance.
     *
     * @return a {@link Dotenv} instance containing the combined environment variables from
     *         the system and `.env` file.
     * @throws DotenvException if there is an error in parsing the `.env` file, such as it being
     *         malformed, missing, or inaccessible based on the builder's configuration.
     */
    static Dotenv load() {
        return new DotenvBuilder().load();
    }

    /**
     * Retrieves a set of all available {@code DotenvEntry} objects.
     * The returned set includes both system environment variables and entries
     * defined in the .env file.
     *
     * @return a set of {@code DotenvEntry} objects where each entry represents
     *         an environment variable and its associated value.
     */
    Set<DotenvEntry> entries();

    /**
     * Retrieves a set of {@link DotenvEntry} objects based on the specified filter.
     * The filter determines which entries to include in the returned set.
     *
     * @param filter the filter criteria to apply for retrieving the environment entries.
     *               If null, all entries are returned.
     * @return a set of {@link DotenvEntry} objects matching the specified filter, or all entries if the filter is null.
     * @throws NullPointerException if the filter is non-null and an invalid operation occurs during filtering.
     */
    Set<DotenvEntry> entries(Filter filter);

    /**
     * Retrieves the value associated with the specified environment variable key.
     * If the key is not found, null is returned.
     *
     * @param key the name of the environment variable to retrieve
     * @return the value of the environment variable if it exists; otherwise, null
     * @throws NullPointerException if the key is null
     */
    String get(String key);

    /**
     * Retrieves the value associated with the specified environment variable key.
     * If the key is not found, the provided default value is returned.
     *
     * @param key the name of the environment variable to retrieve
     * @param defaultValue the value to return if the environment variable is not found
     * @return the value of the environment variable if it exists; otherwise, the default value
     * @throws NullPointerException if the key or defaultValue is null
     */
    String get(String key, String defaultValue);

}
