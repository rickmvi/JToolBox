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
package com.github.rickmvi.jtoolbox.text;

import com.github.rickmvi.jtoolbox.console.utils.convert.TypeCaster;
import com.github.rickmvi.jtoolbox.text.internal.NumberFormatStyle;
import com.github.rickmvi.jtoolbox.text.internal.NumberFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for formatting numeric values using predefined styles from {@link NumberFormatStyle}.
 * <p>
 * Also provides reusable instances of {@link NumberFormatter} for common styles.
 * </p>
 *
 * Examples:
 * <pre>{@code
 * String formatted = NumberFormats.format(1234.5678, NumberFormatStyle.DECIMAL_COMMA); // "1.234,57"
 * NumberFormatter formatter = NumberFormats.SCIENTIFIC;
 * String result = formatter.format(123456); // "1.23E5"
 * }</pre>
 */
@lombok.experimental.UtilityClass
public class NumberFormat {

    /**
     * Formats a generic value using the given formatting style.
     *
     * @param value the object to be converted (supports {@link Number}, {@code String}, etc.)
     * @param style the formatting style to apply
     * @return the formatted string
     */
    public static String format(Object value, @NotNull NumberFormatStyle style) {
        return style.format(TypeCaster.toDouble(value));
    }

    /**
     * Creates a {@link NumberFormatter} from the specified style.
     *
     * @param style the formatting style
     * @return a functional instance of {@link NumberFormatter}
     */
    @Contract(pure = true)
    public static @NotNull NumberFormatter of(NumberFormatStyle style) {
        return value -> format(value, style);
    }

    /** Formatter with comma as a decimal separator */
    public static final NumberFormatter DECIMAL_COMMA = of(NumberFormatStyle.DECIMAL_COMMA);

    /** Formatter with dot as a decimal separator */
    public static final NumberFormatter DECIMAL_POINT = of(NumberFormatStyle.DECIMAL_POINT);

    /** Formatter for integers with a thousand separator */
    public static final NumberFormatter INTEGER       = of(NumberFormatStyle.INTEGER);

    /** Formatter for percentages with two decimal places */
    public static final NumberFormatter PERCENT       = of(NumberFormatStyle.PERCENT);

    /** Formatter in scientific notation */
    public static final NumberFormatter SCIENTIFIC    = of(NumberFormatStyle.SCIENTIFIC);
}
