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

import com.github.rickmvi.jtoolbox.control.For;
import com.github.rickmvi.jtoolbox.control.If;

import com.github.rickmvi.jtoolbox.control.While;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with arrays of primitive types in a safe and efficient way.
 * <p>
 * This class provides methods to perform common array operations, including:
 * <ul>
 *     <li>Getting the length of arrays safely, returning 0 for null or empty arrays</li>
 *     <li>Concatenating multiple arrays into one, preserving order</li>
 *     <li>Finding the first and last index of elements within arrays</li>
 *     <li>Copying a range of elements from an array into a new array</li>
 * </ul>
 * </p>
 *
 * <p>
 * All methods are static and designed to handle primitive types:
 * {@code byte}, {@code short}, {@code int}, {@code long}, {@code float},
 * {@code double}, {@code char}, and {@code boolean}.
 * </p>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Concatenate two int arrays
 * int[] combined = ArrayUtils.concat(new int[]{1,2}, new int[]{3,4});
 *
 * // Find the first index of an element
 * int index = ArrayUtils.indexOf(3, combined);
 *
 * // Find the last index of an element
 * int lastIndex = ArrayUtils.lastIndexOf(2, combined);
 *
 * // Copy a range of elements
 * int[] subArray = ArrayUtils.copyRange(combined, 1, 3);
 *
 * // Get the length of an array safely
 * int len = ArrayUtils.length(combined);
 * }</pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>Methods throw {@link NullPointerException} if required arguments are null.</li>
 *     <li>Methods like {@code concat} and {@code copyRange} may throw
 *         {@link ArrayIndexOutOfBoundsException} if indices are invalid.</li>
 *     <li>Designed for performance with primitive arrays and minimal overhead.</li>
 *     <li>Relies on {@link For} and {@link Numbers} for safe element comparisons and index operations.</li>
 * </ul>
 *
 * @apiNote This class is abstract and cannot be instantiated. Use the static methods directly.
 * @see For
 * @see Numbers
 * @since 1.2
 */
@SuppressWarnings("unused")
public abstract class ArrayUtils {

