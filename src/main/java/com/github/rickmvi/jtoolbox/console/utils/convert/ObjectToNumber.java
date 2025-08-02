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

/**
 * Utility class for converting generic {@link Object} instances to primitive numeric types
 * ({@code int}, {@code long}, {@code float}, {@code double}) and {@code boolean}.
 * <p>
 * This class attempts to convert an object to the requested primitive type by:
 * <ul>
 *     <li>Checking if the object is already an instance of {@link Number} or {@link Boolean}
 *         and returning the corresponding primitive value.</li>
 *     <li>Otherwise, converting the object's {@code toString()} representation to the desired type
 *         using the appropriate parsing method.</li>
 * </ul>
 * <p>
 * This utility is useful when working with loosely typed data where values may come
 * as {@code Object} but need to be interpreted as primitives safely.
 * <p>
 * Note that if the object’s string representation is not a valid format for the target type,
 * these methods will throw runtime exceptions like {@link NumberFormatException}.
 */
@lombok.experimental.UtilityClass
public class ObjectToNumber {

    /**
     * Converts the given object to an {@code int} value.
     * <p>
     * If the object is an instance of {@link Number}, returns its integer value.
     * Otherwise, attempts to parse the object's string representation as an integer.
     *
     * @param o the object to convert, must not be {@code null}
     * @return the integer value represented by the object
     * @throws NumberFormatException if the string representation cannot be parsed as an integer
     */
    @SuppressWarnings("ConstantConditions")
    public static int toInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        return Integer.parseInt(ToString.valueOf(o));
    }

    /**
     * Converts the given object to a {@code long} value.
     * <p>
     * If the object is an instance of {@link Number}, returns its long value.
     * Otherwise, attempts to parse the object's string representation as a long.
     *
     * @param o the object to convert, must not be {@code null}
     * @return the long value represented by the object
     * @throws NumberFormatException if the string representation cannot be parsed as a long
     */
    @SuppressWarnings("ConstantConditions")
    public static long toLong(Object o) {
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(ToString.valueOf(o));
    }

    /**
     * Converts the given object to a {@code float} value.
     * <p>
     * If the object is an instance of {@link Number}, returns its float value.
     * Otherwise, attempts to parse the object's string representation as a float.
     *
     * @param o the object to convert, must not be {@code null}
     * @return the float value represented by the object
     * @throws NumberFormatException if the string representation cannot be parsed as a float
     */
    @SuppressWarnings("ConstantConditions")
    public static float toFloat(Object o) {
        if (o instanceof Number) return ((Number) o).floatValue();
        return Float.parseFloat(ToString.valueOf(o));
    }

    /**
     * Converts the given object to a {@code double} value.
     * <p>
     * If the object is an instance of {@link Number}, returns its double value.
     * Otherwise, attempts to parse the object's string representation as a double.
     *
     * @param o the object to convert, must not be {@code null}
     * @return the double value represented by the object
     * @throws NumberFormatException if the string representation cannot be parsed as a double
     */
    @SuppressWarnings("ConstantConditions")
    public static double toDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(ToString.valueOf(o));
    }

    /**
     * Converts the given object to a {@code boolean} value.
     * <p>
     * If the object is an instance of {@link Boolean}, returns its boolean value.
     * Otherwise, parses the object's string representation as a boolean.
     *
     * @param o the object to convert, must not be {@code null}
     * @return the boolean value represented by the object
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean toBoolean(Object o) {
        if (o instanceof Boolean) return (Boolean) o;
        return Boolean.parseBoolean(ToString.valueOf(o));
    }
}

