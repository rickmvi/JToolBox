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
package com.github.rickmvi.jtoolbox.control.internal;

import com.github.rickmvi.jtoolbox.collections.array.Array;
import com.github.rickmvi.jtoolbox.control.Conditionals;
import com.github.rickmvi.jtoolbox.control.Iteration;
import com.github.rickmvi.jtoolbox.control.While;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class providing arithmetic operations for integers, longs, floats, and doubles
 * including a sum, subtraction, multiplication, division, average, and modulo.
 */
@lombok.experimental.UtilityClass
public class MathUtils {

    /**
     * Computes the sum of an array of byte values.
     *
     * @param numbers an array of byte values to be summed; must not be null.
     * @return the sum of all byte values in the array. If the array is empty, it returns 0.
     * @throws NullPointerException if the provided array is null.
     * @throws ArithmeticException if the sum overflows the range of a byte.
     */
    public static byte sumByte(byte @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        byte result = 0;
        for (byte number : numbers) {
            result = (byte) Math.addExact(result, number);
        }
        return result;
    }

    /**
     * Computes the sum of the given short numbers. This method uses an exact
     * addition and will throw an exception if overflow occurs.
     *
     * @param numbers an array of short values to be summed; must not be null.
     * @return the sum of the provided short numbers.
     * @throws NullPointerException if the {@code numbers} array is null.
     * @throws ArithmeticException if the sum overflows the range of the {@code short} type.
     */
    public static short sumShort(short @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        short result = 0;
        for (short number : numbers) {
            result = (short) Math.addExact(result, number);
        }
        return result;
    }

    /**
     * Computes the sum of a variable number of {@code int} values.
     *
     * @param numbers the integers to be summed; must not be null. If no values are provided, the result is 0.
     * @return the sum of the provided numbers
     * @throws NullPointerException if the {@code numbers} array is null
     * @throws ArithmeticException if an integer overflow occurs while computing the sum
     */
    public static int sumInt(int @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        int result = 0;
        for (int number : numbers) {
            result = Math.addExact(result, number);
        }
        return result;
    }

    /**
     * Computes the sum of a variable number of {@code long} values.
     *
     * @param numbers the {@code long} values to be summed; must not be {@code null}.
     *                If no values are provided, the result is 0.
     * @return the sum of the provided {@code long} values.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if a numeric overflow occurs while computing the sum.
     */
    public static long sumLong(long @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0L;

        long result = 0L;
        for (long number : numbers) {
            result = Math.addExact(result, number);
        }
        return result;
    }

