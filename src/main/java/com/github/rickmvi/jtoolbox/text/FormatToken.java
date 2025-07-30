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

import com.github.rickmvi.jtoolbox.console.convert.ObjectToNumber;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Enumeration representing various format tokens for string formatting.
 * <p>
 * Each token corresponds to a specific format type and defines how to
 * convert an input object into a formatted string representation.
 * <p>
 * Supported tokens include:
 * <ul>
 *   <li>{@code s} - Formats as a string</li>
 *   <li>{@code i} - Formats as an integer</li>
 *   <li>{@code l} - Formats as a long</li>
 *   <li>{@code #d} - Formats as a decimal number with two decimal places</li>
 *   <li>{@code ed} - Formats as a number in exponential notation</li>
 *   <li>{@code n} - Formats as a number with thousands separator and two decimals</li>
 *   <li>{@code u} - Formats as uppercase string</li>
 *   <li>{@code lc} - Formats as lowercase string</li>
 *   <li>{@code c} - Formats as a single character (first character of the string)</li>
 *   <li>{@code b} - Formats as a boolean string representation</li>
 * </ul>
 * <p>
 * The enum provides the {@link #format(Object)} method to convert an object
 * to its formatted string according to the token, and a static method
 * {@link #fromCode(String)} to retrieve the token by its string code.
 */
public enum FormatToken {
    STRING("s") {
        @Override
        public String format(Object args1) {
            return String.valueOf(args1);
        }
    },
    SPACE("n"){
      @Override
      public String format(Object value) {
          return String.valueOf(value).replace("%n", System.lineSeparator());
      }
    },
    INT("i") {
        @Override
        public String format(Object value) {
            return String.valueOf(ObjectToNumber.toInt(value));
        }
    },
    LONG("l") {
        @Override
        public String format(Object value) {
            return String.valueOf(ObjectToNumber.toLong(value));
        }
    },
    DECIMAL("#d") {
        @Override
        public String format(Object value) {
            return String.format(Locale.US, "%.2f", ObjectToNumber.toDouble(value));
        }
    },
    EXPONENTIAL("ed") {
        @Override
        public String format(Object value) {
            return String.format(Locale.US, "%.2e", ObjectToNumber.toDouble(value));
        }
    },
    NUMBER("dn") {
        private final DecimalFormat format = new DecimalFormat("#,##0.00");
        @Override
        public String format(Object value) {
            return format.format(ObjectToNumber.toDouble(value));
        }
    },
    UPPER("u") {
        @Override
        public String format(Object value) {
            return String.valueOf(value).toUpperCase(Locale.ROOT);
        }
    },
    LOWER("lc") {
        @Override
        public String format(Object value) {
            return String.valueOf(value).toLowerCase(Locale.ROOT);
        }
    },
    CHAR("c") {
        @Override
        public String format(Object value) {
            return String.valueOf(value).charAt(0) + "";
        }
    },
    BOOLEAN("b") {
        @Override
        public String format(Object value) {
            return String.valueOf(Boolean.parseBoolean(String.valueOf(value)));
        }
    };

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC, onMethod_ = @Contract(pure = true))
    private final String code;

    /**
     * Constructs a format token with the associated string code.
     *
     * @param code the string code representing this token (e.g., "s", "i", "#d")
     */
    @Contract(pure = true)
    FormatToken(String code) {
        this.code = code;
    }

    /**
     * Formats the given value according to the token's formatting rules.
     *
     * @param value the input value to format
     * @return the formatted string representation of the value
     */
    public abstract String format(Object value);

    /**
     * Retrieves the {@link FormatToken} enum constant corresponding to the given code.
     *
     * @param code the string code to look up (case-sensitive)
     * @return the matching {@link FormatToken}, or {@code null} if no match is found
     */
    public static @Nullable FormatToken fromCode(String code) {
        for (FormatToken token : FormatToken.values()) {
            if (token.code.equals(code)) {
                return token;
            }
        }
        return null;
    }
}