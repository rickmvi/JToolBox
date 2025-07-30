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
package com.github.rickmvi.jtoolbox.console.debug;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum AnsiColor {
    BLACK   ("\u001B[30m"),
    BLUE    ("\u001B[34m"),
    BOLD    ("\u001B[1m"),
    CYAN    ("\u001B[36m"),
    GREEN   ("\u001B[32m"),
    MAGENTA ("\u001B[35m"),
    RED     ("\u001B[31m"),
    RESET   ("\u001B[0m"),
    WHITE   ("\u001B[37m"),
    YELLOW  ("\u001B[33m");

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC, onMethod_ = @NotNull)
    private final String ansiCode;
    private static final AnsiColor[] VALUES = values();

    @Contract(pure = true)
    public static @NotNull AnsiColor valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        return VALUES[ordinal];
    }

    public static @NotNull String getColor(@NotNull LogLevel level) {
        if (level == LogLevel.OFF) return "";
        return switch (level) {
            case TRACE -> MAGENTA.getAnsiCode();
            case DEBUG -> CYAN.getAnsiCode();
            case INFO -> GREEN.getAnsiCode();
            case WARN -> YELLOW.getAnsiCode();
            case ERROR -> RED.getAnsiCode();
            case FATAL -> BOLD.getAnsiCode() + RED.getAnsiCode();
            default -> RESET.getAnsiCode();
        };
    }

    @NotNull
    public static AnsiColor of(@NotNull String ansiCode) {
        for (AnsiColor color : values()) {
            if (color.getAnsiCode().equals(ansiCode)) return color;
        }
        throw new IllegalArgumentException("Invalid ansi code: " + ansiCode);
    }
}