    /**
     * Computes the sum of a variable number of {@code float} values.
     *
     * @param numbers the {@code float} values to be summed; must not be {@code null}.
     *                If no values are provided, the result is 0.0f.
     * @return the sum of the provided {@code float} values.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if the sum operation results in an overflow, or if
     *         a non-numeric (NaN) value is encountered during the computation.
     */
    public static float sumFloat(float @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0.0f;

        float result = 0.0f;
        for (float number : numbers) {
            result += number;

            Conditionals.ifTrueThrow(Float.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Float.isNaN(result), () ->
                    new ArithmeticException("Invalid float operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Computes the sum of a variable number of {@code double} values.
     *
     * @param numbers the {@code double} values to be summed; must not be {@code null}.
     *                If no values are provided, the result is {@code 0.0}.
     * @return the sum of the provided {@code double} values.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if the sum operation results in an overflow, or if
     *                             a non-numeric (NaN) value is encountered during the computation.
     */
    public static double sumDouble(double @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0.0;

        double result = 0.0;
        for (double number : numbers) {
            result += number;

            Conditionals.ifTrueThrow(Double.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Double.isNaN(result), () ->
                    new ArithmeticException("Invalid double operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Subtracts all provided byte numbers in sequence and returns the result.
     * The subtraction starts with the first element of the array, and each later
     * number is subtracted from the current result.
     *
     * @param numbers A varargs array of byte values to be subtracted. Must not be null.
     * @return The result of subtracting all the provided numbers in a sequence.
     *         Returns 0 if the array is empty.
     * @throws NullPointerException If the input array is null.
     * @throws ArithmeticException If an arithmetic overflow occurs during subtraction.
     */
    public static byte subtractByte(byte @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        byte result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = (byte) Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    /**
     * Subtracts a series of short numbers provided as varargs and returns the result.
     * <p>
     * The method starts with the first element of the array as the initial value
     * and then subtracts each later number in the array from the result.
     *
     * @param numbers An array of short numbers to be subtracted.
     *                Must not be null and must contain at least one element.
     * @return The result of sequentially subtracting all the numbers.
     * @throws NullPointerException If the input array is null.
     * @throws ArithmeticException If the subtraction results in underflow or overflow for the short type.
     */
    public static short subtractShort(short @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        short result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = (short) Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    /**
     * Computes the result of subtracting a variable number of {@code int} values.
     * The subtraction is performed in the order the numbers are provided.
     *
     * @param numbers the integers to be subtracted; must not be {@code null}.
     *                If no values are provided, the result is {@code 0}.
     * @return the result of subtracting the provided integers in sequence.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if an integer overflow occurs during subtraction.
     */
    public static int subtractInt(int @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0;

        int result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    /**
     * Computes the result of subtracting a variable number of {@code long} values.
     * The subtraction is performed in the order the numbers are provided.
     *
     * @param numbers the {@code long} values to be subtracted; must not be {@code null}.
     *                If no values are provided, the result is {@code 0}.
     * @return the result of subtracting the provided {@code long} values in sequence.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if a numeric overflow occurs during subtraction.
     */
    public static long subtractLong(long @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0L;

        long result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = Math.subtractExact(result, numbers[i]);
        }
        return result;
    }

    /**
     * Computes the result of subtracting a variable number of {@code float} values.
     * The subtraction is performed sequentially in the order the numbers are provided.
     *
     * @param numbers the {@code float} values to be subtracted; must not be {@code null}.
     *                If no values are provided, the result is {@code 0.0f}.
     * @return the result of subtracting the provided {@code float} values in sequence.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if the subtraction results in an infinite value, or if
     *                             a non-numeric (NaN) value is encountered during the computation.
     */
    public static float subtractFloat(float @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0.0f;

        float result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result -= numbers[i];

            Conditionals.ifTrueThrow(Float.isInfinite(result), () ->
                    new ArithmeticException("Subtraction overflow"));
            Conditionals.ifTrueThrow(Float.isNaN(result), () ->
                    new ArithmeticException("Invalid float operation (NaN encountered)"));
        }
        return result;
    }

    /**
     * Computes the result of subtracting a variable number of {@code double} values.
     * The subtraction is performed sequentially in the order the numbers are provided.
     *
     * @param numbers an array of {@code double} values to be subtracted; must not be {@code null}.
     *                If no values are provided, the result is {@code 0.0}.
     * @return the result of subtracting the provided {@code double} values in sequence.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if the subtraction results in an infinite value, or if
     *                             a non-numeric (NaN) value is encountered during the computation.
     */
    public static double subtractDouble(double @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 0.0;

        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result -= numbers[i];

            Conditionals.ifTrueThrow(Double.isInfinite(result), () ->
                    new ArithmeticException("Subtraction overflow"));
            Conditionals.ifTrueThrow(Double.isNaN(result), () ->
                    new ArithmeticException("Invalid double operation (NaN encountered)"));
        }
        return result;
    }

    /**
     * Multiplies all the given byte numbers and returns the product.
     *
     * @param numbers an array of byte numbers to be multiplied; must not be null
     * @return the product of all byte numbers provided
     * @throws IllegalArgumentException if the input array is null
     * @throws ArithmeticException if an overflow occurs during multiplication
     */
    public static byte mutiplyByte(byte @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1;

        byte result = 1;
        for (byte number : numbers) {
            result = (byte) Math.multiplyExact(result, number);
        }
        return result;
    }

    /**
     * Multiplies a series of numbers and returns their product.
     *
     * @param numbers an array of short numbers to be multiplied. Must not be null.
     * @return the product of the given numbers as a short value. Returns 1 if the array is empty.
     * @throws NullPointerException if the input array is null.
     * @throws ArithmeticException if an overflow occurs during multiplication.
     */
    public static short mutiplyShort(short @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1;

        short result = 1;
        for (short number : numbers) {
            result = (short) Math.multiplyExact(result, number);
        }
        return result;
    }

    /**
     * Multiplies a variable number of {@code int} values.
     * If no values are provided, the result is {@code 1}.
     *
     * @param numbers the integers to be multiplied; must not be {@code null}.
     *                If no values are provided, the result is {@code 1}.
     * @return the product of the provided integers.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if an integer overflow occurs during multiplication.
     */
    public static int multiplyInt(int @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1;

        int result = 1;
        for (int number : numbers) {
            result = Math.multiplyExact(result, number);
        }
        return result;
    }

    /**
     * Computes the product of a variable number of {@code long} values.
     * If no values are provided, the result is {@code 1}.
     *
     * @param numbers the {@code long} values to be multiplied; must not be {@code null}.
     *                If no values are provided, the result is {@code 1}.
     * @return the product of the provided {@code long} values.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if a numeric overflow occurs during multiplication.
     */
    public static long multiplyLong(long @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1L;

        long result = 1L;
        for (long number : numbers) {
            result = Math.multiplyExact(result, number);
        }
        return result;
    }

    /**
     * Computes the product of a variable number of {@code float} values. If no values
     * are provided, the result is {@code 1.0f}.
     *
     * @param numbers the {@code float} values to be multiplied; must not be {@code null}.
     *                If no values are provided, the result is {@code 1.0f}.
     * @return the product of the provided {@code float} values.
     * @throws NullPointerException if the {@code numbers} array is {@code null}.
     * @throws ArithmeticException if the multiplication results in an infinite value, or
     *                             if a non-numeric (NaN) value is encountered during the computation.
     */
    public static float multiplyFloat(float @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1.0f;

        float result = 1.0f;
        for (float number : numbers) {
            result *= number;

            Conditionals.ifTrueThrow(
                    Float.isInfinite(result),
                    () -> new ArithmeticException("Product overflow")
            );
            Conditionals.ifTrueThrow(
                    Float.isNaN(result),
                    () -> new ArithmeticException("Invalid float operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Multiplies a variable number of double values and returns the product.
     * If no values are provided, the method returns 1.0 as the neutral element
     * of multiplication. Throws an exception if the calculation results in an
     * infinity or a NaN value.
     *
     * @param numbers A varargs parameter representing the numbers to be multiplied.
     *                Must not be null but can be empty.
     * @return The product of the provided numbers. If no numbers are provided,
     *         the method returns 1.0.
     * @throws ArithmeticException If the product results in a value of infinity
     *                              or NaN due to overflow or invalid operations.
     */
    public static double multiplyDouble(double @NotNull ... numbers) {
        if (Array.isEmpty(numbers)) return 1.0;

        double result = 1.0;
        for (double number : numbers) {
            result *= number;

            Conditionals.ifTrueThrow(
                    Double.isInfinite(result),
                    () -> new ArithmeticException("Product overflow")
            );
            Conditionals.ifTrueThrow(
                    Double.isNaN(result),
                    () -> new ArithmeticException("Invalid double operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Divides one byte value by another.
     * <p>
     * This method performs division of the specified dividend by the specified divisor
     * and returns the result as a byte. If the divisor is zero, an {@code ArithmeticException}
     * is thrown to indicate a division by zero error.
     *
     * @param dividend the byte value to be divided (numerator)
     * @param divisor the byte value by which to divide (denominator)
     * @return the result of the division as a byte
     * @throws ArithmeticException if the divisor is zero
     */
    public static byte divideByte(byte dividend, byte divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));
        return (byte) Math.divideExact(dividend, divisor);
    }

    /**
     * Divides the given dividend by the divisor and returns the result as a short.
     *
     * @param dividend the number to be divided
     * @param divisor the number by which the dividend is divided
     * @return the result of the division as a short
     * @throws ArithmeticException if the divisor is zero
     */
    public static short divideShort(short dividend, short divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));
        return (short) Math.divideExact(dividend, divisor);
    }

    /**
     * Divides the given dividend by the divisor, ensuring that division by zero
     * does not occur.
     *
     * @param dividend The number to be divided.
     * @param divisor The number by which the dividend is to be divided.
     * @return The integer result of the division.
     * @throws ArithmeticException If the divisor is zero.
     */
    public static int divideInt(int dividend, int divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));
        return Math.divideExact(dividend, divisor);
    }

    /**
     * Divides the given dividend by the specified divisor and returns the result.
     *
     * @param dividend the number to be divided
     * @param divisor the number by which the dividend is to be divided
     * @return the result of dividing the dividend by the divisor
     * @throws ArithmeticException if the divisor is zero
     */
    public static long divideLong(long dividend, long divisor) {
        Conditionals.ifTrueThrow(divisor == 0L, () -> new ArithmeticException("Division by zero"));
        return Math.divideExact(dividend, divisor);
    }

    public static float divideFloat(float dividend, float divisor) {
        return (float) divideDouble(dividend, divisor);
    }

    public static double divideDouble(double dividend, double divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));
        return dividend / divisor;
    }

    /**
     * Divides the given dividend by the divisor after adjusting the dividend to the nearest smaller number
     * that is evenly divisible by the divisor.
     *
     * @param dividend the number to be divided; the method will find the nearest smaller number that is divisible
     *                 by the divisor if the dividend itself is not divisible.
     * @param divisor the number by which the adjusted dividend will be divided; must not be zero.
     * @return the result of the division after adjusting the dividend to the nearest smaller divisible number.
     * @throws ArithmeticException if the divisor is zero or if an overflow occurs while searching for the
     *                             nearest divisible number.
     */
    public static int divideNearestMultipleInt(int dividend, int divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));

        final int[] result = { dividend };
        While.whileTrue(
                () -> result[0] % divisor != 0,
                () -> {
                    Conditionals.ifTrueThrow(
                            result[0] == Integer.MIN_VALUE,
                            () -> new ArithmeticException("Overflow while searching nearest divisible number")
                    );
                    result[0]--;
                });
        return result[0] / divisor;
    }

    /**
     * Computes the average of the given array of numbers. The method expects at least one number
     * to be present in the array. An exception is thrown if the array is empty.
     *
     * @param numbers an array of numbers for which the average is to be calculated; must not be null
     * @return the average of the provided numbers as a double value
     * @throws ArithmeticException if the input array is empty
     */
    public static double averageDouble(long @NotNull ... numbers) {
        Conditionals.ifTrueThrow(
                Array.isEmpty(numbers),
                () -> new ArithmeticException("Cannot average an empty array")
        );
        return (double) sumLong(numbers) / numbers.length;
    }

    /**
     * Computes the average of a given array of float numbers.
     *
     * @param numbers A non-null array of float numbers. Must not be empty.
     * @return The average value of the provided numbers.
     * @throws ArithmeticException If the provided array is empty.
     */
    public static float averageFloat(float @NotNull ... numbers) {
        Conditionals.ifTrueThrow(
                Array.isEmpty(numbers),
                () -> new ArithmeticException("Cannot average an empty array")
        );
        return sumFloat(numbers) / numbers.length;
    }

    /**
     * Calculates the average of a given set of integers.
     *
     * @param numbers the integers to be averaged; must not be null. Passing an empty array results in an exception.
     * @return the average value of the provided integers as a double.
     * @throws ArithmeticException if the input array is empty.
     */
    public static double averageInt(int @NotNull ... numbers) {
        Conditionals.ifTrueThrow(
                Array.isEmpty(numbers),
                () -> new ArithmeticException("Cannot average an empty array")
        );
        return (double) sumInt(numbers) / numbers.length;
    }

    /**
     * Computes the average of the provided array of double values.
     *
     * @param numbers the array of double values to compute the average of; must not be null.
     * @return the average of the values in the array.
     * @throws ArithmeticException if the array is empty.
     * @throws NullPointerException if the array is null.
     */
    public static double averageDouble(double @NotNull ... numbers) {
        Conditionals.ifTrueThrow(
                Array.isEmpty(numbers),
                () -> new ArithmeticException("Cannot average an empty array")
        );
        return sumDouble(numbers) / numbers.length;
    }

    /**
     * Calculates the sum of all even numbers from 0 to the given byte value inclusively.
     *
     * @param number the upper limit up to which even numbers are summed; must be within the valid byte range.
     * @return the sum of all even numbers from 0 to the provided limit as a byte.
     * @throws ArithmeticException if the sum exceeds the range of the byte type.
     */
    @Contract(pure = true)
    public static byte evenByte(byte number) {
        byte result = 0;
        for (int i = 0; i <= number; i++) {
            if (i % 2 == 0) result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Calculates the sum of all odd integers from 1 up to the given number.
     *
     * @param number the inclusive upper bound up to which odd numbers are summed.
     *               Must be a non-negative byte value.
     * @return the sum of all odd integers from 1 to the specified number.
     * @throws ArithmeticException if an integer overflow occurs during the summation process.
     */
    @Contract(pure = true)
    public static byte oddByte(byte number) {
        byte result = 0;
        for (int i = 0; i <= number; i++) {
            if (i % 2 != 0) result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Computes the sum of all even short integers from 0 up to and including
     * the specified number.
     *
     * @param number the upper bound short value up to which even numbers
     *               will be summed
     * @return the sum of all even short integers from 0 to the specified number
     * @throws ArithmeticException if an arithmetic overflow occurs during the summation
     */
    @Contract(pure = true)
    public static short evenShort(short number) {
        short result = 0;
        for (int i = 0; i <= number; i++) {
            if (i % 2 == 0) result = (short) Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Calculates the sum of all odd numbers from 0 to the given number (inclusive).
     *
     * @param number the upper bound to calculate the sum of odd numbers; must be a non-negative short value.
     * @return the sum of all odd numbers from 0 to the specified number (inclusive).
     * @throws ArithmeticException if the sum exceeds the range of the {@code short} type.
     */
    @Contract(pure = true)
    public static short oddShort(short number) {
        short result = 0;
        for (int i = 0; i <= number; i++) {
            if (i % 2 != 0) result = (short) Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Computes the sum of all even integers from 0 up to and including the given number.
     *
     * @param number the upper limit (inclusive) up to which the sum of even integers is calculated
     * @return the sum of all even integers from 0 to the specified number
     * @throws ArithmeticException if an integer overflow occurs during the computation
     */
    @Contract(pure = true)
    public static int evenInt(int number) {
        int result = 0;
        for (int i = 0; i <= number; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Calculates the sum of all odd integers from 1 up to and including the specified number.
     * If the number is lower than 1, the return value will be 0.
     *
     * @param number the upper bound up to which odd integers will be summed, inclusive
     * @return the sum of all odd integers from 1 to the given number, inclusive
     * @throws ArithmeticException if an integer overflow occurs during the calculation
     */
    @Contract(pure = true)
    public static int oddInt(int number) {
        int result = 0;
        for (int i = 1; i <= number; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Calculates the sum of all even numbers from 0 up to and including the specified number.
     *
     * @param number The upper limit up to which even numbers are summed. Must be a non-negative value.
     * @return The sum of all even numbers from 0 to the specified number.
     * @throws ArithmeticException If the result overflows a {@code long}.
     */
    @Contract(pure = true)
    public static long evenLong(long number) {
        long result = 0L;
        for (long i = 0; i <= number; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Calculates the sum of all odd numbers from 1 up to and including the specified number.
     *
     * @param number the upper limit up to which odd numbers are summed, inclusive
     * @return the sum of all odd numbers between 1 and the specified number, inclusive
     * @throws ArithmeticException if the sum overflows a {@code long}
     */
    @Contract(pure = true)
    public static long oddLong(long number) {
        long result = 0L;
        for (long i = 1; i <= number; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    /**
     * Computes the sum of all even integers from 0 up to and including the given number
     * (if the number itself is even) and returns the result as a floating-point value.
     *
     * @param number the upper bound up to which even numbers are summed (inclusive if even)
     * @return the sum of all even integers from 0 to the specified number as a float
     * @throws ArithmeticException if the computed sum overflows to infinity or results in NaN
     */
    @Contract(pure = true)
    public static float evenFloat(int number) {
        float result = 0f;
        for (int i = 0; i <= number; i += 2) {
            result += i;
            Conditionals.ifTrueThrow(Float.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Float.isNaN(result), () ->
                    new ArithmeticException("Invalid float operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Calculates the sum of all odd integers from 1 up to the given number, returning the result as a float.
     * Ensures the computed sum does not result in overflow or NaN conditions.
     *
     * @param number the upper limit for summing odd integers; must be a non-negative integer.
     * @return the sum of all odd integers from 1 up to the given number as a float.
     * @throws ArithmeticException if the resulting sum overflows the float range or if a NaN value is encountered.
     */
    @Contract(pure = true)
    public static float oddFloat(int number) {
        float result = 0f;
        for (int i = 1; i <= number; i += 2) {
            result += i;
            Conditionals.ifTrueThrow(Float.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Float.isNaN(result), () ->
                    new ArithmeticException("Invalid float operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Computes the sum of all even numbers from 0 to the specified number (inclusive) and returns the result as a double.
     *
     * @param number the maximum number up to which even numbers will be summed, inclusive
     * @return the sum of all even numbers from 0 to {@code number} as a double
     * @throws ArithmeticException if the resulting sum overflows or produces an invalid double value (e.g., NaN)
     */
    @Contract(pure = true)
    public static double evenDouble(long number) {
        double result = 0;
        for (long i = 0; i <= number; i += 2) {
            result += i;
            Conditionals.ifTrueThrow(Double.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Double.isNaN(result), () ->
                    new ArithmeticException("Invalid double operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Calculates the sum of all odd numbers from 1 up to the specified number
     * and returns the result as a double.
     * Throws an exception if overflow or invalid operations occur during the sum.
     *
     * @param number the upper bound (inclusive) up to which odd numbers are summed
     * @return the sum of all odd numbers from 1 up to the specified number
     * @throws ArithmeticException if the summation results in a value that is infinite or NaN
     */
    @Contract(pure = true)
    public static double oddDouble(long number) {
        double result = 0;
        for (long i = 1; i <= number; i += 2) {
            result += i;
            Conditionals.ifTrueThrow(Double.isInfinite(result), () ->
                    new ArithmeticException("Sum overflow")
            );
            Conditionals.ifTrueThrow(Double.isNaN(result), () ->
                    new ArithmeticException("Invalid double operation (NaN encountered)")
            );
        }
        return result;
    }

    /**
     * Calculates the remainder of the division between the given dividend and divisor.
     *
     * @param dividend the number to be divided
     * @param divisor the number by which the dividend is divided
     * @return the remainder of the division
     * @throws ArithmeticException if the divisor is zero
     */
    @Contract(pure = true)
    public static int moduloInt(int dividend, int divisor) {
        return (int) moduloLong(dividend, divisor);
    }

    /**
     * Computes the remainder when the divisor divides the dividend.
     * The operation adheres to the mathematical definition of modulo,
     * ensuring valid computation unless the divisor is zero.
     *
     * @param dividend The number to be divided.
     * @param divisor The number by which the dividend is divided.
     *                Must not be zero.
     * @return The remainder after dividing the dividend by the divisor.
     * @throws ArithmeticException If the divisor is zero.
     */
    @Contract(pure = true)
    public static long moduloLong(long dividend, long divisor) {
        Conditionals.ifTrueThrow(divisor == 0, () -> new ArithmeticException("Division by zero"));
        return dividend % divisor;
    }

    /**
     * Calculates and returns the modulo of the nearest number less than or equal
     * to the given dividend that is divisible by the specified divisor. The method
     * ensures the dividend is non-negative and performs the operation iteratively.
     *
     * @param dividend the number to be reduced to the nearest multiple of the divisor;
     *                 must be non-negative
     * @param divisor the number by which to find the nearest multiple; must be positive
     * @return the modulo of the nearest divisible number less than or equal to the dividend
     * @throws ArithmeticException if the dividend is negative or underflow occurs
     *                             while searching for the nearest divisible number
     */
    public static int moduloNearestMultiple(int dividend, int divisor) {
        Conditionals.ifTrueThrow(dividend < 0, () -> new ArithmeticException("Negative dividend"));

        final int[] result = { dividend };
        While.whileTrue(
                () -> result[0] % divisor != 0,
                () -> {
                    Conditionals.ifTrueThrow(
                            result[0] == Integer.MAX_VALUE,
                            () -> new ArithmeticException("Underflow while searching nearest divisible number")
                    );
                    result[0]--;
                });
        return result[0] % divisor;
    }

    /**
     * Computes the modulo operation for the nearest multiple of the divisor
     * that is less than or equal to the given dividend. The method ensures
     * that the dividend is adjusted downwards until a number is found that
     * is divisible by the divisor.
     *
     * @param dividend the value for which the nearest multiple of the divisor
     *                 is to be found. Must be non-negative.
     * @param divisor the value by which the dividend is to be divided.
     * @return the remainder when the nearest divisible number (less than or
     *         equal to the dividend) is divided by the divisor.
     * @throws ArithmeticException if the dividend is negative or if the
     *         search results in underflow.
     */
    public static long moduloNearestMultiple(long dividend, long divisor) {
        Conditionals.ifTrueThrow(dividend < 0L, () -> new ArithmeticException("Negative dividend"));

        final long[] result = { dividend };
        While.whileTrue(
                () -> result[0] % divisor != 0L,
                () -> {
                    Conditionals.ifTrueThrow(
                            result[0] == Long.MAX_VALUE,
                            () -> new ArithmeticException("Underflow while searching nearest divisible number")
                    );
                    result[0]--;
                });
        return result[0] % divisor;
    }

    /**
     * Computes the greatest common divisor (GCD) of two integers using the Euclidean algorithm.
     *
     * @param a the first integer
     * @param b the second integer
     * @return the greatest common divisor of {@code a} and {@code b}
     * @throws ArithmeticException if both {@code a} and {@code b} are zero, as GCD is undefined in that case
     */
    @Contract(pure = true)
    public static int gcd(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;

        int r = a % b;
        if (r == 0) return b;

        return gcd(b, r);
    }

    /**
     * Computes the least common multiple (LCM) of two integers.
     *
     * @param a the first integer; can be positive, negative, or zero
     * @param b the second integer; can be positive, negative, or zero
     * @return the least common multiple of the two integers, or 0 if either integer is 0
     * @throws ArithmeticException if the computation results in an integer overflow
     */
    @Contract(pure = true)
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(Math.multiplyExact(a, b)) / gcd(a, b);
    }

    /**
     * Calculates the power of a given base raised to a non-negative exponent.
     * The method uses iterative multiplication to compute the result.
     *
     * @param base the base value to be raised to the power of the exponent
     * @param exponent the non-negative exponent value
     * @return the result of raising the base to the power of the exponent
     * @throws ArithmeticException if the exponent is negative or if an overflow occurs during the computation
     */
    @Contract(pure = true)
    public static int pow(int base, int exponent) {
        Conditionals.ifTrueThrow(exponent < 0, () -> new ArithmeticException("Negative exponent"));

        int result = 1;
        for (int i = 0; i < exponent; i++) {
            result = Math.multiplyExact(result, base);
        }
        return result;
    }

    /**
     * Computes the result of raising the specified base to the power of the specified exponent.
     *
     * @param base the base number to be raised to a power.
     * @param exponent the non-negative integer exponent to which the base is to be raised.
     * @return the result of base raised to the power of the exponent.
     * @throws ArithmeticException if the exponent is negative or the result overflows.
     */
    public static int pow(int base, long exponent) {
        Conditionals.ifTrueThrow(exponent < 0L, () -> new ArithmeticException("Negative exponent"));

        int result = 1;
        for (long i = 0L; i < exponent; i++) {
            result = Math.multiplyExact(result, base);
        }
        return result;
    }

    /**
     * Calculates the value of a base raised to the power of an exponent.
     * This method ensures that the exponent is non-negative and checks
     * for potential overflow or invalid results in the computation.
     *
     * @param base the base number to be raised to the specified power
     * @param exponent the exponent to which the base will be raised; must be non-negative
     * @return the calculated result of the base raised to the power of the exponent
     * @throws ArithmeticException if the exponent is negative
     * @throws ArithmeticException if the result of the exponentiation is infinite or invalid
     */
    public static double pow(double base, double exponent) {
        Conditionals.ifTrueThrow(exponent < 0, () -> new ArithmeticException("Negative exponent"));

        double result = Math.pow(base, exponent);

        Conditionals.ifTrueThrow(Double.isInfinite(result), () -> new ArithmeticException("Exponentiation overflow"));
        Conditionals.ifTrueThrow(Double.isNaN(result), () -> new ArithmeticException("Invalid exponentiation result"));
        return result;
    }

    /**
     * Computes the integer square root of a non-negative integer value.
     * The result is the largest integer less than or equal to the true square root.
     *
     * @param value the integer value for which the square root is to be computed. Must be non-negative.
     * @return the integer square root of the given value.
     * @throws ArithmeticException if the input value is negative.
     */
    @Contract(pure = true)
    public static int sqrtInt(int value) {
        Conditionals.ifTrueThrow(value < 0, () -> new ArithmeticException("Negative value"));
        return (int) Math.floor(Math.sqrt(value));
    }

    /**
     * Calculates the square root of a given non-negative value.
     *
     * @param value the value for which the square root is to be calculated.
     *              Must be a non-negative number.
     * @return the square root of the given value.
     * @throws ArithmeticException if the given value is negative.
     */
    @Contract(pure = true)
    public static double sqrt(double value) {
        Conditionals.ifTrueThrow(value < 0, () -> new ArithmeticException("Negative value"));
        return Math.sqrt(value);
    }
}
