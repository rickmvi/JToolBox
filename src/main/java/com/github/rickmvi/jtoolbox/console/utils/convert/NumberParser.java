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

import com.github.rickmvi.jtoolbox.text.internal.NumberInterface;
import com.github.rickmvi.jtoolbox.util.SafeRun;

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
 * All parsing operations use the {@link SafeRun} utility for safe conversions,
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
public class NumberParser extends TypeAdapter {

    public static byte toByte(@Nullable String value) {
        return toByte(value, (byte) 0);
    }

    public static byte toByte(@Nullable String value, byte fallback) {
        return SafeRun.convert(value, Byte::parseByte).orElse(fallback);
    }

    public static byte toByte(@Nullable String value, @NotNull Supplier<Byte> fallback) {
        return SafeRun.convert(value, Byte::parseByte).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Byte> toByteOptional(@Nullable String value) {
        return SafeRun.convert(value, Byte::parseByte);
    }

    public static short toShort(@Nullable String value) {
        return toShort(value, (short) 0);
    }

    public static short toShort(@Nullable String value, short fallback) {
        return SafeRun.convert(value, Short::parseShort).orElse(fallback);
    }

    public static short toShort(@Nullable String value, Supplier<Short> fallback) {
        return SafeRun.convert(value, Short::parseShort).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Short> toShortOptional(@Nullable String value) {
        return SafeRun.convert(value, Short::parseShort);
    }

    public static int toInt(@Nullable String value) {
        return toInt(value, 0);
    }

    public static int toInt(@Nullable String value, int fallback) {
        return SafeRun.convert(value, Integer::parseInt).orElse(fallback);
    }

    public static int toInt(@Nullable String value, @NotNull Supplier<Integer> fallback) {
        return SafeRun.convert(value, Integer::parseInt).orElseGet(fallback);
        
    }

    @Contract("null -> !null")
    public static Optional<Integer> toIntOptional(@Nullable String value) {
        return SafeRun.convert(value, Integer::parseInt);
    }

    public static long toLong(@Nullable String value) {
        return toLong(value, 0L);
    }

    public static long toLong(@Nullable String value, long fallback) {
        return SafeRun.convert(value, Long::parseLong).orElse(fallback);
    }

    public static long toLong(@Nullable String value, @NotNull Supplier<Long> fallback) {
        return SafeRun.convert(value, Long::parseLong).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Long> toLongOptional(@Nullable String value) {
        return SafeRun.convert(value, Long::parseLong);
    }

    public static double toDouble(@Nullable String value) {
        return toDouble(value, 0.0);
    }

    public static double toDouble(@Nullable String value, double fallback) {
        return SafeRun.convert(value, Double::parseDouble).orElse(fallback);
    }

    public static double toDouble(@Nullable String value, @NotNull Supplier<Double> fallback) {
        return SafeRun.convert(value, Double::parseDouble).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Double> toDoubleOptional(@Nullable String value) {
        return SafeRun.convert(value, Double::parseDouble);
    }

    public static float toFloat(@Nullable String value) {
        return toFloat(value, 0.0f);
    }

    public static float toFloat(@Nullable String value, float fallback) {
        return SafeRun.convert(value, Float::parseFloat).orElse(fallback);
    }

    public static float toFloat(@Nullable String value, @NotNull Supplier<Float> fallback) {
        return SafeRun.convert(value, Float::parseFloat).orElseGet(fallback);
    }

    public static Optional<Float> toFloatOptional(@Nullable String value) {
        return SafeRun.convert(value, Float::parseFloat);
    }

}