    @Contract(pure = true)
    public static int length(byte @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(short @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(int @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(long @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(float @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(double @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(char @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int length(boolean @NotNull ... array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int lastIndex(byte @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(short @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(int @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(long @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(float @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(double @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(char @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    @Contract(pure = true)
    public static int lastIndex(boolean @NotNull ... array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    public static byte @NotNull [] concat(byte[] first, byte @NotNull ... second) {
        byte[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static short @NotNull [] concat(short[] first, short @NotNull ... second) {
        short[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static int @NotNull [] concat(int[] first, int @NotNull ... second) {
        int[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static long @NotNull [] concat(long[] first, long @NotNull ... second) {
        long[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static float @NotNull [] concat(float[] first, float @NotNull ... second) {
        float[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static double @NotNull [] concat(double[] first, double @NotNull ... second) {
        double[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static char @NotNull [] concat(char[] first, char @NotNull ... second) {
        char[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static boolean @NotNull [] concat(boolean[] first, boolean @NotNull ... second) {
        boolean[] result = Arrays.copyOf(first, length(first) + length(second));
        System.arraycopy(second, 0, result, length(first), length(second));
        return result;
    }

    public static int @NotNull [] concat(int @NotNull [] first, int @NotNull []... rest) {
        int totalLength = length(first);
        for (int[] array : rest) {
            totalLength = MathUtils.sumInt(totalLength, array.length);
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = length(first);
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset = MathUtils.sumInt(offset, array.length);
        }
        return result;
    }

    public static int indexOf(byte element, byte @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(short element, short @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(int element, int @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(long element, long @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(float element, float @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(double element, double @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(char element, char @NotNull ... array) {
        return For.range(0, array.length - 1)
                .filter(i -> Numbers.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static int indexOf(boolean element, boolean @NotNull ... array) {
        return For.range(0, array.length - 1)
                .filter(i -> array[i] == element)
                .findFirst()
                .orElse(-1);
    }

    public static int lastIndexOf(byte element, byte @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static int lastIndexOf(short element, short @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static int lastIndexOf(int element, int @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static int lastIndexOf(long element, long @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static int lastIndexOf(float element, float @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static int lastIndexOf(double element, double @NotNull ... array) {
        return For.range(0, length(array)-1)
                .filter(i -> Numbers.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    public static byte @NotNull [] copyRange(byte @NotNull [] array, int from, int to) {
        int length = to - from;
        byte[] result = new byte[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static short @NotNull [] copyRange(short @NotNull [] array, int from, int to) {
        int length = to - from;
        short[] result = new short[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static int @NotNull [] copyRange(int @NotNull [] array, int from, int to) {
        int length = to - from;
        int[] result = new int[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static long @NotNull [] copyRange(long @NotNull [] array, int from, int to) {
        int length = to - from;
        long[] result = new long[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static float @NotNull [] copyRange(float @NotNull [] array, int from, int to) {
        int length = to - from;
        float[] result = new float[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static double @NotNull [] copyRange(double @NotNull [] array, int from, int to) {
        int length = to - from;
        double[] result = new double[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static char @NotNull [] copyRange(char @NotNull [] array, int from, int to) {
        int length = to - from;
        char[] result = new char[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static boolean @NotNull [] copyRange(boolean @NotNull [] array, int from, int to) {
        int length = to - from;
        boolean[] result = new boolean[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static boolean contains(byte @NotNull [] array, byte element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(short @NotNull [] array, short element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(int @NotNull [] array, int element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(long @NotNull [] array, long element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(float @NotNull [] array, float element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(double @NotNull [] array, double element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    public static boolean contains(char @NotNull [] array, char element) {
        return For.range(0, lastIndex(array)).stream().anyMatch(i -> Numbers.equals(array[i], element));
    }

    @Contract(pure = true)
    public static boolean contains(boolean @NotNull [] array, boolean element) {
        for (boolean b : array) {
            if (b == element) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int @NotNull [] array, int @NotNull [] elements) {
        for (int element : elements) {
            if (!contains(array, element)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean isEmpty(byte @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(short @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(int @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(long @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(float @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(double @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(char @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static boolean isEmpty(boolean @NotNull [] array) {
        return Numbers.isZero(array.length);
    }

    @Contract(pure = true)
    public static byte @NotNull [] remove(byte @NotNull [] array, byte element) {
        byte[] result = new byte[array.length - 1];
        int index = 0;
        for (byte i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static short @NotNull [] remove(short @NotNull [] array, short element) {
        short[] result = new short[array.length - 1];
        int index = 0;
        for (short i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static int @NotNull [] remove(int @NotNull [] array, int element) {
        int count = (int) Arrays.stream(array).filter(i -> Numbers.notEquals(i, element)).count();
        int[] result = new int[count];
        int index = 0;
        for (int i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static long @NotNull [] remove(long @NotNull [] array, long element) {
        int count = (int) Arrays.stream(array).filter(i -> Numbers.notEquals(i, element)).count();
        long[] result = new long[count];
        int index = 0;
        for (long i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static float @NotNull [] remove(float @NotNull [] array, float element) {
        float[] result = new float[array.length - 1];
        int index = 0;
        for (float i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static double @NotNull [] remove(double @NotNull [] array, double element) {
        int count = (int) Arrays.stream(array).filter(i -> Numbers.notEquals(i, element)).count();
        double[] result = new double[count];
        int index = 0;
        for (double i : array) {
            if (Numbers.notEquals(i, element)) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static char @NotNull [] remove(char @NotNull [] array, char element) {
        char[] result = new char[array.length - 1];
        int index = 0;
        for (char i : array) {
            if (i != element) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static boolean @NotNull [] remove(boolean @NotNull [] array, boolean element) {
        boolean[] result = new boolean[array.length - 1];
        int index = 0;
        for (boolean i : array) {
            if (i != element) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static int @NotNull [] remove(int @NotNull [] array, int @NotNull [] elements) {
        if (isEmpty(elements) || elements.length > array.length) return array;

        int[] result = new int[length(array) - length(elements)];
        int index = 0;
        for (int i : array) {
            if (!contains(elements, i)) {
                result[index++] = i;
            }
        }
        return result;
    }

    public static byte @NotNull [] reversedCopy(byte @NotNull [] array) {
        byte[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(byte @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            byte tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static short @NotNull [] reversedCopy(short @NotNull [] array) {
        short[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(short @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            short tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static int @NotNull [] reversedCopy(int @NotNull [] array) {
        int[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(int @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            int tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static long @NotNull [] reversedCopy(long @NotNull [] array) {
        long[] result = array.clone();
        reversedInPlace(array);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(long @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            long tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static float @NotNull [] reversedCopy(float @NotNull [] array) {
        float[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(float @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            float tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static double @NotNull [] reversedCopy(double @NotNull [] array) {
        double[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(double @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            double tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    @Contract(pure = true)
    public static char @NotNull [] reversedCopy(char @NotNull [] array) {
        char[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(char @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            char tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static boolean @NotNull [] reversedCopy(boolean @NotNull [] array) {
        boolean[] result = array.clone();
        reversedInPlace(result);
        return result;
    }

    @ApiStatus.Internal
    private static void reversedInPlace(boolean @NotNull [] array) {
        int[] start = {0};
        int[] end = {array.length - 1};

        While.runTrue(() -> start[0] < end[0], () -> {
            boolean tmp = array[start[0]];
            array[start[0]] = array[end[0]];
            array[end[0]] = tmp;
            start[0]++;
            end[0]--;
        });
    }

    public static byte @NotNull [] filter(byte @NotNull [] array, byte @NotNull [] filter) {
        byte[] result = new byte[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());

        return result;
    }

    public static short @NotNull [] filter(short @NotNull [] array, short @NotNull [] filter) {
        short[] result = new short[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static int @NotNull [] filter(int @NotNull [] array, int @NotNull [] filter) {
        int[] result = new int[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static long @NotNull [] filter(long @NotNull [] array, long @NotNull [] filter) {
        long[] result = new long[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static float @NotNull [] filter(float @NotNull [] array, float @NotNull [] filter) {
        float[] result = new float[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static double @NotNull [] filter(double @NotNull [] array, double @NotNull [] filter) {
        double[] result = new double[array.length];
        For.range(0, length(array) - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static char @NotNull [] filter(char @NotNull [] array, char @NotNull [] filter) {
        char[] result = new char[array.length];
        For.range(0, array.length - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static boolean @NotNull [] filter(boolean @NotNull [] array, boolean @NotNull [] filter) {
        boolean[] result = new boolean[array.length];
        For.range(0, array.length - 1).forEach(i ->
                If.isTrue(contains(filter, array[i]), () -> result[i] = array[i]).run());
        return result;
    }

    public static int @NotNull [] convert(@NotNull String number) {
        if (number.trim().isEmpty()) return new int[0];

        List<Integer> result = new ArrayList<>();

        for (char c : number.toCharArray()) {
            int digit = Character.getNumericValue(c);
            If.isTrue(digit >= 0 && digit <= 9, () -> result.add(digit)).run();
        }

        if (result.isEmpty()) return new int[0];

        int[] arr = new int[result.size()];
        For.range(0, result.size() - 1).forEach(i -> arr[i] = result.get(i));
        return arr;
    }
}