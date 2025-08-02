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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A utility class providing methods for generating a string representation of various objects.
 * The methods in this class are designed to safely handle {@code null} inputs, collections, arrays,
 * maps, and other object types. Default values are provided to replace {@code null} when needed.
 */
@lombok.experimental.UtilityClass
public class ToString {

    /**
     * Converts the given object to its string representation.
     * <p>
     * If the object is {@code null}, the method returns the string "null".
     * Otherwise, it returns the result of calling {@code String.valueOf(o)}.
     *
     * @param o the object to convert to a string, may be {@code null}
     * @return the string representation of the object, or "null" if the object is {@code null}
     * @throws RuntimeException if {@code String.valueOf(o)} throws an exception
     */
    @Contract(value = "null -> !null", pure = true)
    public static String valueOf(@Nullable Object o) {
        return o == null ? "null" : String.valueOf(o);
    }

    /**
     * Converts the given object to a string representation.
     * <p>
     * If the given object is {@code null}, the string "null" is returned.
     * For non-null objects, the method attempts to call the {@code toString()}
     * method of the object to get its string representation,
     * falling back to default representations for special cases (e.g., arrays, collections).
     *
     * @param o the object to be converted to a string, may be {@code null}
     * @return a non-null string representation of the object, or "null" if the object is {@code null}
     * @throws RuntimeException if any exception occurs during the string conversion process
     */
    @Contract("null -> !null")
    public static @NotNull String toString(@Nullable Object o) {
        return toString(o, "null");
    }

    /**
     * Converts the given object to its string representation, or uses the specified default value
     * if the object is {@code null}.
     * <p>
     * The method handles various object types, including strings, numbers, booleans, characters,
     * enums, collections, maps, arrays, and exceptions. If none of these types are matched, the
     * object's {@code toString()} method is used. If an error occurs during conversion, a generic
     * error message is returned.
     *
     * @param o the object to be converted to a string; may be {@code null}
     * @param defaultValue the default string to return if {@code o} is {@code null}; must not be {@code null}
     * @return a non-null string representation of the object, or the specified {@code defaultValue}
     *         if the object is {@code null}
     * @throws NullPointerException if {@code defaultValue} is {@code null}
     */
    @Contract("null,_-> param2")
    public static @NotNull String toString(@Nullable Object o, @NotNull String defaultValue) {
        if (o == null) return defaultValue;

        try {
            if (o instanceof String) return (String) o;
            if (o instanceof Number || o instanceof Boolean || o instanceof Character) return o.toString();

            switch (o) {
                case Enum<?> anEnum -> {
                    return anEnum.name();
                }
                case Throwable throwable -> {
                    return throwable.toString();
                }
                case Collection<?> objects -> {
                    return toCollectionString(objects);
                }
                case Map<?, ?> map -> {
                    return toMapString(map);
                }
                default -> {
                }
            }
            if (o.getClass().isArray()) return toArrayString(o);

            String result = o.toString();
            if (result == null || result.trim().isEmpty()) {
                return o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode());
            }
            return result;
        } catch (Exception e) {
            return "Error@toString(" + e.getClass().getSimpleName() + ")";
        }
    }

    @ApiStatus.Internal
    private static @NotNull String toArrayString(Object array) {
        if (array instanceof Object[])  return Arrays.toString((Object[])   array);
        if (array instanceof int[])     return Arrays.toString((int[])      array);
        if (array instanceof long[])    return Arrays.toString((long[])     array);
        if (array instanceof double[])  return Arrays.toString((double[])   array);
        if (array instanceof float[])   return Arrays.toString((float[])    array);
        if (array instanceof boolean[]) return Arrays.toString((boolean[])  array);
        if (array instanceof char[])    return Arrays.toString((char[])     array);
        if (array instanceof byte[])    return Arrays.toString((byte[])     array);
        if (array instanceof short[])   return Arrays.toString((short[])    array);
        return "UnknownArray@" + array.getClass().getComponentType();
    }

    @ApiStatus.Internal
    private static @NotNull String toCollectionString(@NotNull Collection<?> col) {
        return "[" + col.stream()
                .map(ToString::toString)
                .reduce((a, b) -> a + ", " + b).orElse("") + "]";
    }

    @ApiStatus.Internal
    private static @NotNull String toMapString(@NotNull Map<?, ?> map) {
        return "{" + map.entrySet().stream()
                .map(e -> toString(e.getKey()) + "=" + toString(e.getValue()))
                .reduce((a, b) -> a + ", " + b).orElse("") + "}";
    }
}
