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
package com.github.rickmvi.jtoolbox.logger;

import com.github.rickmvi.jtoolbox.control.For;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.logger.log.LogLevel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
 *
 * @author Rick M. Viana
 * @version 1.1
 * @since 2025
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AnsiColor {
    BLACK   ( "\u001B[30m" ),
    BLUE    ( "\u001B[34m" ),
    BOLD    ( "\u001B[1m"  ),
    CYAN    ( "\u001B[36m" ),
    GREEN   ( "\u001B[32m" ),
    MAGENTA ( "\u001B[35m" ),
    RED     ( "\u001B[31m" ),
    RESET   ( "\u001B[0m"  ),
    WHITE   ( "\u001B[37m" ),
    YELLOW  ( "\u001B[33m" );

    @Getter(value = AccessLevel.PUBLIC)
    private final String code;
    private static final AnsiColor[] VALUES = values();

    @Contract(pure = true)
    public static @NotNull AnsiColor valueOf(int ordinal) {
        If.ThrowWhen(
                ordinal < 0 || ordinal >= VALUES.length,
                () -> new IndexOutOfBoundsException("Invalid ordinal: " + ordinal)
        );
        return VALUES[ordinal];
    }

    public static @NotNull String getColor(@NotNull LogLevel level) {
        if (level == LogLevel.OFF) return "";
        return switch (level) {
            case TRACE -> MAGENTA.getCode();
            case DEBUG -> CYAN.getCode();
            case INFO  -> GREEN.getCode();
            case WARN  -> YELLOW.getCode();
            case ERROR -> RED.getCode();
            case FATAL -> BOLD.getCode() + RED.getCode();
            default    -> RESET.getCode();
        };
    }

    public static AnsiColor of(@NotNull String ansiCode) {
        return For.of(AnsiColor.values())
                .filter(getColor -> getColor.getCode().equals(ansiCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ansi code: " + ansiCode));
    }
}
