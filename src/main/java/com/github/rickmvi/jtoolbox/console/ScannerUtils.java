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
package com.github.rickmvi.jtoolbox.console;

import com.github.rickmvi.jtoolbox.console.internal.ScannerHandler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * Utility class for enhanced and safe input operations via {@link Scanner}.
 * <p>
 * This class provides static methods for initializing, reading, validating, and closing
 * input streams.
 * It wraps a core {@link ScannerHandler} instance to centralize
 * input logic, enabling features like locale management, pattern matching, and safe reads.
 * <p>
 * The methods are null-safe, optionally locale-aware, and ready for user input in
 * interactive console applications.
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class ScannerUtils {


    /**
     * A private, final, and static instance of {@link ScannerHandler} designed
     * to manage the internal handling of {@link Scanner} operations.
     * This singleton ensures streamlined input management with options like custom initialization,
     * locale settings, and various token or line reading methods.
     * <p>
     * This instance cannot be accessed directly due to its private access level
     * but is used internally by the {@link ScannerUtils} class to provide
     * input scanning functionalities.
     */
    @lombok.Getter(value = lombok.AccessLevel.PRIVATE)
    private final static ScannerHandler core = new ScannerHandler();

    /**
     * Initializes the internal {@link Scanner} using {@code System.in}.
     * If already initialized, it may override the existing instance.
     */
    public static void init() {
        core.init();
    }

    /**
     * Initializes the internal {@link Scanner} with a custom {@link Scanner} instance.
     * Useful for testing or custom input sources.
     *
     * @param scanner the scanner instance to be used
     */
    public static void init(@NotNull Scanner scanner) {
        core.init(scanner);
    }

    /**
     * Sets the expected input locale or regional configuration.
     * This may affect things like decimal separators (e.g., dot vs. comma).
     *
     * @param location the location enum specifying locale behavior
     */
    public static void locale(@NotNull Location location) {
        core.locale(location);
    }

    /**
     * Checks whether the scanner has another token available.
     *
     * @return {@code true} if another token is available; {@code false} otherwise
     */
    public static boolean hasNext() {
        return core.hasNext();
    }

    /**
     * Checks whether the scanner has another full line of input.
     *
     * @return {@code true} if another line is available; {@code false} otherwise
     */
    public static boolean hasNextLine() {
        return core.hasNextLine();
    }

    /**
     * Reads the next token from the input.
     *
     * @return the next token as a string
     */
    @Contract(pure = true)
    public static String next() {
        return core.next();
    }

    /**
     * Reads the next token that matches the given regular expression pattern.
     *
     * @param pattern the regular expression pattern to match
     * @return the matched input token
     */
    @Contract(pure = true)
    public static String next(@NotNull String pattern) {
        return core.next(pattern);
    }

    /**
     * Reads the next full line of input.
     *
     * @return the next line as a string
     */
    @Contract(pure = true)
    public static String nextLine() {
        return core.nextLine();
    }

    /**
     * Reads the next token as an integer value.
     *
     * @return the parsed integer
     * @throws java.util.InputMismatchException if input is not a valid integer
     */
    @Contract(pure = true)
    public static int nextInt() {
        return core.nextInt();
    }

    /**
     * Reads the next token as a long value.
     *
     * @return the parsed long
     * @throws java.util.InputMismatchException if input is not a valid long
     */
    @Contract(pure = true)
    public static long nextLong() {
        return core.nextLong();
    }

    /**
     * Reads the next token as a double value.
     *
     * @return the parsed double
     * @throws java.util.InputMismatchException if input is not a valid double
     */
    @Contract(pure = true)
    public static double nextDouble() {
        return core.nextDouble();
    }

    /**
     * Reads the next token as a float value.
     *
     * @return the parsed float
     * @throws java.util.InputMismatchException if input is not a valid float
     */
    @Contract(pure = true)
    public static float nextFloat() {
        return core.nextFloat();
    }

    /**
     * Reads the next token as a boolean value.
     * Accepts "true" or "false" (case-insensitive).
     *
     * @return the parsed boolean
     * @throws java.util.InputMismatchException if input is not a valid boolean
     */
    @Contract(pure = true)
    public static boolean nextBoolean() {
        return core.nextBoolean();
    }

    /**
     * Reads the next token from the input safely.
     * <p>
     * This version ensures no exception is thrown and always returns a non-null string.
     * If input is invalid, returns an empty string or a safe default.
     *
     * @return the next token, or a fallback string if an error occurs
     */
    @Contract(pure = true)
    public static String nextSafe() {
        return core.nextSafe();
    }

    /**
     * Closes the underlying {@link Scanner} and releases any associated resources.
     * Once closed, the scanner cannot be reused unless re-initialized.
     */
    public static void close() {
        core.close();
    }
}
