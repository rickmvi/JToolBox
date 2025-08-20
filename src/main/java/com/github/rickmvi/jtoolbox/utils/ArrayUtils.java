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

import com.github.rickmvi.jtoolbox.collections.array.Array;
import com.github.rickmvi.jtoolbox.control.Conditionals;
import com.github.rickmvi.jtoolbox.control.Iteration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ArrayUtils {

    /**
     * Combines two byte arrays into a single array, preserving the order of elements
     * from both arrays. The first array elements appear first in the resulting array,
     * followed by the elements from the second array.
     *
     * @param first  the first byte array to be concatenated; must not be null
     * @param second the second byte array to be concatenated; must not be null
     * @return a new byte array containing all elements from the first array followed
     *         by all elements from the second array
     * @throws NullPointerException if either the {@code first} or {@code second} array is null
     */
    public static byte @NotNull [] concat(byte[] first, byte @NotNull [] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Combines two short arrays into a single array, preserving the order of elements
     * from both arrays. The elements from the first array appear first in the resulting
     * array, followed by the elements from the second array.
     *
     * @param first  the first short array to be concatenated; must not be null
     * @param second the second short array to be concatenated; must not be null
     * @return a new short array containing all elements from the first array followed
     *         by all elements from the second array
     * @throws NullPointerException if either the {@code first} or {@code second} array is null
     */
    public static short @NotNull [] concat(short[] first, short @NotNull [] second) {
        short[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Combines two integer arrays into a single array, preserving the order of elements
     * from both arrays. The elements from the first array appear first in the resulting
     * array, followed by the elements from the second array.
     *
     * @param first  the first integer array to be concatenated; must not be null
     * @param second the second integer array to be concatenated; must not be null
     * @return a new integer array containing all elements from the first array followed
     *         by all elements from the second array
     * @throws NullPointerException if either the {@code first} or {@code second} array is null
     */
    public static int @NotNull [] concat(int[] first, int @NotNull [] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Combines two {@code long} arrays into a single array, preserving the order of elements
     * from both arrays. The elements from the first array appear first in the resulting
     * array, followed by the elements from the second array.
     *
     * @param first the first {@code long} array to be concatenated; must not be {@code null}
     * @param second the second {@code long} array to be concatenated; must not be {@code null}
     * @return a new {@code long} array containing all elements from the {@code first} array
     *         followed by all elements from the {@code second} array
     * @throws NullPointerException if either {@code first} or {@code second} array is {@code null}
     */
    public static long @NotNull [] concat(long[] first, long @NotNull [] second) {
        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Combines two {@code float} arrays into a single array, preserving the order of elements
     * from both arrays. The elements from the first array appear first in the resulting
     * array, followed by the elements from the second array.
     *
     * @param first  the first {@code float} array to be concatenated; must not be {@code null}
     * @param second the second {@code float} array to be concatenated; must not be {@code null}
     * @return a new {@code float} array containing all elements from the {@code first} array
     *         followed by all elements from the {@code second} array
     * @throws NullPointerException if either the {@code first} or {@code second} array is {@code null}
     */
    public static float @NotNull [] concat(float[] first, float @NotNull [] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates two arrays of doubles into a single array.
     *
     * @param first the first array to be concatenated; must not be null
     * @param second the second array to be concatenated; must not be null
     * @return a new array containing all elements from the {@code first} array
     *         followed by all elements from the {@code second} array
     * @throws NullPointerException if either {@code first} or {@code second} is null
     */
    public static double @NotNull [] concat(double[] first, double @NotNull [] second) {
        double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates two character arrays into a single array.
     *
     * @param first the first character array to be concatenated; may be null
     * @param second the second character array to be concatenated; must not be null
     * @return a new character array containing all elements of the first array
     *         followed by all elements of the second array
     * @throws NullPointerException if the second array is null
     */
    public static char @NotNull [] concat(char[] first, char @NotNull [] second) {
        char[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates two arrays of boolean values into a single array.
     *
     * @param first the first array of boolean values; may be empty
     *              but not null.
     * @param second the second array of boolean values; must not
     *               be null.
     * @return a new array containing all the elements of the first
     *         array followed by all the elements of the second array.
     * @throws NullPointerException if either the first or second
     *                              array is null.
     */
    public static boolean @NotNull [] concat(boolean[] first, boolean @NotNull [] second) {
        boolean[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the given arrays into a single array. The first array is always
     * included, and any number of additional arrays can be provided.
     *
     * @param first The first array to be concatenated. Must not be null.
     * @param rest  Additional arrays to be concatenated. Each array within this
     *              varargs parameter must not be null.
     * @return A new array containing the elements of the first array followed by
     *         all the elements of the additional arrays.
     * @throws NullPointerException If the {@code first} array or any of the arrays
     *                              in {@code rest} is null.
     */
    public static int @NotNull [] concat(int @NotNull [] first, int @NotNull []... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength = MathUtils.sumInt(totalLength, array.length);
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset = MathUtils.sumInt(offset, array.length);
        }
        return result;
    }

    /**
     * Finds the index of the first occurrence of the specified byte element
     * in the given byte array. If the element is not present in the array,
     * a value indicating absence may be returned.
     *
     * @param array the array of bytes to search in; must not be null
     * @param element the byte element to search for
     * @return the index of the first occurrence of the specified byte
     *         element within the array, or a value indicating absence
     *         if the element is not found
     * @throws NullPointerException if the array is null
     */
    public static int indexOf(byte @NotNull [] array, byte element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the index of the first occurrence of a specified element in the provided array.
     *
     * @param array the array to search within; must not be null
     * @param element the element to find in the array
     * @return the index of the first occurrence of the specified element;
     *         -1 if the element is not found
     * @throws NullPointerException if the array is null
     */
    public static int indexOf(short @NotNull [] array, short element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the index of the first occurrence of the specified element in the
     * given array. The method will search through the array to find the first
     * element that matches the specified value.
     *
     * @param array the array to be searched; must not be null
     * @param element the element to find within the array
     * @return the index of the first occurrence of the specified element in the
     *         array, or -1 if the element is not found
     * @throws NullPointerException if the {@code array} is null
     */
    public static int indexOf(int @NotNull [] array, int element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given array.
     *
     * @param array the array to search. Must not be null.
     * @param element the element to search for in the array.
     * @return the index of the first occurrence of the specified element,
     *         or -1 if the element is not found in the array.
     * @throws NullPointerException if the specified array is null.
     */
    public static int indexOf(long @NotNull [] array, long element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the index of the first occurrence of the specified element in the given array.
     * If the element is not found, -1 is returned.
     *
     * @param array the array of float elements to search; must not be null
     * @param element the float element to find within the array
     * @return the index of the first occurrence of the specified element,
     *         or -1 if the element is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int indexOf(float @NotNull [] array, float element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the index of the first occurrence of the specified element in the given array.
     *
     * @param array the array of doubles to search; must not be null
     * @param element the element to find in the array
     * @return the index of the first occurrence of the specified element in the array,
     *         or -1 if the element is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int indexOf(double @NotNull [] array, double element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the index of the first occurrence of a specified character in the given array.
     *
     * @param array the array of characters in which to search for the specified element.
     *              Must not be null.
     * @param element the character to search for within the array.
     * @return the index of the first occurrence of the specified character in the array,
     *         or -1 if the character is not found.
     * @throws NullPointerException if the provided array is null.
     */
    public static int indexOf(char @NotNull [] array, char element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Searches for the first occurrence of a specified boolean value in the given array
     * and returns its index. If the specified value is not found, returns -1.
     *
     * @param array the array of boolean values to be searched; must not be null
     * @param element the boolean value to search for in the array
     * @return the index of the first occurrence of the specified boolean value in the
     *         array, or -1 if the value is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int indexOf(boolean @NotNull [] array, boolean element) {
        return Iteration.findFirstIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the last index of the given element in the specified byte array.
     *
     * @param array the array in which to search for the element; must not be null
     * @param element the element to search for in the array
     * @return the last index of the specified element in the array, or -1 if the element is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int lastIndexOf(byte @NotNull [] array, byte element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in the provided array, or -1 if the array does not contain the element.
     *
     * @param array the array of short elements to be searched; must not be null
     * @param element the element to search for within the array
     * @return the index of the last occurrence of the specified element,
     *         or -1 if the element is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int lastIndexOf(short @NotNull [] array, short element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the last index of the specified element in the given array.
     *
     * @param array the array to search; must not be null
     * @param element the element to find in the array
     * @return the last index of the element in the array if found, or -1 if the element is not present
     * @throws NullPointerException if the provided array is null
     */
    public static int lastIndexOf(int @NotNull [] array, int element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the last index at which the specified element is found in the provided array.
     * If the element does not exist in the array, -1 is returned.
     *
     * @param array the array of long values to search through; must not be null
     * @param element the value to search for in the array
     * @return the last index of the specified element in the array, or -1 if the element is not found
     * @throws NullPointerException if the provided array is null
     */
    public static int lastIndexOf(long @NotNull [] array, long element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the last index in the given array where the specified element occurs.
     * This method searches from the end of the array towards the beginning.
     *
     * @param array the array to search, must not be null
     * @param element the floating-point value to find in the array
     * @return the last index of the specified element in the array, or -1 if the element is not found
     * @throws NullPointerException if the array is null
     */
    public static int lastIndexOf(float @NotNull [] array, float element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the last index in the given array where the specified element is located.
     *
     * @param array the array of doubles to search; must not be null.
     * @param element the double value to search for in the array.
     * @return the last index where the specified element can be found in the array,
     *         or -1 if the element is not present in the array.
     * @throws NullPointerException if the array is null.
     */
    public static int lastIndexOf(double @NotNull [] array, double element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Finds the last index of the specified character in the given array.
     * If the character is not found, returns -1.
     *
     * @param array the array of characters to search in; must not be null
     * @param element the character whose last index is to be determined
     * @return the last index where the specified character is found in the array, or -1 if not found
     * @throws NullPointerException if the provided array is null
     */
    public static int lastIndexOf(char @NotNull [] array, char element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Returns the index of the last occurrence of the specified element in the given boolean array,
     * or -1 if the array does not contain the element.
     *
     * @param array the boolean array to search, must not be null
     * @param element the boolean value to find in the array
     * @return the index of the last occurrence of the specified element in the array,
     *         or -1 if the element is not found
     * @throws NullPointerException if the array is null
     */
    public static int lastIndexOf(boolean @NotNull [] array, boolean element) {
        return Iteration.findLastIndexMatching(array.length, i -> array[i] == element);
    }

    /**
     * Copies a range of elements from the specified source array into a new array.
     * The range is defined by the specified {@code from} index, inclusive, to the
     * {@code to} index, exclusive. The original array remains unmodified.
     *
     * @param array the source array from which a range of bytes is to be copied
     * @param from the starting index (inclusive) of the range to be copied
     * @param to the ending index (exclusive) of the range to be copied
     * @return a new byte array containing the specified range of elements
     * @throws NullPointerException if the {@code array} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are
     *         outside the bounds of the array, or if {@code from} is greater
     *         than {@code to}
     */
    public static byte @NotNull [] copyRange(byte @NotNull [] array, int from, int to) {
        int length = to - from;
        byte[] result = new byte[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a specified range from the given array into a new array and returns it.
     *
     * @param array the source array from which elements are to be copied
     * @param from the starting index (inclusive) of the range to copy
     * @param to the ending index (exclusive) of the range to copy
     * @return a new array containing the elements from the specified range of the source array
     * @throws NullPointerException if the provided array is null
     * @throws IndexOutOfBoundsException if the starting or ending index is out of the bounds of the source array
     * @throws IllegalArgumentException if the starting index is greater than the ending index
     */
    public static short @NotNull [] copyRange(short @NotNull [] array, int from, int to) {
        int length = to - from;
        short[] result = new short[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Creates a copy of a subrange from the specified array.
     *
     * @param array the source array from which elements are to be copied.
     *              This array must not be null.
     * @param from the starting index (inclusive) of the range to copy.
     * @param to the ending index (exclusive) of the range to copy.
     * @return a new array containing the elements from the specified range of the source array.
     * @throws NullPointerException if the provided array is null.
     * @throws ArrayIndexOutOfBoundsException if the specified range is outside the bounds of the array.
     * @throws IllegalArgumentException if the from index is greater than the to index.
     */
    public static int @NotNull [] copyRange(int @NotNull [] array, int from, int to) {
        int length = to - from;
        int[] result = new int[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a range of elements from the provided array into a new array.
     *
     * @param array the source array from which elements will be copied
     * @param from the starting index (inclusive) of the range to be copied
     * @param to the ending index (exclusive) of the range to be copied
     * @return a new array containing the elements from the specified range
     * @throws NullPointerException if the provided array is null
     * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} is out of bounds
     *         or if {@code from > to}
     */
    public static long @NotNull [] copyRange(long @NotNull [] array, int from, int to) {
        int length = to - from;
        long[] result = new long[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a specified range of elements from the source array into a new array.
     *
     * @param array the source array from which elements are to be copied; must not be null
     * @param from the starting index (inclusive) of the range to copy
     * @param to the ending index (exclusive) of the range to copy
     * @return a new array containing the elements within the specified range
     * @throws NullPointerException if the source array is null
     * @throws ArrayIndexOutOfBoundsException if the {@code from} or {@code to} indices are out of bounds
     * @throws IllegalArgumentException if {@code from} is greater than {@code to}
     */
    public static float @NotNull [] copyRange(float @NotNull [] array, int from, int to) {
        int length = to - from;
        float[] result = new float[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a range of elements from the specified array into a new array.
     * The range is defined by the indices {@code from} (inclusive) and {@code to} (exclusive).
     *
     * @param array the source array from which elements will be copied; must not be null
     * @param from the starting index (inclusive) of the range to copy
     * @param to the ending index (exclusive) of the range to copy
     * @return a new array containing the elements from the specified range in the source array
     * @throws NullPointerException if the {@code array} is null
     * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are out of bounds of the array
     * @throws IllegalArgumentException if {@code from} is greater than {@code to}
     */
    public static double @NotNull [] copyRange(double @NotNull [] array, int from, int to) {
        int length = to - from;
        double[] result = new double[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a specified range of elements from the given character array
     * into a new array and returns it.
     *
     * @param array the source array from which elements are to be copied
     * @param from the starting index (inclusive) of the range to be copied
     * @param to the ending index (exclusive) of the range to be copied
     * @return a new array containing the elements from the specified range
     * @throws NullPointerException if the provided array is null
     * @throws ArrayIndexOutOfBoundsException if the from or to indices are
     *         out of bounds or if from is greater than to
     */
    public static char @NotNull [] copyRange(char @NotNull [] array, int from, int to) {
        int length = to - from;
        char[] result = new char[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Copies a specified range from the given boolean array into a new boolean array.
     *
     * @param array the source boolean array from which the range will be copied
     * @param from the starting index (inclusive) of the range to be copied
     * @param to the ending index (exclusive) of the range to be copied
     * @return a new boolean array containing the elements within the specified range
     * @throws NullPointerException if the input array is null
     * @throws ArrayIndexOutOfBoundsException if the from or to indices are out of bounds
     * @throws IllegalArgumentException if from is greater than to
     */
    public static boolean @NotNull [] copyRange(boolean @NotNull [] array, int from, int to) {
        int length = to - from;
        boolean[] result = new boolean[length];
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    /**
     * Checks if the specified byte array contains the provided element.
     *
     * @param array the byte array in which to search for the element; must not be null
     * @param element the byte value to search for in the array
     * @return {@code true} if the array contains the specified element, {@code false} otherwise
     * @throws NullPointerException if the input array is {@code null}
     */
    public static boolean contains(byte @NotNull [] array, byte element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks whether the specified array contains the given element.
     *
     * @param array the array to search, must not be null
     * @param element the element to search for in the array
     * @return true if the element is found in the array, false otherwise
     * @throws NullPointerException if the array is null
     */
    public static boolean contains(short @NotNull [] array, short element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if the specified array contains the given element.
     *
     * @param array the array to search, must not be null
     * @param element the element to look for within the array
     * @return true if the array contains the specified element, false otherwise
     * @throws NullPointerException if the array is null
     */
    public static boolean contains(int @NotNull [] array, int element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if a given element is present in the specified array.
     *
     * @param array the array to be searched; must not be null
     * @param element the element to search for in the array
     * @return {@code true} if the element is found in the array, {@code false} otherwise
     * @throws NullPointerException if the array is null
     */
    public static boolean contains(long @NotNull [] array, long element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if a specified float element is present within a given array.
     *
     * @param array the array to be searched for the specified element; must not be null
     * @param element the float element to search for in the provided array
     * @return {@code true} if the specified element is found in the array; {@code false} otherwise
     * @throws NullPointerException if the input array is null
     */
    public static boolean contains(float @NotNull [] array, float element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if the given array contains the specified element.
     *
     * @param array the array to search for the element; must not be null
     * @param element the element to search for within the array
     * @return true if the array contains the specified element, false otherwise
     * @throws NullPointerException if the input array is null
     */
    public static boolean contains(double @NotNull [] array, double element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if the specified character array contains the given element.
     *
     * @param array the array to search, must not be null
     * @param element the character to search for in the array
     * @return true if the array contains the specified element, false otherwise
     * @throws NullPointerException if the array is null
     */
    public static boolean contains(char @NotNull [] array, char element) {
        return Iteration.anyMatch(array.length, i -> array[i] == element);
    }

    /**
     * Checks if the specified boolean element is present in the given boolean array.
     *
     * @param array the boolean array to search, must not be null
     * @param element the boolean element to search for in the array
     * @return true if the element is found in the array, false otherwise
     * @throws NullPointerException if the array is null
     */
    @Contract(pure = true)
    public static boolean contains(boolean @NotNull [] array, boolean element) {
        for (boolean b : array) {
            if (b == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether all elements in the specified elements array
     * are present in the given array.
     *
     * @param array the array in which to check for the presence of elements
     * @param elements the array of elements to be verified for presence in the array
     * @return true if all elements in the elements array are present in the array,
     *         false otherwise
     * @throws NullPointerException if either the array or elements is null
     */
    public static boolean contains(int @NotNull [] array, int @NotNull [] elements) {
        for (int element : elements) {
            if (!contains(array, element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given byte array is empty.
     *
     * @param array the byte array to check for emptiness; must not be null.
     * @return {@code true} if the array is empty (contains no elements); {@code false} otherwise.
     * @throws NullPointerException if the provided array is null.
     */
    @Contract(pure = true)
    public static boolean isEmpty(byte @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks if the given short array is empty.
     *
     * @param array the short array to check; must not be null
     * @return {@code true} if the array is empty, {@code false} otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(short @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks if the provided integer array is empty.
     *
     * @param array the array to check; must not be null
     * @return true if the array is empty (has a length of 0), false otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(int @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks if the given array is empty.
     *
     * @param array the array to be checked for emptiness; must not be null
     * @return {@code true} if the array is empty, {@code false} otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(long @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks whether the provided float array is empty.
     *
     * @param array the float array to be checked; must not be null
     * @return {@code true} if the array length is zero, {@code false} otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(float @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Determines whether the provided array of doubles is empty.
     *
     * @param array the array of doubles to check; must not be null
     * @return true if the array is empty, false otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(double @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks if the provided character array is empty.
     *
     * @param array the character array to check for emptiness; must not be null
     * @return {@code true} if the array is empty (length is 0), {@code false} otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(char @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Checks if the given boolean array is empty.
     *
     * @param array the boolean array to be checked; must not be null
     * @return true if the array is empty; false otherwise
     * @throws NullPointerException if the provided array is null
     */
    @Contract(pure = true)
    public static boolean isEmpty(boolean @NotNull [] array) {
        return array.length == 0;
    }

    /**
     * Removes the first occurrence of the specified element from the given array and returns a new array without the element.
     * The original array is not modified.
     *
     * @param array the array from which the element is to be removed; must not be null
     * @param element the element to be removed from the array
     * @return a new array that excludes the first occurrence of the specified element; never null
     * @throws NullPointerException if the input array is null
     * @throws ArrayIndexOutOfBoundsException if the specified element is not found in the array
     */
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

    /**
     * Removes all occurrences of a specified element from the given array and returns
     * a new array containing the remaining elements. The original array is not modified.
     *
     * @param array The array from which the element will be removed. Must not be null.
     * @param element The element to be removed from the array.
     * @return A new array containing all the elements of the original array except the specified element.
     *         The returned array is never null.
     * @throws NullPointerException If the input array is null.
     * @throws ArrayIndexOutOfBoundsException If the removed element results in an invalid array state.
     */
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

    /**
     * Removes all occurrences of the specified element from the given array
     * and returns a new array containing the remaining elements.
     * The input array is not modified.
     *
     * @param array the input array from which the specified element should be removed; must not be null
     * @param element the element to be removed from the array
     * @return a new array containing all elements from the original array except the specified element; never null
     * @throws NullPointerException if the input array is null
     */
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

    /**
     * Removes all occurrences of the specified element from the given array.
     *
     * @param array the input array from which the specified element will be removed; must not be null.
     * @param element the element to remove from the array.
     * @return a new array that contains all elements of the input array except the specified element.
     *         The resulting array will be empty if the input array contains only occurrences of the element
     *         or if the input array is empty.
     * @throws NullPointerException if the input array is null.
     */
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

    /**
     * Removes all occurrences of the specified element from the given array and returns a new array
     * with the remaining elements. The input array is not modified.
     *
     * @param array the array from which the element needs to be removed; must not be {@code null}
     * @param element the element to be removed from the array
     * @return a new array containing all elements from the input array except the specified element;
     *         never {@code null}
     * @throws NullPointerException if the input array is {@code null}
     * @throws IllegalArgumentException if the input array has only one element and it matches the
     *                                  specified element
     */
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

    /**
     * Removes all occurrences of a specified element from the given array.
     *
     * @param array the array from which the specified element should be removed; must not be null
     * @param element the element to be removed from the array
     * @return a new array containing all elements of the original array except the specified element,
     *         maintaining the order of the remaining elements
     * @throws NullPointerException if the input array is null
     */
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

    /**
     * Removes all occurrences of the specified element from the given array.
     * The method creates a new array that excludes the specified element.
     *
     * @param array the original array from which the element should be removed; must not be null
     * @param element the character to be removed from the array
     * @return a new array containing all elements from the original array except the specified element; never null
     * @throws IllegalArgumentException if the input array is null or the result array cannot be created
     */
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

    /**
     * Removes the specified element from the given boolean array and returns
     * a new array with the element removed. If the element does not exist
     * in the array, the original array is returned.
     *
     * @param array the boolean array from which the specified element is to be removed
     * @param element the boolean value to be removed from the array
     * @return a new boolean array without the specified element; if the element
     *         is not present, the original array is returned
     * @throws NullPointerException if the input array is null
     * @throws ArrayIndexOutOfBoundsException if the array is empty or if
     *         removing the element would result in an empty array
     */
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

    /**
     * Removes specified elements from the given array and returns a new array
     * containing only the elements that were not removed.
     *
     * @param array The original array from which elements should be removed.
     *              Must not be null.
     * @param elements The array of elements to be removed from the original array.
     *                  Must not be null.
     * @return A new array containing the elements from the original array that
     *         are not present in the {@code elements} array.
     * @throws NullPointerException If either {@code array} or {@code elements} is null.
     */
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

    /**
     * Creates a new array where the elements are in reversed order
     * compared to the input array.
     *
     * @param array the input array whose elements are to be reversed.
     *              Must not be null.
     * @return a new array with the elements of the input array in reversed order.
     * @throws NullPointerException if the input array is null.
     */
    public static byte @NotNull [] reversed(byte @NotNull [] array) {
        byte[] result = new byte[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = (byte) (array.length - i - 1));
        return result;
    }

    /**
     * Returns a new array containing the elements of the input array in reversed order.
     *
     * @param array the input array to be reversed; must not be null
     * @return a new array that represents the reversed order of the input array
     * @throws NullPointerException if the provided array is null
     */
    public static short @NotNull [] reversed(short @NotNull [] array) {
        short[] result = new short[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = (short) (array.length - i - 1));
        return result;
    }

    /**
     * Creates a new array containing the elements of the input array in reversed order.
     *
     * @param array the input array to be reversed; must not be null
     * @return a new array with elements in reversed order
     * @throws NullPointerException if the input array is null
     */
    public static int @NotNull [] reversed(int @NotNull [] array) {
        int[] result = new int[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    /**
     * Reverses the indices of the given array and returns a new array with the modified order.
     *
     * @param array the input array of long values which must not be null
     * @return a new array of long values with the elements rearranged in reverse order based on index
     * @throws NullPointerException if the provided array is null
     */
    public static long @NotNull [] reversed(long @NotNull [] array) {
        long[] result = new long[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    /**
     * Returns a new array containing the elements of the input array in reverse order.
     *
     * @param array the input array to be reversed; must not be null
     * @return a new array with the elements of the input array in reverse order
     * @throws NullPointerException if the input array is null
     */
    public static float @NotNull [] reversed(float @NotNull [] array) {
        float[] result = new float[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    /**
     * Reverses the given array of doubles and returns the reversed version.
     *
     * @param array the array of doubles to be reversed; must not be null
     * @return a new array of doubles with the elements in reverse order
     * @throws NullPointerException if the input array is null
     */
    public static double @NotNull [] reversed(double @NotNull [] array) {
        double[] result = new double[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array.length - i - 1);
        return result;
    }

    /**
     * Reverses the given character array and returns a new array with elements in reverse order.
     *
     * @param array the character array to be reversed; must not be null
     * @return a new character array containing the elements of the input array in reverse order; never null
     * @throws NullPointerException if the input array is null
     */
    @Contract(pure = true)
    public static char @NotNull [] reversed(char @NotNull [] array) {
        char[] result = new char[array.length];
        Iteration.forEachReversedWithIndex(
                array.length,
                (index, reverseIndex) -> result[index] = array[reverseIndex]);
        return result;
    }

    /**
     * Reverses the elements of the given boolean array.
     *
     * @param array the input boolean array, must not be null
     * @return a new boolean array containing the elements of the input array in reversed order
     * @throws NullPointerException if the input array is null
     */
    public static boolean @NotNull [] reversed(boolean @NotNull [] array) {
        boolean[] result = new boolean[array.length];
        Iteration.forEachIndex(array.length, i -> result[i] = array[array.length - i - 1]);
        return result;
    }

    /**
     * Filters the given byte array by including only those elements
     * that are present in the specified filter array.
     *
     * @param array the byte array to be filtered, must not be null
     * @param filter the byte array specifying the filter criteria, must not be null
     * @return a new byte array containing elements from the input array
     *         that match the filter criteria
     * @throws NullPointerException if the input array or filter array is null
     */
    public static byte @NotNull [] filter(byte @NotNull [] array, byte @NotNull [] filter) {
        byte[] result = new byte[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters the elements in the given array based on the specified filter array.
     * Each element from the input array is retained in the result if it exists
     * in the filter array.
     *
     * @param array the array to be filtered; must not be null
     * @param filter the array containing the values to retain in the result; must not be null
     * @return a new array containing elements from the input array that exist in the filter array
     * @throws NullPointerException if either the array or filter is null
     */
    public static short @NotNull [] filter(short @NotNull [] array, short @NotNull [] filter) {
        short[] result = new short[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters the elements of the given array by including only those elements
     * that are present in the filter array.
     *
     * @param array the input array to filter; must not be null.
     * @param filter the array containing elements to be retained in the result; must not be null.
     * @return a new array containing only the elements of the input array that are present in the filter array.
     * @throws NullPointerException if either the input array or the filter array is null.
     */
    public static int @NotNull [] filter(int @NotNull [] array, int @NotNull [] filter) {
        int[] result = new int[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters the given array by retaining only the elements that are present
     * in the filter array.
     *
     * @param array the array to be filtered; must not be null
     * @param filter the array containing values to retain in the result; must not be null
     * @return a new array containing only the elements from the input array
     *         that are present in the filter array; the length will be equal
     *         to the input array
     * @throws NullPointerException if either the input array or the filter array is null
     */
    public static long @NotNull [] filter(long @NotNull [] array, long @NotNull [] filter) {
        long[] result = new long[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters elements from the input array based on the presence of those elements
     * in the filter array. For each element in the input array, if it exists in the
     * filter array, it will be included in the result array at the same index.
     *
     * @param array the input array to be filtered; must not be null
     * @param filter the array containing elements to include in the result; must not be null
     * @return a new array of the same length as the input array, containing elements
     *         from the input array that are present in the filter array at their corresponding indices
     * @throws NullPointerException if either the {@code array} or {@code filter} is null
     */
    public static float @NotNull [] filter(float @NotNull [] array, float @NotNull [] filter) {
        float[] result = new float[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters an array by retaining only the elements that are also present in the specified filter array.
     *
     * @param array the array to be filtered; must not be null.
     * @param filter the array containing the elements to retain; must not be null.
     * @return a new array containing only the elements of the input array that are present in the filter array.
     * @throws NullPointerException if the {@code array} or {@code filter} parameter is null.
     */
    public static double @NotNull [] filter(double @NotNull [] array, double @NotNull [] filter) {
        double[] result = new double[array.length];
        Iteration.forEachIndex(array.length, i -> {

            Conditionals.ifTrue(contains(filter, array[i]), () -> result[i] = array[i]);

            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters the elements of the input array based on the provided filter array.
     * Only elements in the input array that are present in the filter array
     * will be included in the resulting array. The resulting array maintains
     * the order of the elements from the original input array.
     *
     * @param array the input array of characters to be filtered; must not be null
     * @param filter the array of characters to be used as the filter criteria; must not be null
     * @return a new array of characters containing only the elements from the input array
     *         that are present in the filter array
     * @throws NullPointerException if either the input array or the filter array is null
     */
    public static char @NotNull [] filter(char @NotNull [] array, char @NotNull [] filter) {
        char[] result = new char[array.length];
        Iteration.forEachIndex(array.length, i -> {
            if (contains(filter, array[i])) {
                result[i] = array[i];
            }
        });
        return result;
    }

    /**
     * Filters the elements of the input array based on the specified filter array.
     * The method returns a new array where only the elements of the input array
     * that are present in the filter array are retained.
     *
     * @param array the input array to be filtered; must not be null
     * @param filter the array containing elements to be retained; must not be null
     * @return a new boolean array containing elements from the input array that are present in the filter array;
     *         the array has the same length as the input array
     * @throws NullPointerException if either the input array or the filter array is null
     */
    public static boolean @NotNull [] filter(boolean @NotNull [] array, boolean @NotNull [] filter) {
        boolean[] result = new boolean[array.length];
        Iteration.forEachIndex(array.length, i -> Conditionals
                .ifTrue(contains(filter, array[i]), () -> result[i] = array[i]));
        return result;
    }

    /**
     * Converts a given string into an array of integer digits.
     * Non-digit characters are ignored safely.
     *
     * @param number the input string; must not be null.
     * @return an array of integers containing only the digits found in the string.
     *         Returns an empty array if the string is empty, contains only whitespace,
     *         or no digits are found.
     * @throws NullPointerException if the input number is null.
     */
    public static int @NotNull [] convert(@NotNull String number) {
        if (number.trim().isEmpty()) return new int[0];

        List<Integer> result = new ArrayList<>();

        for (char c : number.toCharArray()) {
            int digit = Character.getNumericValue(c);
            Conditionals.ifTrue(digit >= 0 && digit <= 9, () -> result.add(digit));
        }

        if (result.isEmpty()) return new int[0];

        int[] arr = new int[result.size()];
        Iteration.forEachIndex(result.size(), i -> arr[i] = result.get(i));

        return arr;
    }
}