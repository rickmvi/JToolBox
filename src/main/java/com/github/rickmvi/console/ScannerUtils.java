/*
 * Console API - Biblioteca utilitária para entrada, saída e formatação no console.
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
package com.github.rickmvi.console;

import com.github.rickmvi.console.internal.ScannerHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

@lombok.experimental.UtilityClass
public class ScannerUtils {

    @lombok.Getter(value = lombok.AccessLevel.PRIVATE)
    private final static ScannerHandler core = new ScannerHandler();

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

    @Contract(pure = true)
    public static String nextSafe() {
        return core.nextSafe();
    }

    public static void close() {
        core.close();
    }
}
