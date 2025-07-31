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

import com.github.rickmvi.jtoolbox.console.convert.StringToBoolean;
import com.github.rickmvi.jtoolbox.console.convert.StringToNumber;
import com.github.rickmvi.jtoolbox.console.Location;

import static com.github.rickmvi.jtoolbox.debug.SLogger.warn;
import com.github.rickmvi.jtoolbox.control.Conditionals;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Scanner;

/**
 * Internal implementation of the {@link InputScanner} interface.
 * <p>
 * This class handles low-level operations for console input using {@link Scanner},
 * including validation, localization, safe reading, and type conversion.
 * It provides robust handling of user input, encapsulating common input
 * operations and converting raw strings into typed values.
 * <p>
 * This class is used internally by the public-facing {@link com.github.rickmvi.jtoolbox.console.ScannerUtils}.
 */
public class ScannerHandler implements InputScanner {

    /**
     * Internal {@link Scanner} instance wrapped in an {@link Optional}.
     * Ensures that initialization is checked before each operation.
     */
    @lombok.Getter
    @lombok.Setter(value = lombok.AccessLevel.PRIVATE)
    private Optional<Scanner> scanner = Optional.empty();

    /**
     * Initializes the internal scanner using {@code System.in}.
     * This must be called before any input operation.
     */
    @Override
    public void init() {
        scanner = Optional.of(new Scanner(System.in));
    }

    /**
     * Initializes the internal scanner using a custom {@link Scanner} instance.
     *
     * @param scanner a non-null scanner to be used for input
     */
    @Override
    public void init(@NotNull Scanner scanner) {
        this.scanner = Optional.of(scanner);
    }

    /**
     * Configures the locale of the scanner, affecting number and date parsing behavior.
     *
     * @param location the desired locale setting
     */
    @Override
    public void locale(@NotNull Location location) {
        validate();
        scanner.ifPresent(sc -> {
            switch (location) {
                case US ->      sc.useLocale(java.util.Locale.US);
                case PTBR ->    sc.useLocale(java.util.Locale.of("pt", "BR"));
                case ROOT ->    sc.useLocale(java.util.Locale.ROOT);
                case DEFAULT -> sc.useLocale(java.util.Locale.getDefault());
            }
        });
    }

    /**
     * Checks if another token is available.
     *
     * @return {@code true} if another token is available; {@code false} otherwise
     */
    @Override
    public boolean hasNext() {
        validate();
        return scanner.map(Scanner::hasNext).orElse(false);
    }

    /**
     * Checks if another line of input is available.
     *
     * @return {@code true} if another line is available; {@code false} otherwise
     */
    @Override
    public boolean hasNextLine() {
        validate();
        return scanner.map(Scanner::hasNextLine).orElse(false);
    }

    /**
     * Reads the next token from the input.
     *
     * @return the next input token as a string, or an empty string if scanner is missing
     */
    @Override
    @Contract(pure = true)
    public String next() {
        validate();
        return scanner.map(Scanner::next).orElse("");
    }

    /**
     * Reads the next token that matches a regular expression pattern.
     *
     * @param pattern regex pattern to match
     * @return matched string or empty if the scanner is missing
     */
    @Override
    @Contract(pure = true)
    public String next(@NotNull String pattern) {
        validate();
        return scanner.map(sc -> sc.next(pattern)).orElse("");
    }

    /**
     * Reads the next full line from the input.
     *
     * @return the next line, or an empty string if scanner is missing
     */
    @Override
    @Contract(pure = true)
    public String nextLine() {
        validate();
        return scanner.map(Scanner::nextLine).orElse("");
    }

    /**
     * Reads the next token and parses it into an {@code int}.
     * Falls back to safe parsing via {@link StringToNumber#toInt(String)}.
     *
     * @return the parsed integer value, or {@code 0} if invalid
     */
    @Override
    @Contract(pure = true)
    public int nextInt() {
        return StringToNumber.toInt(nextSafe());
    }

    /**
     * Reads the next token and parses it into a {@code long}.
     *
     * @return the parsed long value, or {@code 0L} if invalid
     */
    @Override
    @Contract(pure = true)
    public long nextLong() {
        return StringToNumber.toLong(nextSafe());
    }

    /**
     * Reads the next token and parses it into a {@code float}.
     *
     * @return the parsed float value, or {@code 0.0f} if invalid
     */
    @Override
    @Contract(pure = true)
    public float nextFloat() {
        return StringToNumber.toFloat(nextSafe());
    }

    /**
     * Reads the next token and parses it into a {@code double}.
     *
     * @return the parsed double value, or {@code 0.0} if invalid
     */
    @Override
    @Contract(pure = true)
    public double nextDouble() {
        return StringToNumber.toDouble(nextSafe());
    }

    /**
     * Reads the next token and parses it into a {@code boolean}.
     * Accepts "true", "false", "yes", "no", "1", "0", case-insensitively.
     *
     * @return the parsed boolean value
     */
    @Override
    @Contract(pure = true)
    public boolean nextBoolean() {
        return StringToBoolean.toBoolean(nextSafe());
    }

    /**
     * Reads the next token safely, returning an empty string in case of any exception.
     *
     * @return the next token or {@code ""} if an error occurs
     */
    @Override
    @Contract(pure = true)
    public String nextSafe() {
        try {
            return next();
        } catch (Exception e) {
            warn("nextSafe() failed. Returning empty string. Cause: {}", e, e.getMessage());
            return "";
        }
    }

    /**
     * Closes the scanner and releases underlying resources.
     * Once closed, the scanner should be re-initialized before further use.
     */
    @Override
    public void close() {
        validate();
        scanner.ifPresent(Scanner::close);
    }

    /**
     * Validates that the scanner has been initialized before use.
     *
     * @throws IllegalStateException if the scanner is not present
     */
    private void validate() {
        Conditionals.ifTrueThrow(scanner.isEmpty(), () ->
                new IllegalStateException("Mistake: Scanner not initialized. Call ScannerUtils.init() first."));
    }
}
