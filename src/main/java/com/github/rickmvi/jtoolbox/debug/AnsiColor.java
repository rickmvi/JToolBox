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
package com.github.rickmvi.jtoolbox.debug;

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.debug.log.LogLevel;

import com.github.rickmvi.jtoolbox.util.constants.Constants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * AnsiColor is an enumeration that represents ANSI escape codes for coloring and formatting console output.
 * These color codes are commonly used for adding color and styles to text output in terminal applications.
 * The enum provides a variety of standard colors, as well as bold and reset formatting options.
 * <p>
 * Each color is associated with its respective ANSI code, which can be used to apply formatting to strings.
 * It also includes utility methods for retrieving colors by ordinal, resolving colors by ANSI code,
 * and mapping log levels to specific colors.
 */
@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum AnsiColor {
    BLACK   ( Constants.BLACK   ),
    BLUE    ( Constants.BLUE    ),
    BOLD    ( Constants.BOLD    ),
    CYAN    ( Constants.CYAN    ),
    GREEN   ( Constants.GREEN   ),
    MAGENTA ( Constants.MAGENTA ),
    RED     ( Constants.RED     ),
    RESET   ( Constants.RESET   ),
    WHITE   ( Constants.WHITE   ),
    YELLOW  ( Constants.YELLOW  );

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC)
    private final String ansiCode;
    private static final AnsiColor[] VALUES = values();

    /**
     * Retrieves an {@code AnsiColor} by its ordinal index.
     *
     * @param ordinal the ordinal index of the enum constant
     * @return the {@code AnsiColor} at the specified index
     * @throws IndexOutOfBoundsException if the ordinal is invalid
     */
    @Contract(pure = true)
    public static @NotNull AnsiColor valueOf(int ordinal) {
        If.Throws(
                ordinal < 0 || ordinal >= VALUES.length,
                () -> new IndexOutOfBoundsException("Invalid ordinal: " + ordinal)
        );
        return VALUES[ordinal];
    }

    /**
     * Retrieves the appropriate ANSI color code based on the specified log level.
     *
     * @param level the {@link LogLevel}
     * @return the ANSI escape code associated with the level, or empty string if OFF
     */
    public static @NotNull String getColor(@NotNull LogLevel level) {
        if (level == LogLevel.OFF) return "";
        return switch (level) {
            case TRACE -> MAGENTA.getAnsiCode();
            case DEBUG -> CYAN.getAnsiCode();
            case INFO  -> GREEN.getAnsiCode();
            case WARN  -> YELLOW.getAnsiCode();
            case ERROR -> RED.getAnsiCode();
            case FATAL -> BOLD.getAnsiCode() + RED.getAnsiCode();
            default ->    RESET.getAnsiCode();
        };
    }

    /**
     * Finds the corresponding {@code AnsiColor} enum from a raw ANSI escape code.
     *
     * @param ansiCode the ANSI escape code
     * @return the corresponding {@code AnsiColor}
     * @throws IllegalArgumentException if no matching color is found
     */
    @NotNull
    public static AnsiColor of(@NotNull String ansiCode) {
        for (AnsiColor color : values()) {
            if (color.getAnsiCode().equals(ansiCode)) return color;
        }
        throw new IllegalArgumentException("Invalid ansi code: " + ansiCode);
    }
}
