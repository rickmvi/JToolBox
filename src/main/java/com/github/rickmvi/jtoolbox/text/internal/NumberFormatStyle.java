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
package com.github.rickmvi.jtoolbox.text.internal;

import org.jetbrains.annotations.Contract;

import java.text.DecimalFormat;

/**
 * Enum representing predefined numeric formatting styles
 * using patterns from {@link DecimalFormat}.
 * <p>
 * Each style provides a different format useful for representing numbers
 * with decimal commas, decimal points, integers, percentages, and scientific notation.
 * </p>
 *
 * Examples:
 * <ul>
 *     <li>{@code DECIMAL_COMMA} → "1.234,56"</li>
 *     <li>{@code DECIMAL_POINT} → "1234.56"</li>
 *     <li>{@code INTEGER} → "1.234"</li>
 *     <li>{@code PERCENT} → "12.50%"</li>
 *     <li>{@code SCIENTIFIC} → "1.23E3"</li>
 * </ul>
 */
public enum NumberFormatStyle {

    /** Format with comma as decimal separator (e.g., 1.234,56) */
    DECIMAL_COMMA ("#,##0.00"),

    /** Format with dot as decimal separator (e.g., 1234.56) */
    DECIMAL_POINT ("###0.00"),

    /** Integer format with a thousand separator (e.g., 1.234) */
    INTEGER       ("#,##0"),

    /** Percentage format (e.g., 12.50%) */
    PERCENT       ("0.00'%'"),

    /** Scientific notation format (e.g., 1.23E3) */
    SCIENTIFIC    ("0.##E0");

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC)
    private final DecimalFormat format;

    /**
     * Constructs a formatting style using the specified pattern.
     *
     * @param pattern the {@link DecimalFormat} pattern
     */
    @Contract(pure = true)
    NumberFormatStyle(String pattern) {
        this.format = new DecimalFormat(pattern);
    }

    /**
     * Formats the given number according to this style.
     *
     * @param number the numeric value to format
     * @return the formatted string
     */
    public String format(double number) {
        return format.format(number);
    }
}