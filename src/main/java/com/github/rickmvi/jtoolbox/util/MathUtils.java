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
package com.github.rickmvi.jtoolbox.util;

import com.github.rickmvi.jtoolbox.util.constants.Constants;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.control.While;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.stream.IntStream;

import static com.github.rickmvi.jtoolbox.util.ArrayUtils.length;

/**
 * Provides utility methods for performing basic mathematical operations
 * including addition, subtraction, and multiplication across primitive
 * numeric data types such as {@code byte}, {@code short}, {@code int},
 * {@code long}, {@code float}, and {@code double}.
 * <p>
 * The methods in this class ensure arithmetic precision and will throw
 * exceptions in cases of overflow, underflow, or invalid input.
 */
@UtilityClass
@SuppressWarnings("unused")
public class MathUtils {

    public static double random() {
        return Math.random();
    }

    public static double random(double min, double max) {
        return Math.random() * (subtractDouble(max, min)) + min;
    }

    public static double random(double min, double max, double delta) {
        return Math.random() * (subtractDouble(max, min)) + min + delta;
    }

    public static double random(double min, double max, double delta, double minDelta, double maxDelta) {
        return Math.random() * (subtractDouble(max, min)) + min + delta * (Math.random() * (subtractDouble(maxDelta, minDelta)) + minDelta);
    }

    public static byte sumByte(byte @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;

        byte result = 0;
        for (byte number : numbers) {
            result = (byte) Math.addExact(result, number);
        }
        return result;
    }

    public static short sumShort(short @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;

        short result = 0;
        for (short number : numbers) {
            result = (short) Math.addExact(result, number);
        }
        return result;
    }

    public static int sumInt(int @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;

        int result = 0;
        for (int number : numbers) {
            result = Math.addExact(result, number);
        }
        return result;
    }

    public static long sumLong(long @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0L;

        long result = 0L;
        for (long number : numbers) {
            result = Math.addExact(result, number);
        }
        return result;
    }

