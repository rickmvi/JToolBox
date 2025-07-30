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
package com.github.rickmvi.jtoolbox.console.internal;

import com.github.rickmvi.jtoolbox.console.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Interface defining the contract for input scanners capable of reading various types
 * of input data from a source, typically the console or another input stream.
 * <p>
 * This interface abstracts the behavior of input reading mechanisms, allowing
 * initialization with default or custom {@link java.util.Scanner} instances,
 * locale configuration, and retrieval of input tokens of various primitive types
 * and strings.
 * <p>
 * It also provides methods to safely check for the presence of input data and
 * to close the underlying scanner resource when no longer needed.
 */
public interface InputScanner {

    /**
     * Initializes the scanner using the default input source (usually {@code System.in}).
     * Must be called before any reading methods to prepare the scanner.
     */
    void init();

    /**
     * Initializes the scanner using the provided {@link java.util.Scanner} instance.
     * Allows using a custom input source.
     *
     * @param scanner the scanner instance to use for input reading
     */
    void init(@NotNull java.util.Scanner scanner);

    /**
     * Sets the locale for the scanner to use during parsing of locale-sensitive values.
     *
     * @param location the locale setting to apply, represented by {@link Location}
     */
    void locale(@NotNull Location location);

    /**
     * Checks if there is another token available in the input.
     *
     * @return {@code true} if another token is available, {@code false} otherwise
     */
    boolean hasNext();

    /**
     * Checks if there is another line of input available.
     *
     * @return {@code true} if another line is available, {@code false} otherwise
     */
    boolean hasNextLine();

    /**
     * Retrieves the next token from the input.
     *
     * @return the next token as a {@link String}
     */
    String next();

    /**
     * Retrieves the next token from the input that matches the specified pattern.
     *
     * @param pattern a regular expression pattern the next token must match
     * @return the next matching token as a {@link String}
     */
    String next(@NotNull String pattern);

    /**
     * Retrieves the next entire line of input.
     *
     * @return the next line as a {@link String}
     */
    String nextLine();

    /**
     * Parses and returns the next token as an {@code int}.
     *
     * @return the next integer value read from input
     */
    int nextInt();

    /**
     * Parses and returns the next token as a {@code long}.
     *
     * @return the next long value read from input
     */
    long nextLong();

    /**
     * Parses and returns the next token as a {@code float}.
     *
     * @return the next float value read from input
     */
    float nextFloat();

    /**
     * Parses and returns the next token as a {@code double}.
     *
     * @return the next double value read from input
     */
    double nextDouble();

    /**
     * Parses and returns the next token as a {@code boolean}.
     *
     * @return the next boolean value read from input
     */
    boolean nextBoolean();

    /**
     * Retrieves the next token as a string safely, returning an empty string in case
     * of errors or if no token is available.
     *
     * @return the next token as a {@link String}, or an empty string if not available or error occurs
     */
    String nextSafe();

    /**
     * Closes the underlying scanner and releases any associated resources.
     * Should be called when input reading is complete.
     */
    void close();
}