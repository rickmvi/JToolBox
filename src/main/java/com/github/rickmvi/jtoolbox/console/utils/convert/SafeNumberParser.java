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
package com.github.rickmvi.jtoolbox.console.utils.convert;

import com.github.rickmvi.jtoolbox.template.TryConvert;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.Optional;

/**
 * Utility class for safely converting {@link String} values to Java numeric types.
 * <p>
 * This class provides methods to convert strings into primitive numeric types
 * such as {@code int}, {@code long}, {@code double}, and {@code float} with
 * support for default fallback values and lazy fallback suppliers.
 * <p>
 * All parsing operations use the {@link TryConvert} utility for safe conversions,
 * avoiding unchecked exceptions and enabling use of {@link Optional} where appropriate.
 * <p>
 * Methods are overloaded to support:
 * <ul>
 *   <li>Conversion with default fallback values</li>
 *   <li>Conversion with {@link Supplier} fallback to defer computation</li>
 *   <li>Optional results wrapping</li>
 * </ul>
 * <p>
 * Null or invalid inputs result in fallback values or empty optionals, ensuring
 * robust and null-safe numeric parsing in console applications or general utilities.
 */
@lombok.experimental.UtilityClass
public class StringToNumber {

    /**
     * Converts a string to an {@code int}, returning 0 if the string is null or invalid.
     *
     * @param value the string to convert, may be {@code null}
     * @return the parsed integer or 0 on failure
     */
    public static int toInt(@Nullable String value) {
        return toInt(value, 0);
    }

    /**
     * Converts a string to an {@code int}, returning the specified fallback if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback integer value if conversion fails
     * @return the parsed integer or the fallback value on failure
     */
    public static int toInt(@Nullable String value, int fallback) {
        return TryConvert.convert(value, Integer::parseInt).orElse(fallback);
    }

    /**
     * Converts a string to an {@code int}, using a lazy fallback supplier if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback supplier to provide an integer if conversion fails
     * @return the parsed integer or the fallback value supplied
     */
    public static int toInt(@Nullable String value, @NotNull Supplier<Integer> fallback) {
        return TryConvert.convert(value, Integer::parseInt).orElseGet(fallback);
    }

    /**
     * Converts a string to an {@link Optional} integer.
     *
     * @param value the string to convert, may be {@code null}
     * @return an {@link Optional} containing the parsed integer or empty if invalid
     */
    @Contract("null -> !null")
    public static Optional<Integer> toIntOptional(@Nullable String value) {
        return TryConvert.convert(value, Integer::parseInt);
    }

    /**
     * Converts a string to a {@code long}, returning 0L if the string is null or invalid.
     *
     * @param value the string to convert, may be {@code null}
     * @return the parsed long or 0L on failure
     */
    public static long toLong(@Nullable String value) {
        return toLong(value, 0L);
    }

    /**
     * Converts a string to a {@code long}, returning the specified fallback if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback-long value if conversion fails
     * @return the parsed long or the fallback value on failure
     */
    public static long toLong(@Nullable String value, long fallback) {
        return TryConvert.convert(value, Long::parseLong).orElse(fallback);
    }

    /**
     * Converts a string to a {@code long}, using a lazy fallback supplier if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback supplier to provide a long if conversion fails
     * @return the parsed long or the fallback value supplied
     */
    public static long toLong(@Nullable String value, @NotNull Supplier<Long> fallback) {
        return TryConvert.convert(value, Long::parseLong).orElseGet(fallback);
    }

    /**
     * Converts a string to an {@link Optional} long.
     *
     * @param value the string to convert, may be {@code null}
     * @return an {@link Optional} containing the parsed long or empty if invalid
     */
    @Contract("null -> !null")
    public static Optional<Long> toLongOptional(@Nullable String value) {
        return TryConvert.convert(value, Long::parseLong);
    }

    /**
     * Converts a string to a {@code double}, returning 0.0 if the string is null or invalid.
     *
     * @param value the string to convert, may be {@code null}
     * @return the parsed double or 0.0 on failure
     */
    public static double toDouble(@Nullable String value) {
        return toDouble(value, 0.0);
    }

    /**
     * Converts a string to a {@code double}, returning the specified fallback if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback double value if conversion fails
     * @return the parsed double or the fallback value on failure
     */
    public static double toDouble(@Nullable String value, double fallback) {
        return TryConvert.convert(value, Double::parseDouble).orElse(fallback);
    }

    /**
     * Converts a string to a {@code double}, using a lazy fallback supplier if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback supplier to provide a double if conversion fails
     * @return the parsed double or the fallback value supplied
     */
    public static double toDouble(@Nullable String value, @NotNull Supplier<Double> fallback) {
        return TryConvert.convert(value, Double::parseDouble).orElseGet(fallback);
    }

    /**
     * Converts a string to an {@link Optional} double.
     *
     * @param value the string to convert, may be {@code null}
     * @return an {@link Optional} containing the parsed double or empty if invalid
     */
    @Contract("null -> !null")
    public static Optional<Double> toDoubleOptional(@Nullable String value) {
        return TryConvert.convert(value, Double::parseDouble);
    }

    /**
     * Converts a string to a {@code float}, returning 0.0f if the string is null or invalid.
     *
     * @param value the string to convert, may be {@code null}
     * @return the parsed float or 0.0f on failure
     */
    public static float toFloat(@Nullable String value) {
        return toFloat(value, 0.0f);
    }

    /**
     * Converts a string to a {@code float}, returning the specified fallback if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback float value if conversion fails
     * @return the parsed float or the fallback value on failure
     */
    public float toFloat(@Nullable String value, float fallback) {
        return TryConvert.convert(value, Float::parseFloat).orElse(fallback);
    }

    /**
     * Converts a string to a {@code float}, using a lazy fallback supplier if conversion fails.
     *
     * @param value    the string to convert, may be {@code null}
     * @param fallback the fallback supplier to provide a float if conversion fails
     * @return the parsed float or the fallback value supplied
     */
    public float toFloat(@Nullable String value, @NotNull Supplier<Float> fallback) {
        return TryConvert.convert(value, Float::parseFloat).orElseGet(fallback);
    }

    /**
     * Converts a string to an {@link Optional} float.
     *
     * @param value the string to convert, may be {@code null}
     * @return an {@link Optional} containing the parsed float or empty if invalid
     */
    public Optional<Float> toFloatOptional(@Nullable String value) {
        return TryConvert.convert(value, Float::parseFloat);
    }
}