    public static float sumFloat(float @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0.0f;

        float result = 0.0f;
        for (float number : numbers) {
            result += number;

            If.Throws(Float.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Float.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "float"))
            );
        }
        return result;
    }

    public static double sumDouble(double @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0.0d;

        double result = 0.0d;
        for (double number : numbers) {
            result += number;

            If.Throws(Double.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Double.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "double"))
            );
        }
        return result;
    }

    public static byte subtractByte(byte @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;
        int length = length(numbers);
        byte result = numbers[0];
        for (int i = 1; i < length; i++) {
            result = (byte) Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    public static short subtractShort(short @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;
        int length = length(numbers);
        short result = numbers[0];
        for (int i = 1; i < length; i++) {
            result = (short) Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    public static int subtractInt(int @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0;
        int length = length(numbers);
        int result = numbers[0];
        for (int i = 1; i < length; i++) {
            result = Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    public static long subtractLong(long @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0L;
        int length = length(numbers);
        long result = numbers[0];
        for (int i = 1; i < length; i++) {
            result = Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    public static float subtractFloat(float @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0.0f;
        int length = length(numbers);
        float result = numbers[0];
        for (int i = 1; i < length; i++) {
            result -= numbers[i];

            If.Throws(Float.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUBTRACT_OVERFLOW));
            If.Throws(Float.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "float"))
            );
        }
        return result;
    }

    public static double subtractDouble(double @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 0.0;
        int length = length(numbers);
        double result = numbers[0];
        for (int i = 1; i < length; i++) {
            result -= numbers[i];

            If.Throws(Double.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUBTRACT_OVERFLOW));
            If.Throws(Double.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "double"))
            );
        }
        return result;
    }

    public static byte multiplyByte(byte @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1;

        byte result = 1;
        for (byte number : numbers) {
            result = (byte) Math.multiplyExact(result, number);
        }
        return result;
    }

    public static short multiplyShort(short @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1;

        short result = 1;
        for (short number : numbers) {
            result = (short) Math.multiplyExact(result, number);
        }
        return result;
    }

    public static int multiplyInt(int @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1;

        int result = 1;
        for (int number : numbers) {
            result = Math.multiplyExact(result, number);
        }
        return result;
    }

    public static long multiplyLong(long @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1L;

        long result = 1L;
        for (long number : numbers) {
            result = Math.multiplyExact(result, number);
        }
        return result;
    }

    public static float multiplyFloat(float @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1.0f;

        float result = 1.0f;
        for (float number : numbers) {
            result *= number;

            If.Throws(
                    Float.isInfinite(result),
                    () -> new ArithmeticException(Constants.PRODUCT_OVERFLOW)
            );
            If.Throws(
                    Float.isNaN(result),
                    () -> new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "float"))
            );
        }
        return result;
    }

    public static double multiplyDouble(double @NotNull ... numbers) {
        if (ArrayUtils.isEmpty(numbers)) return 1.0;

        double result = 1.0;
        for (double number : numbers) {
            result *= number;

            If.Throws(
                    Double.isInfinite(result),
                    () -> new ArithmeticException(Constants.PRODUCT_OVERFLOW)
            );
            If.Throws(
                    Double.isNaN(result),
                    () -> new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "double"))
            );
        }
        return result;
    }

    public static byte divideByte(byte dividend, byte divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return (byte) Math.divideExact(dividend, divisor);
    }

    public static short divideShort(short dividend, short divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return (short) Math.divideExact(dividend, divisor);
    }

    public static int divideInt(int dividend, int divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return Math.divideExact(dividend, divisor);
    }

    public static long divideLong(long dividend, long divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return Math.divideExact(dividend, divisor);
    }

    public static float divideFloat(float dividend, float divisor) {
        return (float) divideDouble(dividend, divisor);
    }

    public static double divideDouble(double dividend, double divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));

        return dividend / divisor;
    }

    public static int divideNearestMultipleInt(int dividend, int divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));

        final int[] result = { dividend };
        While.isTrue(
                () -> result[0] % divisor != 0,
                () -> {
                    If.Throws(
                            result[0] == Integer.MIN_VALUE,
                            () -> new ArithmeticException(Constants.OVERFLOW_DIVISIBLE_NUMBER)
                    );
                    result[0]--;
                });
        return result[0] / divisor;
    }

    public static double averageDouble(long @NotNull ... numbers) {
        If.Throws(
                ArrayUtils.isEmpty(numbers),
                () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY)
        );
        return (double) sumLong(numbers) / length(numbers);
    }

    public static float averageFloat(float @NotNull ... numbers) {
        If.Throws(
                ArrayUtils.isEmpty(numbers),
                () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY)
        );
        return sumFloat(numbers) / length(numbers);
    }

    public static double averageInt(int @NotNull ... numbers) {
        If.Throws(
                ArrayUtils.isEmpty(numbers),
                () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY)
        );
        return (double) sumInt(numbers) / length(numbers);
    }

    public static double averageDouble(double @NotNull ... numbers) {
        If.Throws(
                ArrayUtils.isEmpty(numbers),
                () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY)
        );
        return sumDouble(numbers) / length(numbers);
    }

    public static byte evenByte(byte number) {
        byte valid = Numbers.requiredNonNegative(number);
        byte result = 0;
        for (int i = 0; i <= valid; i += 2) {
            result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    public static byte oddByte(byte number) {
        byte valid = Numbers.requiredPositive(number);
        byte result = 0;
        for (int i = 1; i <= valid; i += 2) {
            result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    public static short evenShort(short number) {
        short valid = Numbers.requiredNonNegative(number);
        short result = 0;
        for (int i = 0; i <= valid; i += 2) {
            result = (short) Math.addExact(result, i);
        }
        return result;
    }

    public static short oddShort(short number) {
        short valid = Numbers.requiredPositive(number);
        short result = 0;
        for (int i = 1; i <= valid; i += 2) {
            result = (short) Math.addExact(result, i);
        }
        return result;
    }

    public static int evenInt(int number) {
        int valid = Numbers.requiredNonNegative(number);
        return IntStream.rangeClosed(0, valid).filter(Numbers::isEven).sum();
    }

    public static int oddInt(int number) {
        int valid = Numbers.requiredPositive(number);
        return IntStream.rangeClosed(1, valid).filter(Numbers::isOdd).sum();
    }

    public static long evenLong(long number) {
        long valid = Numbers.requiredNonNegative(number);
        long result = 0L;
        for (long i = 0; i <= valid; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    public static long oddLong(long number) {
        long valid = Numbers.requiredPositive(number);
        long result = 0L;
        for (long i = 1; i <= valid; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    public static float evenFloat(int number) {
        int valid = Numbers.nonNegativeNonZero(number);
        float result = 0f;
        for (int i = 0; i <= valid; i += 2) {
            result += i;
            If.Throws(Float.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Float.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "float"))
            );
        }
        return result;
    }

    public static float oddFloat(int number) {
        int valid = Numbers.requiredPositive(number);
        float result = 0f;
        for (int i = 1; i <= valid; i += 2) {
            result += i;
            If.Throws(Float.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Float.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "float"))
            );
        }
        return result;
    }

    public static double evenDouble(long number) {
        long valid = Numbers.requiredNonNegative(number);
        double result = 0;
        for (long i = 0; i <= valid; i += 2) {
            result += i;
            If.Throws(Double.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Double.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "double"))
            );
        }
        return result;
    }

    public static double oddDouble(long number) {
        long valid = Numbers.requiredPositive(number);
        double result = 0;
        for (long i = 1; i <= valid; i += 2) {
            result += i;
            If.Throws(Double.isInfinite(result), () ->
                    new ArithmeticException(Constants.SUM_OVERFLOW)
            );
            If.Throws(Double.isNaN(result), () ->
                    new ArithmeticException(
                            StringFormatter.format(Constants.INVALID_OPERATION, "double"))
            );
        }
        return result;
    }

    public static int moduloInt(int dividend, int divisor) {
        return (int) moduloLong(dividend, divisor);
    }

    public static long moduloLong(long dividend, long divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return dividend % divisor;
    }

    public static int moduloNearestMultiple(int dividend, int divisor) {
        If.Throws(Numbers.isNegative(dividend), () -> new ArithmeticException(Constants.DIVIDEND_NEGATIVE));
        If.Throws(Numbers.isNonPositive(divisor), () -> new ArithmeticException(Constants.DIVISOR_BE_POSITIVE));

        final int[] result = { dividend };
        While.isTrue(
                () -> result[0] % divisor != 0,
                () -> {
                    If.Throws(
                            result[0] == Integer.MAX_VALUE,
                            () -> new ArithmeticException(Constants.OVERFLOW_DIVISIBLE_NUMBER)
                    );
                    result[0]--;
                });
        return result[0] % divisor;
    }

    public static long moduloNearestMultiple(long dividend, long divisor) {
        If.Throws(dividend < 0L, () -> new ArithmeticException(Constants.DIVIDEND_NEGATIVE));

        final long[] result = { dividend };
        While.isTrue(
                () -> result[0] % divisor != 0L,
                () -> {
                    If.Throws(
                            result[0] == Long.MAX_VALUE,
                            () -> new ArithmeticException(Constants.OVERFLOW_DIVISIBLE_NUMBER)
                    );
                    result[0]--;
                });
        return result[0] % divisor;
    }

    public static int gcd(int a, int b) {
        if (Numbers.isZero(a)) return b;
        if (Numbers.isZero(b)) return a;

        int r = a % b;
        if (Numbers.isZero(r)) return b;

        return gcd(b, r);
    }

    public static int lcm(int a, int b) {
        if (Numbers.isZero(a) || Numbers.isZero(b)) return 0;
        return Math.abs(a / gcd(a, b) * b);
    }

    public static int pow(int base, int exponent) {
        If.Throws(Numbers.isNegative(exponent), () -> new ArithmeticException(Constants.NEGATIVE_EXPONENT));
        return powerBySquaring(base, exponent);
    }

    public static int pow(int base, long exponent) {
        If.Throws(Numbers.isNegative(exponent), () -> new ArithmeticException(Constants.NEGATIVE_EXPONENT));
        return powerBySquaring(base, exponent);
    }

    @ApiStatus.Internal
    private static int powerBySquaring(int base, long exponent) {
        int result = 1;
        result = computePower(exponent, result, base);

        return result;
    }

    @ApiStatus.Internal
    private static int computePower(long exponent, int result, int b) {
        while (Numbers.isPositive(exponent)) {
            if (Numbers.isOdd(exponent)) {
                result = Math.multiplyExact(result, b);
            }
            exponent >>= 1;
            if (Numbers.isPositive(exponent)) {
                b = Math.multiplyExact(b, b);
            }
        }

        return result;
    }

    public static double pow(double base, double exponent) {
        If.Throws(Numbers.isNegative(exponent), () -> new ArithmeticException(Constants.NEGATIVE_EXPONENT));

        double result = Math.pow(base, exponent);
        If.Throws(Double.isInfinite(result), () -> new ArithmeticException(Constants.NEGATIVE_EXPONENT));
        If.Throws(Double.isNaN(result), () -> new ArithmeticException(Constants.INVALID_EXPONENTIATION));
        return result;
    }

    public static int sqrtInt(int value) {
        If.Throws(Numbers.isNegative(value), () -> new ArithmeticException(Constants.NEGATIVE_VALUE));
        return (int) Math.round(Math.sqrt(value));
    }

    public static double sqrt(double value) {
        If.Throws(Numbers.isNegative(value), () -> new ArithmeticException(Constants.NEGATIVE_VALUE));
        return Math.sqrt(value);
    }
}
