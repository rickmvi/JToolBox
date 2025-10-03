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
package com.github.rickmvi.jtoolbox.console.utils;

import com.github.rickmvi.jtoolbox.console.utils.internal.Scanf;

import lombok.Getter;
import lombok.AccessLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Contract;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Utility interface for enhanced and safe input operations via {@link Scanner}.
 * <p>
 * This interface provides static methods for initializing, reading, validating, and closing
 * input streams.
 * It wraps a core {@link Scanf} instance to centralize
 * input logic, enabling features like locale management, pattern matching, and safe reads.
 * <p>
 * The methods are null-safe, optionally locale-aware, and ready for user input in
 * interactive console applications.
 *
 * @author Rick M. Viana
 * @since 1.1
 */
public interface Scan {

    @Getter(value = AccessLevel.PRIVATE)
    Scanf SCAN_UTILITY_INSTANCE = new Scanf();

    static void init() {
        SCAN_UTILITY_INSTANCE.init();
    }

    static void init(@NotNull Scanner scanner) {
        SCAN_UTILITY_INSTANCE.init(scanner);
    }

    static void locale(@NotNull Location location) {
        SCAN_UTILITY_INSTANCE.locale(location);
    }

    static boolean hasNext() {
        return SCAN_UTILITY_INSTANCE.hasNext();
    }

    static boolean hasNextLine() {
        return SCAN_UTILITY_INSTANCE.hasNextLine();
    }

    @Contract(pure = true)
    static String read() {
        return SCAN_UTILITY_INSTANCE.read();
    }

    @Contract(pure = true)
    static String read(@NotNull String pattern) {
        return SCAN_UTILITY_INSTANCE.read(pattern);
    }

    @Contract(pure = true)
    static String readLine() {
        return SCAN_UTILITY_INSTANCE.readLine();
    }

    @Contract(pure = true)
    static int readInt() {
        return SCAN_UTILITY_INSTANCE.readInt();
    }

    @Contract(pure = true)
    static long readLong() {
        return SCAN_UTILITY_INSTANCE.readLong();
    }

    @Contract(pure = true)
    static double readDouble() {
        return SCAN_UTILITY_INSTANCE.readDouble();
    }

    @Contract(pure = true)
    static float readFloat() {
        return SCAN_UTILITY_INSTANCE.readFloat();
    }

    @Contract(pure = true)
    static boolean readBoolean() {
        return SCAN_UTILITY_INSTANCE.readBoolean();
    }

    static String readSafe() {
        return SCAN_UTILITY_INSTANCE.readSafe();
    }

    static void close() {
        SCAN_UTILITY_INSTANCE.close();
    }

    static String readPrompt(String prompt) {
        return SCAN_UTILITY_INSTANCE.readPrompt(prompt);
    }

    static String readUntil(String prompt, @NotNull Predicate<String> validator) {
        return SCAN_UTILITY_INSTANCE.readUntil(prompt, validator);
    }

    static int readIntPrompt(String prompt) {
        return SCAN_UTILITY_INSTANCE.readIntPrompt(prompt);
    }

    static int readIntUntil(String prompt, IntPredicate validator) {
        return SCAN_UTILITY_INSTANCE.readIntUntil(prompt, validator);
    }

    static <T extends Number> T readNumberUntil(
            String prompt,
            Function<String, T> parser,
            Predicate<T> validator)
    {
        return SCAN_UTILITY_INSTANCE.readNumberUntil(prompt, parser, validator);
    }

}
