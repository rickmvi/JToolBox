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
import com.github.rickmvi.jtoolbox.collections.array.Array;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.control.If;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.IntStream;
import java.util.function.BinaryOperator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MathUtils is a utility class providing a wide range of mathematical operations
 * and helpers for numeric computations. It includes methods for generating random
 * numbers, performing arithmetic operations (addition, subtraction, multiplication,
 * division), computing averages, sums of even and odd numbers, modulo, GCD, LCM,
 * exponentiation, and square roots.
 * <p>
 * This class supports primitive types and provides generic {@link NumberOperator}
 * instances for functional-style numeric operations. Overflow, underflow, and invalid
 * operations are checked for floats and doubles, and exceptions are thrown for invalid
 * arguments such as negative exponents or empty arrays.
 * <p>
 * The class is designed as a {@link lombok.experimental.UtilityClass}, meaning all
 * methods are static and it cannot be instantiated.
 *
 * <h2>Random Numbers</h2>
 * <ul>
 *     <li>{@link #random()} - returns a random double between 0.0 and 1.0.</li>
 *     <li>{@link #random(double, double)} - returns a random double in a specified range.</li>
 *     <li>{@link #random(double, double, double, double, double)} - returns a random double in a range
 *     with an additional offset and offset range.</li>
 * </ul>
 *
 * <h2>Arithmetic Operators</h2>
 * <p>Provides pre-defined {@link NumberOperator} instances for addition, subtraction,
 * multiplication, and division for all primitive numeric types: byte, short, int, long, float, double.</p>
 *
 * <h2>Averages</h2>
 * <p>Methods to calculate the average of arrays of numeric types: int, long, float, double.
 * Throws an {@link ArithmeticException} if the array is empty.</p>
 *
 * <h2>Even and Odd Sums</h2>
 * <p>Methods to calculate the sum of all even or odd numbers up to a given limit,
 * for all numeric types. Overflow and invalid results are checked for floating-point numbers.</p>
 *
 * <h2>Other Utilities</h2>
 * <ul>
 *     <li>{@link #moduloInt(int, int)} / {@link #moduloLong(long, long)} - safe modulo operations.</li>
 *     <li>{@link #gcd(int, int)} - calculates the greatest common divisor of two integers.</li>
 *     <li>{@link #lcm(int, int)} - calculates the least common multiple of two integers.</li>
 *     <li>{@link #pow(int, int)}, {@link #pow(int, long)}, {@link #pow(double, double)} - exponentiation with overflow/invalid checks.</li>
 *     <li>{@link #sqrtInt(int)}, {@link #sqrt(double)} - square root calculations with negative value checks.</li>
 * </ul>
 *
 * <h2>Internal Classes</h2>
 * <ul>
 *     <li>{@link NumberFactory} - internal factory for creating {@link NumberOperator} instances and checking for overflow in floating-point operations.</li>
 *     <li>{@link NumberOperator} - functional interface for arithmetic operations with support for multiple operands.</li>
 * </ul>
 *
 * <p>All numeric operations are validated using helper methods to ensure safety against invalid
 * operations such as division by zero, negative exponents, NaN, or infinite results.</p>
 *
 * @author Rick
 * @version 1.0
 * @since 2025
 */
@Deprecated
@UtilityClass
@SuppressWarnings("unused")
public class MathUtils {

    public static double random() {
        return Math.random();
    }

    public static double random(double min, double max) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return rand.nextDouble(min, max);
    }

    public static double random(double min, double max, double offset, double minOffset, double maxOffset) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return rand.nextDouble(min, max) + offset * rand.nextDouble(minOffset, maxOffset);
    }

    public static final NumberOperator<Byte> SumByte =
            NumberFactory.sum((a, b) -> (byte) Math.addExact(a, b), (byte) 0);

    public static final NumberOperator<Short> SumShort =
            NumberFactory.sum((a, b) -> (short) Math.addExact(a, b), (short) 0);

    public static final NumberOperator<Integer> SumInt =
            NumberFactory.sum(Math::addExact, 0);

    public static final NumberOperator<Long> SumLong =
            NumberFactory.sum(Math::addExact, 0L);

    public static final NumberOperator<Float> SumFloat =
            NumberFactory.sum((a, b) -> {
                float result = a + b;
                NumberFactory.checkFloat(result, "sum");
                return result;
            }, 0.0f);

    public static final NumberOperator<Double> SumDouble =
            NumberFactory.sum((a, b) -> {
                double result = a + b;
                NumberFactory.checkFloat(result, "sum");
                return result;
            }, 0.0);

    public static final NumberOperator<Byte> SubtractByte =
            NumberFactory.subtract((a, b) -> (byte) Math.subtractExact(a, b));

    public static final NumberOperator<Short> SubtractShort =
            NumberFactory.subtract((a, b) -> (short) Math.subtractExact(a, b));

    public static final NumberOperator<Integer> SubtractInt =
            NumberFactory.subtract(Math::subtractExact);

    public static final NumberOperator<Long> SubtractLong =
            NumberFactory.subtract(Math::subtractExact);

    public static final NumberOperator<Float> SubtractFloat =
            NumberFactory.subtract((a, b) -> {
                float result = a - b;
                NumberFactory.checkFloat(result, "subtract");
                return result;
            });

    public static final NumberOperator<Double> SubtractDouble =
            NumberFactory.subtract((a, b) -> {
                double result = a - b;
                NumberFactory.checkFloat(result, "subtract");
                return result;
            });

    public static final NumberOperator<Byte> MultiplyByte =
            NumberFactory.sum((a, b) -> (byte) Math.multiplyExact(a, b), (byte) 1);

    public static final NumberOperator<Short> MultiplyShort =
            NumberFactory.sum((a, b) -> (short) Math.multiplyExact(a, b), (short) 1);

    public static final NumberOperator<Integer> MultiplyInt =
            NumberFactory.sum(Math::multiplyExact, 1);

    public static final NumberOperator<Long> MultiplyLong =
            NumberFactory.sum(Math::multiplyExact, 1L);

    public static final NumberOperator<Float> MultiplyFloat =
            NumberFactory.sum((a, b) -> {
                float result = a * b;
                NumberFactory.checkFloat(result, "multiply");
                return result;
            }, 1.0f);

    public static final NumberOperator<Double> MultiplyDouble =
            NumberFactory.sum((a, b) -> {
                double result = a * b;
                NumberFactory.checkFloat(result, "multiply");
                return result;
            }, 1.0);

    public static final BinaryOperator<Byte> DivideByte =
            NumberFactory.divide((a, b) -> (byte) (a / b));

    public static final BinaryOperator<Short> DivideShort =
            NumberFactory.divide((a, b) -> (short) (a / b));

    public static final BinaryOperator<Integer> DivideInt =
            NumberFactory.divide((a, b) -> a / b);

    public static final BinaryOperator<Long> DivideLong =
            NumberFactory.divide((a, b) -> a / b);

    public static final BinaryOperator<Float> DivideFloat =
            NumberFactory.divide((a, b) -> {
                float result = a / b;
                NumberFactory.checkFloat(result, "divide");
                return result;
            });

    public static final BinaryOperator<Double> DivideDouble =
            NumberFactory.divide((a, b) -> {
                double result = a / b;
                NumberFactory.checkFloat(result, "divide");
                return result;
            });

    public static double averageLong(long @NotNull ... numbers) {
        If.Throws(ArrayUtils.isEmpty(numbers), () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY));
        return Arrays.stream(numbers).average().orElse(0);
    }

    public static float averageFloat(float @NotNull ... numbers) {
        If.Throws(ArrayUtils.isEmpty(numbers), () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY));
        float sum = 0;
        for (float number : numbers) sum += number;
        return sum / numbers.length;
    }

    public static double averageInt(int @NotNull ... numbers) {
        If.Throws(ArrayUtils.isEmpty(numbers), () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY));
        return Arrays.stream(numbers).average().orElse(0);
    }

    public static double averageDouble(double @NotNull ... numbers) {
        If.Throws(ArrayUtils.isEmpty(numbers), () -> new ArithmeticException(Constants.AVERAGE_EMPTY_ARRAY));
        double sum = 0;
        for (double number : numbers) sum += number;
        return sum / numbers.length;
    }

    public static byte sumEvenByte(byte number) {
        byte valid = Numbers.requiredNonNegative(number);
        byte result = 0;
        for (int i = 0; i <= valid; i += 2) {
            result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    public static byte sumOddByte(byte number) {
        byte valid = Numbers.requiredPositive(number);
        byte result = 0;
        for (int i = 1; i <= valid; i += 2) {
            result = (byte) Math.addExact(result, i);
        }
        return result;
    }

    public static short sumEvenShort(short number) {
        short valid = Numbers.requiredNonNegative(number);
        short result = 0;
        for (int i = 0; i <= valid; i += 2) {
            result = (short) Math.addExact(result, i);
        }
        return result;
    }

    public static short sumOddShort(short number) {
        short valid = Numbers.requiredPositive(number);
        short result = 0;
        for (int i = 1; i <= valid; i += 2) {
            result = (short) Math.addExact(result, i);
        }
        return result;
    }

    public static int sumEvenInt(int number) {
        int valid = Numbers.requiredNonNegative(number);
        return IntStream.rangeClosed(0, valid).filter(Numbers::isEven).sum();
    }

    public static int sumOddInt(int number) {
        int valid = Numbers.requiredPositive(number);
        return IntStream.rangeClosed(1, valid).filter(Numbers::isOdd).sum();
    }

    public static long sumEvenLong(long number) {
        long valid = Numbers.requiredNonNegative(number);
        long result = 0L;
        for (long i = 0; i <= valid; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    public static long sumOddLong(long number) {
        long valid = Numbers.requiredPositive(number);
        long result = 0L;
        for (long i = 1; i <= valid; i += 2) {
            result = Math.addExact(result, i);
        }
        return result;
    }

    public static float sumEvenFloat(int number) {
        int valid = Numbers.nonNegativeNonZero(number);
        float result = 0f;
        for (int i = 0; i <= valid; i += 2) {
            result += i;
            assertResultIntegrity(result);
        }
        return result;
    }

    public static float sumOddFloat(int number) {
        int valid = Numbers.requiredPositive(number);
        float result = 0f;
        for (int i = 1; i <= valid; i += 2) {
            result += i;
            assertResultIntegrity(result);
        }
        return result;
    }

    private static void assertResultIntegrity(float result) {
        If.Throws(Float.isInfinite(result), () ->
                new ArithmeticException(Constants.SUM_OVERFLOW)
        );
        If.Throws(Float.isNaN(result), () ->
                new ArithmeticException(
                        StringFormatter.format(Constants.INVALID_OPERATION, "float"))
        );
    }

    public static double sumEvenDouble(long number) {
        long valid = Numbers.requiredNonNegative(number);
        double result = 0;
        for (long i = 0; i <= valid; i += 2) {
            result += i;
            assertResultIntegrity(result);
        }
        return result;
    }

    public static double sumOddDouble(long number) {
        long valid = Numbers.requiredPositive(number);
        double result = 0;
        for (long i = 1; i <= valid; i += 2) {
            result += i;
            assertResultIntegrity(result);
        }
        return result;
    }

    private static void assertResultIntegrity(double result) {
        If.Throws(Double.isInfinite(result), () ->
                new ArithmeticException(Constants.SUM_OVERFLOW)
        );
        If.Throws(Double.isNaN(result), () ->
                new ArithmeticException(
                        StringFormatter.format(Constants.INVALID_OPERATION, "double"))
        );
    }

    public static int moduloInt(int dividend, int divisor) {
        return (int) moduloLong(dividend, divisor);
    }

    public static long moduloLong(long dividend, long divisor) {
        If.Throws(Numbers.isZero(divisor), () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
        return dividend % divisor;
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

    private static int powerBySquaring(int base, long exponent) {
        int result = 1;
        result = computePower(exponent, result, base);
        return result;
    }

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

    @UtilityClass
    @ApiStatus.Internal
    class NumberFactory {

        @Contract(pure = true)
        public static <T extends Number> @NotNull NumberOperator<T> sum(BinaryOperator<T> adder, T zero) {
            return new NumberOperator<>() {
                @Override
                public T apply(T a, T b) {
                    return adder.apply(a, b);
                }

                @SafeVarargs
                @Override
                public final T apply(T... numbers) {
                    if (Array.isEmpty(numbers)) return zero;
                    T result = zero;
                    for (T n : numbers) result = adder.apply(result, n);
                    return result;
                }
            };
        }

        public static <T extends Number> @NotNull NumberOperator<T> subtract(BinaryOperator<T> subtractor) {
            return new NumberOperator<>() {
                @Override
                public T apply(T a, T b) {
                    return subtractor.apply(a, b);
                }

                @SafeVarargs
                @Override
                public final T apply(T... numbers) {
                    if (Array.isEmpty(numbers)) return numbers[0];
                    T result = numbers[0];
                    for (int i = 1; i < numbers.length; i++)
                        result = subtractor.apply(result, numbers[i]);
                    return result;
                }
            };
        }

        @Contract(pure = true)
        public static <T extends Number> @NotNull BinaryOperator<T> divide(BinaryOperator<T> divider) {
            return (a, b) -> {
                If.Throws(b.doubleValue() == 0.0D, () -> new ArithmeticException(Constants.DIVISION_BY_ZERO));
                return divider.apply(a, b);
            };
        }

        public static void checkFloat(double value, String operation) {
            If.Throws(Double.isInfinite(value),
                    () -> new ArithmeticException(StringFormatter.format(Constants.OVERFLOW, operation)));
            If.Throws(Double.isNaN(value),
                    () -> new ArithmeticException(StringFormatter.format(Constants.INVALID_OPERATION, "double")));
        }
    }

    @FunctionalInterface
    public interface NumberOperator<T extends Number> {

        T apply(T a, T b);

        default T apply(T... numbers) {
            if (Array.isEmpty(numbers))
                throw new IllegalArgumentException("Array cannot be empty.");
            T result = numbers[0];
            for (int i = 1; i < numbers.length; i++)
                result = apply(result, numbers[i]);
            return result;
        }
    }
}
