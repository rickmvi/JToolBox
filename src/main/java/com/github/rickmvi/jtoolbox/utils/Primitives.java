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
package com.github.rickmvi.jtoolbox.utils;

import com.github.rickmvi.jtoolbox.control.ifs.If;
import com.github.rickmvi.jtoolbox.utils.constants.Constants;
import com.github.rickmvi.jtoolbox.debug.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

/**
 * Utility class for primitive type operations.
 * Provides methods for comparison, validation, and other operations
 * across various primitive types such as byte, short, int, long, and float.
 */
@UtilityClass
@SuppressWarnings("unused")
public final class Primitives {

    /* ================================= Byte Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(byte x, byte y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean notEquals(byte x, byte y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(byte value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(byte value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(byte value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(byte x, byte y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(byte x, byte y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(byte x, byte y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(byte x, byte y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqualOrNegative(byte x, byte y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqualOrIsNegative(byte x, byte y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static byte requiredNonNegative(byte value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static byte requiredNonNegative(byte value, byte fallback) {
        if (isNegative(value)) {
            if (isNegative(fallback)) return 0;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract(value = "_, _ -> param1", pure = true)
    public static byte requiredNonNegative(byte value, @Nullable Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static byte nonNegative(byte value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public static byte requiredPositive(byte value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static byte requiredPositive(byte value, byte fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static byte requiredPositive(byte value, Supplier<String> supplierMessage) {
        If.trueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static byte nonNegativeNonZero(byte value) {
        return isNonPositive(value) ? 1 : value;
    }

    @Contract(pure = true)
    public static boolean isEven(byte value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(byte value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(byte value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static int compare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    @Contract(pure = true)
    public static int compareUnsigned(byte x, byte y) {
        return Byte.compareUnsigned(x, y);
    }

    /* ================================= Short Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(short x, short y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean notEquals(short x, short y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(short value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(short value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(short value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(short x, short y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(short x, short y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(short x, short y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(short x, short y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqualOrNegative(short x, short y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqualOrIsNegative(short x, short y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static short requiredNonNegative(short value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static short requiredNonNegative(short value, short fallback) {
        if (isNegative(value)) {
            if (isNegative(fallback)) return 0;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static short requiredNonNegative(short value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static short nonNegative(short value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public static short requiredPositive(short value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static short requiredPositive(short value, short fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static short requiredPositive(short value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static short nonNegativeNonZero(short value) {
        return isNonPositive(value) ? 1 : value;
    }

    @Contract(pure = true)
    public static boolean isEven(short value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(short value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(short value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static int compare(short x, short y) {
        return Short.compare(x, y);
    }

    @Contract(pure = true)
    public static int compareUnsigned(short x, short y) {
        return Short.compareUnsigned(x, y);
    }

    /* ================================= Int Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(int x, int y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean notEquals(int x, int y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(int value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(int value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(int value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(int x, int y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(int x, int y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(int x, int y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    public static boolean isLessOrEqual(int x, int y) {
        return equals(x, y) || isLessThan(x, y);
    }

    public static boolean isGreaterOrEqualOrNegative(int x, int y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    public static boolean isLessOrEqualOrIsNegative(int x, int y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static int requiredNonNegative(int value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    public static int requiredNonNegative(int value, int fallback) {
        if (isNegative(value)) {
            if (isNegative(fallback)) return 0;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static int requiredNonNegative(int value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static int nonNegative(int value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public static int requiredPositive(int value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static int requiredPositive(int value, int fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static int requiredPositive(int value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static int nonNegativeNonZero(int value) {
        return isNonPositive(value) ? 1 : value;
    }

    @Contract(pure = true)
    public static boolean isEven(int value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(int value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(int value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static int compare(int x, int y) {
        return Integer.compare(x, y);
    }

    @Contract(pure = true)
    public static int compareUnsigned(int x, int y) {
        return Integer.compareUnsigned(x, y);
    }

    @Contract(pure = true)
    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /* ================================= Long Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(long x, long y) {
        return (x ^ y) == 0L;
    }

    @Contract(pure = true)
    public static boolean notEquals(long x, long y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(long value) {
        return value == 0L;
    }

    @Contract(pure = true)
    public static boolean isPositive(long value) {
        return value > 0L;
    }

    @Contract(pure = true)
    public static boolean isNegative(long value) {
        return value < 0L;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(long x, long y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(long x, long y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(long x, long y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(long x, long y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqualOrNegative(long x, long y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqualOrIsNegative(long x, long y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static long requiredNonNegative(long value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static long requiredNonNegative(long value, long fallback) {
        if (isNegative(value)) {
            Logger.warn(Constants.NON_NEGATIVE);
            if (isNegative(fallback)) return 0L;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static long requiredNonNegative(long value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static long nonNegative(long value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public static long requiredPositive(long value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static long requiredPositive(long value, long fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1L;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static long requiredPositive(long value, Supplier<String> supplierMessage) {
        If.trueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static long nonNegativeNonZero(long value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(long value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isEven(long value) {
        return (value & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(long value) {
        return (value & 1L) != 0;
    }

    @Contract(pure = true)
    public static long compare(long x, long y) {
        return Long.compare(x, y);
    }

    @Contract(pure = true)
    public static long compareUnsigned(long x, long y) {
        return Long.compareUnsigned(x, y);
    }

    /* ================================= Float Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(float x, float y) {
        return Float.compare(x, y) == 0f;
    }

    @Contract(pure = true)
    public static boolean notEquals(float x, float y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(float value) {
        return value == 0f;
    }

    @Contract(pure = true)
    public static boolean isPositive(float value) {
        return value > 0f;
    }

    @Contract(pure = true)
    public static boolean isNegative(float value) {
        return value < 0f;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(float x, float y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(float x, float y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(float x, float y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(float x, float y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqualOrNegative(float x, float y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqualOrIsNegative(float x, float y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static float requiredNonNegative(float value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static float requiredNonNegative(float value, float fallback) {
        if (isNegative(value)) {
            if (isNegative(fallback)) return 0.0f;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static float requiredNonNegative(float value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new
                    IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static float nonNegative(float value) {
        return isNegative(value) ? 0.0f : value;
    }

    @Contract("_ -> param1")
    public static float requiredPositive(float value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static float requiredPositive(float value, float fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1.0f;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static float requiredPositive(float value, Supplier<String> supplierMessage) {
        If.trueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static float nonNegativeNonZero(float value) {
        return isNonPositive(value) ? 1.0f : value;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(float value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isEven(float value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(float value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    @Contract(pure = true)
    public static float compare(float x, float y) {
        return Float.compare(x, y);
    }

    @Contract(pure = true)
    public static boolean isNaN(float value) {
        return Float.isNaN(value);
    }

    @Contract(pure = true)
    public static boolean isInfinite(float value) {
        return Float.isInfinite(value);
    }

    /* ================================= Double Methods  ================================= */

