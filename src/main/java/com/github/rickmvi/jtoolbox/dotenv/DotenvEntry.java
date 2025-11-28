package com.github.rickmvi.jtoolbox.dotenv;

/**
 * Represents a single entry in a .env file, consisting of a key and a value.
 *
 * @param key   the environment variable name
 * @param value the environment variable value
 */
public record DotenvEntry(String key, String value) {
}
