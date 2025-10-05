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

import com.github.rickmvi.jtoolbox.control.Switch;

import java.util.Objects;

/**
 * Utility class for converting generic {@link Object} instances to primitive numeric types
 * ({@code byte}, {@code short}, {@code int}, {@code long}, {@code float}, {@code double}), {@code boolean} and {@code char}.
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
 *
 * @see NumberParser
 * @see Stringifier
 * @see Switch
 * @see Objects
 * @since 1.1
 */
@lombok.experimental.UtilityClass
public class TypeAdapter {

    public static byte toByte(Object o) {
        return Switch.<Object, Byte>on(o)
                .caseNull(() -> (byte) 0)
                .caseType(Number.class, Number::byteValue)
                .caseDefault(v -> NumberParser.toByte(Stringifier.valueOf(v)))
                .get();
    }

    public static short toShort(Object o) {
        return Switch.<Object, Short>on(o)
                .caseNull(() -> (short) 0)
                .caseType(Number.class, Number::shortValue)
                .caseDefault(v -> NumberParser.toShort(Stringifier.valueOf(v)))
                .get();
    }

    public static int toInt(Object o) {
        return Switch.<Object, Integer>on(o)
                .caseNull(() -> 0)
                .caseType(Number.class, Number::intValue)
                .caseDefault(v -> NumberParser.toInt(Stringifier.valueOf(v)))
                .get();
    }

    public static long toLong(Object o) {
        return Switch.<Object, Long>on(o)
                .caseNull(() -> 0L)
                .caseType(Number.class, Number::longValue)
                .caseDefault(v -> NumberParser.toLong(Stringifier.valueOf(v)))
                .get();
    }

    public static float toFloat(Object o) {
        return Switch.<Object, Float>on(o)
                .caseNull(() -> 0.0F)
                .caseType(Number.class, Number::floatValue)
                .caseDefault(v -> NumberParser.toFloat(Stringifier.valueOf(v)))
                .get();
    }

    public static double toDouble(Object o) {
        return Switch.<Object, Double>on(o)
                .caseNull(() -> 0.0D)
                .caseType(Number.class, Number::doubleValue)
                .caseDefault(v -> NumberParser.toDouble(Stringifier.valueOf(v)))
                .get();
    }

    public static boolean toBoolean(Object o) {
        return Switch.<Object, Boolean>on(o)
                .caseNull(() -> false)
                .caseType(Boolean.class, Boolean::booleanValue)
                .caseDefault(v -> BooleanParser.toBoolean(Stringifier.valueOf(v)))
                .get();
    }

    public static char toChar(Object o) {
        return Switch.<Object, Character>on(o)
                .caseNull(() -> (char) 0)
                .caseType(Character.class, Character::charValue)
                .caseType(Number.class, number -> (char) number.intValue())
                .caseDefault(Stringifier::charAtZero)
                .get();
    }
}

