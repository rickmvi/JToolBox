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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility class for converting strings to primitive Java types or their wrapper objects.
 * <p>
 * This class provides a unified API for safely converting string inputs to well-known types
 * such as {@code int}, {@code long}, {@code float}, {@code double}, {@code boolean}, and {@code String}.
 * <p>
 * It supports:
 * <ul>
 *   <li>Type-safe conversion using {@link Class} objects.</li>
 *   <li>Conversion using a {@link PrimitiveType} enum.</li>
 *   <li>Fallback support in case of nulls or parsing errors.</li>
 * </ul>
 * All conversion operations return {@link Optional} to prevent {@code NullPointerException}s
 * and enforce explicit handling of absent or invalid values.
 */
@lombok.experimental.UtilityClass
public final class TypeConverter {

    /**
     * Enum representing supported primitive or wrapper types for conversion.
     */
    public enum PrimitiveType {
        INT, LONG, DOUBLE, FLOAT, BOOLEAN, STRING
    }

    /**
     * Converts a string to a target type represented by a {@link Class} object.
     * <p>
     * The following target types are supported:
     * <ul>
     *   <li>{@code Integer.class}</li>
     *   <li>{@code Long.class}</li>
     *   <li>{@code Double.class}</li>
     *   <li>{@code Float.class}</li>
     *   <li>{@code Boolean.class}</li>
     *   <li>{@code String.class}</li>
     * </ul>
     *
     * @param value the string value to convert, may be {@code null}
     * @param type  the target class type to convert to
     * @param <T>   the generic target type
     * @return an {@link Optional} containing the converted value, or empty if conversion fails
     * @throws IllegalArgumentException if the type is not supported
     */
    @Contract("null, _ -> !null")
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> convertTo(@Nullable String value, @NotNull Class<T> type) {
        return TryConvert.convert(value, v -> {
            if (type == Integer.class)  return (T) Integer.valueOf(v);
            if (type == Long.class)     return (T) Long.valueOf(v);
            if (type == Double.class)   return (T) Double.valueOf(v);
            if (type == Float.class)    return (T) Float.valueOf(v);
            if (type == Boolean.class)  return (T) Boolean.valueOf(v);
            if (type == String.class)   return (T) v;
            throw new IllegalArgumentException("Type not supported: " + type);
        });
    }

    /**
     * Converts a string to a primitive value using a {@link PrimitiveType} enum.
     * <p>
     * The returned value is wrapped in an {@link Optional}, and may be empty if
     * the input is null or invalid for the specified type.
     *
     * @param value the string value to convert, may be {@code null}
     * @param type  the type to convert the string into
     * @return an {@link Optional} containing the converted value, or empty if conversion fails
     */
    @Contract("null, _ -> !null")
    public static Optional<?> convertTo(@Nullable String value, @NotNull PrimitiveType type) {
        return TryConvert.convert(value, v -> switch (type) {
            case INT ->     Integer.valueOf(v);
            case LONG ->    Long.valueOf(v);
            case DOUBLE ->  Double.valueOf(v);
            case FLOAT ->   Float.valueOf(v);
            case BOOLEAN -> Boolean.valueOf(v);
            case STRING ->  v;
        });
    }

    /**
     * Converts a string to a primitive value using a {@link PrimitiveType} and returns a fallback
     * value if the input is null or conversion fails.
     * <p>
     * This method is useful when you want to guarantee a non-null result, even if the input is invalid.
     *
     * @param value    the input string to convert, may be {@code null}
     * @param type     the target type to convert to
     * @param fallback the fallback value to return if conversion fails
     * @return the converted value, or {@code fallback} if conversion is unsuccessful
     */
    @Contract("null, _, _ -> !null")
    public static Object convert(@Nullable String value, @NotNull PrimitiveType type, @Nullable Object fallback) {
        return TryConvert.convert(value, s -> switch (type) {
            case INT ->     SafeNumberParser.toInt      (s, (Integer) fallback);
            case LONG ->    SafeNumberParser.toLong     (s, (Long)    fallback);
            case DOUBLE ->  SafeNumberParser.toDouble   (s, (Double)  fallback);
            case FLOAT ->   SafeNumberParser.toFloat    (s, (Float)   fallback);
            case BOOLEAN -> BooleanParser.toBoolean (s, (Boolean) fallback);
            case STRING ->  s != null ? s : fallback;
        });
    }
}
