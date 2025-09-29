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

import com.github.rickmvi.jtoolbox.console.utils.convert.TypeAdapter;
import com.github.rickmvi.jtoolbox.text.internal.NumberInterface;
import com.github.rickmvi.jtoolbox.text.internal.NumberStyle;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for formatting numeric values using predefined styles from {@link NumberStyle}.
 * <p>
 * Also provides reusable instances of {@link NumberInterface} for common styles.
 * </p>
 *
 * Examples:
 * <pre>{@code
 * String formatted = NumberFormats.format(1234.5678, NumberFormatStyle.DECIMAL_COMMA); // "1.234,57"
 * NumberFormatter formatter = NumberFormats.SCIENTIFIC;
 * String result = formatter.format(123456); // "1.23E5"
 * }</pre>
 */
@UtilityClass
public class NumberFormat {

    /**
     * Creates a {@link NumberInterface} from the specified style.
     *
     * @param style the formatting style
     * @return a functional instance of {@link NumberInterface}
     */
    @Contract(pure = true)
    public static @NotNull NumberInterface of(NumberStyle style) {
        return value -> format(value, style);
    }

    @ApiStatus.Internal
    public static String format(Object value, @NotNull NumberStyle style) {
        return style.format(TypeAdapter.toDouble(value));
    }

    /** Formatter with comma as a decimal separator */
    public static final NumberInterface DECIMAL_COMMA  = of(NumberStyle.DECIMAL_COMMA);

    /** Formatter with dot as a decimal separator */
    public static final NumberInterface DECIMAL_POINT  = of(NumberStyle.DECIMAL_POINT);

    /** Formatter for integers with a thousand separator */
    public static final NumberInterface INTEGER        = of(NumberStyle.INTEGER);

    /** Formatter for percentages with two decimal places */
    public static final NumberInterface PERCENT        = of(NumberStyle.PERCENT);

    /** Formatter in scientific notation */
    public static final NumberInterface SCIENTIFIC     = of(NumberStyle.SCIENTIFIC);

    public static final NumberInterface EXPONENTIATION = of(NumberStyle.EXPONENTIATION);
}
