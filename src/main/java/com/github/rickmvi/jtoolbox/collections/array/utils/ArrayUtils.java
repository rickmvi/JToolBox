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
package com.github.rickmvi.jtoolbox.collections.array.utils;

import com.github.rickmvi.jtoolbox.control.Iteration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ArrayUtils {

    // ========== CONCAT ==========
    public static byte @NotNull [] concat(byte[] first, byte @NotNull [] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static short @NotNull [] concat(short[] first, short @NotNull [] second) {
        short[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int @NotNull [] concat(int[] first, int @NotNull [] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static long @NotNull [] concat(long[] first, long @NotNull [] second) {
        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static float @NotNull [] concat(float[] first, float @NotNull [] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static double @NotNull [] concat(double[] first, double @NotNull [] second) {
        double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static char @NotNull [] concat(char[] first, char @NotNull [] second) {
        char[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static boolean @NotNull [] concat(boolean[] first, boolean @NotNull [] second) {
        boolean[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int @NotNull [] concat(int @NotNull [] first, int @NotNull []... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength = Iteration.sumInt(totalLength, array.length);
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset = Iteration.sumInt(offset, array.length);
        }
        return result;
    }

    // ========== INDEX OF ==========
    public static int indexOf(byte @NotNull [] array, byte element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(short @NotNull [] array, short element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(int @NotNull [] array, int element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(long @NotNull [] array, long element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(float @NotNull [] array, float element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(double @NotNull [] array, double element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(char @NotNull [] array, char element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    public static int indexOf(boolean @NotNull [] array, boolean element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    // ========== LAST INDEX OF ==========
    public static int lastIndexOf(byte @NotNull [] array, byte element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(short @NotNull [] array, short element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(int @NotNull [] array, int element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(long @NotNull [] array, long element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(float @NotNull [] array, float element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(double @NotNull [] array, double element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(char @NotNull [] array, char element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    public static int lastIndexOf(boolean @NotNull [] array, boolean element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    // ========== COPY RANGE ==========
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

    // ========== CONTAINS ==========
    public static boolean contains(byte @NotNull [] array, byte element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(short @NotNull [] array, short element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(int @NotNull [] array, int element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(long @NotNull [] array, long element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(float @NotNull [] array, float element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(double @NotNull [] array, double element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    public static boolean contains(char @NotNull [] array, char element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
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
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(short @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(int @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(long @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(float @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(double @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(char @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static boolean isEmpty(boolean @NotNull [] array) {
        return array.length == 0;
    }

    @Contract(pure = true)
    public static byte @NotNull [] remove(byte @NotNull [] array, byte element) {
        byte[] result = new byte[array.length - 1];
        int index = 0;
        for (byte i : array) {
            if (i != element) {
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
            if (i != element) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static int @NotNull [] remove(int @NotNull [] array, int element) {
        int count = (int) Arrays.stream(array).filter(i -> i != element).count();
        int[] result = new int[count];
        int index = 0;
        for (int i : array) {
            if (i != element) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static long @NotNull [] remove(long @NotNull [] array, long element) {
        int count = (int) Arrays.stream(array).filter(i -> i != element).count();
        long[] result = new long[count];
        int index = 0;
        for (long i : array) {
            if (i != element) {
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
            if (i != element) {
                result[index++] = i;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static double @NotNull [] remove(double @NotNull [] array, double element) {
        int count = (int) Arrays.stream(array).filter(i -> i != element).count();
        double[] result = new double[count];
        int index = 0;
        for (double i : array) {
            if (i != element) {
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
        int[] result = new int[array.length - elements.length];
        int index = 0;
        for (int i : array) {
            if (!contains(elements, i)) {
                result[index++] = i;
            }
        }
        return result;
    }

    public static byte @NotNull [] reversed(byte @NotNull [] array) {
        byte[] result = new byte[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = (byte) (array.length - i - 1));
        return result;
    }

    public static short @NotNull [] reversed(short @NotNull [] array) {
        short[] result = new short[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = (short) (array.length - i - 1));
        return result;
    }

    public static int @NotNull [] reversed(int @NotNull [] array) {
        int[] result = new int[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    public static long @NotNull [] reversed(long @NotNull [] array) {
        long[] result = new long[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    public static float @NotNull [] reversed(float @NotNull [] array) {
        float[] result = new float[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    public static double @NotNull [] reversed(double @NotNull [] array) {
        double[] result = new double[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    @Contract(pure = true)
    public static char @NotNull [] reversed(char @NotNull [] array) {
        char[] result = new char[array.length];
        Iteration.forEachReversedWithIndex(
                array.length,
                (index, reverseIndex) -> result[index] = array[reverseIndex]);
        return result;
    }

    public static boolean @NotNull [] reversed(boolean @NotNull [] array) {
        boolean[] result = new boolean[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array[array.length - i - 1]);
        return result;
    }

    public static byte @NotNull [] filter(byte @NotNull [] array, byte @NotNull [] filter) {
        byte[] result = new byte[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static short @NotNull [] filter(short @NotNull [] array, short @NotNull [] filter) {
        short[] result = new short[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static int @NotNull [] filter(int @NotNull [] array, int @NotNull [] filter) {
        int[] result = new int[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static long @NotNull [] filter(long @NotNull [] array, long @NotNull [] filter) {
        long[] result = new long[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static float @NotNull [] filter(float @NotNull [] array, float @NotNull [] filter) {
        float[] result = new float[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static double @NotNull [] filter(double @NotNull [] array, double @NotNull [] filter) {
        double[] result = new double[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static char @NotNull [] filter(char @NotNull [] array, char @NotNull [] filter) {
        char[] result = new char[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    public static boolean @NotNull [] filter(boolean @NotNull [] array, boolean @NotNull [] filter) {
        boolean[] result = new boolean[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }
}