    @Contract(pure = true)
    public static boolean equals(double x, double y) {
        return Double.compare(x, y) == 0;
    }

    @Contract(pure = true)
    public static boolean notEquals(double x, double y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean isZero(double value) {
        return Double.compare(value, 0.0d) == 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(double value) {
        return Double.compare(value, 0.0d) > 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(double value) {
        return Double.compare(value, -0.0d) < 0;
    }

    @Contract(pure = true)
    public static boolean isGreaterThan(double x, double y) {
        return x > y;
    }

    @Contract(pure = true)
    public static boolean isLessThan(double x, double y) {
        return x < y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(double x, double y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(double x, double y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqualOrNegative(double x, double y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public static boolean isLessOrEqualOrIsNegative(double x, double y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract("_ -> param1")
    public static double requiredNonNegative(double value) {
        If.trueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static double requiredNonNegative(double value, double fallback) {
        if (isNegative(value)) {
            if (isNegative(fallback)) return 0.0d;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static double requiredNonNegative(double value, Supplier<String> supplierMessage) {
        If.trueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.NON_NEGATIVE));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static double nonNegative(double value) {
        return isNegative(value) ? 0.0 : value;
    }

    @Contract("_ -> param1")
    public static double requiredPositive(double value) {
        If.trueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static double requiredPositive(double value, double fallback) {
        if (isNonPositive(value)) {
            if (isNonPositive(fallback)) return 1.0d;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static double requiredPositive(double value, Supplier<String> supplierMessage) {
        If.trueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    If.messageOrDefault(supplierMessage, Constants.POSITIVE_VALUE));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public static double nonNegativeNonZero(double value) {
        return isNonPositive(value) ? 1.0d : value;
    }

    @Contract(pure = true)
    public static boolean isEven(double value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(double value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    @Contract(pure = true)
    public static boolean isNonPositive(double value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static double compare(double x, double y) {
        return Double.compare(x, y);
    }

    @Contract(pure = true)
    public static boolean isNaN(double value) {
        return Double.isNaN(value);
    }

    @Contract(pure = true)
    public static boolean isInfinite(double value) {
        return Double.isInfinite(value);
    }

    /* ================================= Assistants Methods  ================================= */

    @ApiStatus.Internal
    @Contract(pure = true)
    private static boolean isIntegral(double value) {
        return !isNaN(value) && !isInfinite(value) && value % 1 == 0;
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private static boolean isIntegral(float value) {
        return !isNaN(value) && !isInfinite(value) && value % 1 == 0;
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    public static @NotNull IllegalArgumentException getValueCannotBeNegative() {
        return new IllegalArgumentException(Constants.NON_NEGATIVE);
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    public static @NotNull IllegalArgumentException getValueMustBePositive() {
        return new IllegalArgumentException(Constants.POSITIVE_VALUE);
    }
}
