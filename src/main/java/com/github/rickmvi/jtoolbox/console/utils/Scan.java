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

import com.github.rickmvi.jtoolbox.console.utils.internal.ScanUtility;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Contract;

import java.util.Scanner;

/**
 * Utility class for enhanced and safe input operations via {@link Scanner}.
 * <p>
 * This class provides static methods for initializing, reading, validating, and closing
 * input streams.
 * It wraps a core {@link ScanUtility} instance to centralize
 * input logic, enabling features like locale management, pattern matching, and safe reads.
 * <p>
 * The methods are null-safe, optionally locale-aware, and ready for user input in
 * interactive console applications.
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@UtilityClass
public class Scan {

    @Getter(value = AccessLevel.PRIVATE)
    private final static ScanUtility core = new ScanUtility();

    public static void init() {
        core.init();
    }

    public static void init(@NotNull Scanner scanner) {
        core.init(scanner);
    }

    public static void locale(@NotNull Location location) {
        core.locale(location);
    }

    public static boolean hasNext() {
        return core.hasNext();
    }

    public static boolean hasNextLine() {
        return core.hasNextLine();
    }

    @Contract(pure = true)
    public static String next() {
        return core.next();
    }

    @Contract(pure = true)
    public static String next(@NotNull String pattern) {
        return core.next(pattern);
    }

    @Contract(pure = true)
    public static String nextLine() {
        return core.nextLine();
    }

    @Contract(pure = true)
    public static int nextInt() {
        return core.nextInt();
    }

    @Contract(pure = true)
    public static long nextLong() {
        return core.nextLong();
    }

    @Contract(pure = true)
    public static double nextDouble() {
        return core.nextDouble();
    }

    @Contract(pure = true)
    public static float nextFloat() {
        return core.nextFloat();
    }

    @Contract(pure = true)
    public static boolean nextBoolean() {
        return core.nextBoolean();
    }

    public static String nextSafe() {
        return core.nextSafe();
    }

    public static void close() {
        core.close();
    }
}
