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
package com.github.rickmvi.jtoolbox.collections.array;

import com.github.rickmvi.jtoolbox.lang.exceptions.InvalidStartIndexException;
import com.github.rickmvi.jtoolbox.utils.ArrayUtils;
import com.github.rickmvi.jtoolbox.control.Conditionals;
import com.github.rickmvi.jtoolbox.control.Iteration;

import com.github.rickmvi.jtoolbox.utils.MathOperations;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.rickmvi.jtoolbox.text.Formatted.format;

@lombok.experimental.UtilityClass
public class Array extends ArrayUtils {

    /* ==================================== ADD METHOD ========================================= */

    public static <T> T @NotNull [] add(T @NotNull [] array, T element) {
        T[] result = java.util.Arrays.copyOf(array, length(array) + 1);
        result[length(array)] = element;
        return result;
    }

    public static <T> T @NotNull [] add(T @NotNull [] array, int index, T element) {
        Conditionals.ifTrueThrow(index < 0 || index > array.length, () ->
                new InvalidStartIndexException(format("Index: {}, Length: {}", index, length(array))));
        T[] result = java.util.Arrays.copyOf(array, array.length + 1);

        System.arraycopy(
                array,
                index,
                result,
                index + 1,
                length(array) - index);
        result[index] = element;

        return result;
    }

    /* ==================================== CONCAT METHOD ========================================= */

    /**
     * Concatenates two arrays of the same type into a single array.
     *
     * @param <T> the type of the elements in the arrays
     * @param first the first array to be concatenated, which may be null
     * @param second the second array to be concatenated, must not be null
     * @return a new array containing all elements from the first array followed by all elements from the second array
     * @throws NullPointerException if the {@code second} array is null
     */
    public static <T> T @NotNull [] concat(T[] first, T @NotNull [] second) {
        T[] result = java.util.Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /* ==================================== INDEX METHOD ========================================= */

    /**
     * Returns the index of the first occurrence of the specified element in the given array.
     * If the element is not present in the array, returns -1.
     *
     * @param <T>     The type of elements in the array.
     * @param array   The array to search for the element. Must not be null.
     * @param element The element to locate in the array. Can be null.
     * @return The index of the first occurrence of the specified element in the array,
     *         or -1 if the element is not found.
     * @throws NullPointerException If the provided array is null.
     */
    public static <T> int indexOf(T @NotNull [] array, T element) {
        return Iteration.findFirstIndexMatching(length(array), i -> Objects.equals(array[i], element));
    }

    /**
     * Returns the index of the last occurrence of the specified element in the given array.
     * If the element is not found, it returns -1.
     *
     * @param <T>    the type of elements in the array
     * @param array  the array to search, must not be null
     * @param element the element to find in the array, can be null
     * @return the index of the last occurrence of the specified element in the array,
     *         or -1 if the element is not found
     * @throws NullPointerException if the array is null
     */
    public static <T> int lastIndexOf(T @NotNull [] array, T element) {
        return Iteration.findLastIndexMatching(length(array), i -> Objects.equals(array[i], element));
    }

    /* ==================================== CONTAINS METHOD ========================================= */

    /**
     * Checks if a specified element is present in the given array.
     *
     * @param <T>    the type of elements in the array
     * @param array  the array to search through; must not be null
     * @param element the element to search for; can be null
     * @return {@code true} if the array contains the specified element,
     *         {@code false} otherwise
     * @throws NullPointerException if the specified array is null
     */
    @Contract(pure = true)
    public static <T> boolean contains(T @NotNull [] array, T element) {
        for (T t : array) {
            if (Objects.equals(t, element)) return true;
        }
        return false;
    }

    /* ==================================== IS EMPTY METHOD ========================================= */

    /**
     * Checks whether the given array is null or empty.
     *
     * @param array the array to be checked for emptiness; may be null
     * @return {@code true} if the array is null or has no elements;
     *         {@code false} otherwise
     */
    @Contract(value = "null -> true", pure = true)
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /* ==================================== LENGTH METHOD ========================================= */

    @Contract(pure = true)
    public static int length(Object[] array) {
        return isEmpty(array) ? 0 : array.length;
    }

    /* =================================== COPY RANGE METHOD ======================================== */

    /**
     * Creates and returns a new array containing a portion of the elements
     * from the provided source array. The portion is defined by the specified
     * range of indexes [from, to), where the start index is inclusive and the
     * end index is exclusive.
     *
     * @param <T>  the type of elements in the array
     * @param array the source array from which elements will be copied; must not be null
     * @param from  the starting index (inclusive) of the range to copy; must be non-negative
     * @param to    the ending index (exclusive) of the range to copy; must not exceed the array's length
     * @return a new array containing the specified range of elements from the source array
     * @throws NullPointerException if the provided array is null
     * @throws IndexOutOfBoundsException if the specified range is invalid, such as
     *         when {@code from < 0}, {@code to > array.length}, or {@code from > to}
     */
    public static <T> T @NotNull [] copyRange(T @NotNull [] array, int from, int to) {
        Conditionals.ifTrueThrow(from < 0 || to > array.length || from > to,
                () -> new IndexOutOfBoundsException(format("Invalid range: {} to {}",from, to)));
        return java.util.Arrays.copyOfRange(array, from, to);
    }

    /* =================================== REMOVER METHOD'S ======================================== */

    /**
     * Removes the element at the specified index from the given array and returns a new array
     * without the removed element. The order of the remaining elements is preserved.
     *
     * @param <T>   the type of elements in the array
     * @param array the array from which the element should be removed; must not be null
     * @param index the index of the element to remove; must be within the bounds of the array
     * @return a new array with the specified element removed; never null
     * @throws NullPointerException if the array is null
     * @throws IndexOutOfBoundsException if the index is negative or greater than or equal to the array's length
     */
    public static <T> T @NotNull [] remove(T @NotNull [] array, int index) {
        Conditionals.ifTrueThrow(index < 0 || index >= array.length, () ->
                new InvalidStartIndexException(format("Index: {}, Length: {}", index, length(array))));
        T[] result = java.util.Arrays.copyOf(array, array.length - 1);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    /**
     * Removes the specified element from the given array, if it exists.
     * If the element is not present in the array, the original array is returned unchanged.
     *
     * @param <T>     the type of the elements in the array
     * @param array   the array from which the element should be removed; must not be null
     * @param element the element to remove from the array
     * @return a new array with the element removed, or the original array if the element is not found
     * @throws NullPointerException if the array is null or contains null elements
     */
    public static <T> T @NotNull [] remove(T @NotNull [] array, T element) {
        int index = indexOf(array, element);
        return index >= 0 ? remove(array, index) : array;
    }

    /**
     * Removes all occurrences of the specified elements from the given array.
     *
     * @param <T> the type of elements in the array
     * @param array the array from which elements will be removed must not be null
     * @param elements the array of elements to be removed from the input array must not be null
     * @return a new array with the specified elements removed, never null
     * @throws NullPointerException if either the input array or element array is null
     */
    public static <T> T @NotNull [] removeAll(T @NotNull [] array, T @NotNull [] elements) {
        T[] result = array;
        for (T element : elements) {
            result = remove(result, element);
        }
        return result;
    }

    /**
     * Removes all occurrences of the specified elements from the given array.
     *
     * @param <T> the type of elements in the array
     * @param array the array from which elements will be removed must not be null
     * @param elements an iterable containing the elements to be removed must not be null
     * @return a new array with all specified elements removed
     * @throws NullPointerException if either the array or elements parameter is null
     */
    public static <T> T @NotNull [] removeAll(T @NotNull [] array, @NotNull Iterable<T> elements) {
        T[] result = array;
        for (T element : elements) {
            result = remove(result, element);
        }
        return result;
    }

    /* ==================================== REVERSE METHOD'S ========================================= */

    /**
     * Returns a new array that is the reversed form of the input array.
     * The input array remains unchanged.
     *
     * @param <T>  the type of elements in the array
     * @param array the input array to be reversed must not be null
     * @return a new array that contains the elements of the input array in reverse order, never null
     * @throws NullPointerException if the input array is null
     */
    public static <T> @NotNull T @NotNull [] reversed(T @NotNull [] array) {
        T[] result = java.util.Arrays.copyOf(array, array.length);
        Iteration.forEachHalf(array.length, i -> {
            T t = result[i];
            result[i] = result[array.length - i - 1];
            result[array.length - i - 1] = t;
        });
        return result;
    }

    /**
     * Reverses the elements of the given array in place.
     * This method modifies the original array by reversing
     * the order of its elements.
     *
     * @param <T> The type of the elements in the array.
     * @param array The array whose elements are to be reversed.
     *              Must not be null.
     * @throws NullPointerException If the array is null.
     */
    public static <T> void reverseInPlace(T @NotNull [] array) {
        Iteration.forEachHalf(array.length, i -> {
            T t = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = t;
        });
    }

    /* =================================== FILTER METHOD ======================================= */

    /**
     * Filters the elements of an input array based on a given predicate and returns a new array
     * containing only the elements that satisfy the predicate.
     *
     * @param <T>       The type of elements in the input array and the resulting array.
     * @param array     The input array to be filtered. Must not be null.
     * @param predicate A predicate to test each element. Must not be null.
     * @param generator A function to create a new array of the correct type and size. Must not be null.
     * @return A new array containing elements from the input array that satisfy the given predicate.
     * @throws NullPointerException If any of the parameters (`array`, `predicate`, or `generator`) is null.
     */
    public static <T> T @NotNull [] filter(T @NotNull [] array, @NotNull Predicate<T> predicate, @NotNull IntFunction<T[]> generator) {
        List<T> list = new ArrayList<>();
        for (T t : array) {
            if (predicate.test(t)) list.add(t);
        }
        return list.toArray(generator.apply(list.size()));
    }

    /* ==================================== MAP METHOD ========================================= */

    /**
     * Applies the given mapping function to each element of the input array and
     * returns a new array containing the mapped results.
     *
     * @param <T> The type of the elements in the input array.
     * @param <R> The type of the elements in the resulting array.
     * @param array The input array to be mapped over. Must not be null.
     * @param mapper A function to apply to each element of the input array. Must not be null.
     * @param generator A function to create an array of the resulting type, given the required size. Must not be null.
     * @return A new array containing the results of applying the mapping function to the elements of the input array.
     * @throws NullPointerException If the input array, mapper, or generator is null.
     */
    public static <T, R> R[] map(T @NotNull [] array, @NotNull Function<T, R> mapper, @NotNull IntFunction<R[]> generator) {
        R[] result = generator.apply(array.length);
        Iteration.forEachIndex(array.length, i -> result[i] = mapper.apply(array[i]));
        return result;
    }
    /* ================================== DISTINCT METHOD ======================================= */

    /**
     * Returns a new array containing distinct elements from the provided array.
     * The order of elements in the resulting array is the same as their first occurrence
     * in the original array.
     *
     * @param <T>       the type of elements in the array.
     * @param array     the input array from which distinct elements are to be extracted;
     *                  must not be {@code null}.
     * @param generator a function to generate a new array of the same type and size
     *                  as the result; must not be {@code null}.
     * @return a new array containing only the distinct elements of the input array.
     * @throws NullPointerException if either {@code array} or {@code generator} is {@code null}.
     */
    public static <T> T @NotNull [] distinct(T @NotNull [] array, @NotNull IntFunction<T[]> generator) {
        return java.util.Arrays.stream(array).distinct().toArray(generator);
    }

    /* ==================================== JOIN METHOD'S ========================================= */

    /**
     * Joins the elements of the provided array into a single {@code String} with
     * the elements separated by the specified delimiter.
     *
     * @param array the array containing the elements to be joined; must not be {@code null}.
     *              Each element in the array will be converted to a string using {@code String.valueOf(Object)}.
     * @param delimiter the string to be used as a separator between the array elements;
     *                  must not be {@code null}.
     * @return a {@code String} that consists of the {@code String} representations of the
     *         array elements separated by the specified delimiter.
     * @throws NullPointerException if {@code array} or {@code delimiter} is {@code null}.
     */
    @Contract("_, _ -> new")
    public static @NotNull String join(Object @NotNull [] array, @NotNull String delimiter) {
        return String.join(delimiter, java.util.Arrays.stream(array)
                .map(String::valueOf)
                .toArray(String[]::new));
    }

    /* ================================ To List / Set METHOD'S ==================================== */

    /**
     * Converts the specified array into a {@code List}. If the array is null, an empty list is returned.
     *
     * @param <T>   the type of elements in the array.
     * @param array the array to be converted into a {@code List}. Can be {@code null}.
     * @return a {@code List} containing the elements of the array. If the array is {@code null},
     *         an empty {@code List} is returned.
     * @throws NullPointerException if the array is not null and contains {@code null} elements,
     *                              and any operation on the resulting {@code List} does not support
     *                              {@code null} elements.
     */
    @Contract("_ -> !null")
    public static <T> List<T> toList(T[] array) {
        return array == null ? Collections.emptyList() : java.util.Arrays.asList(array);
    }

    /**
     * Converts the specified array into a {@link Set}. If the array is null, an empty set is returned.
     *
     * @param <T>   the type of elements in the array.
     * @param array the array to be converted into a {@code Set}. Can be {@code null}.
     * @return a {@code Set} containing the elements of the array. If the array is {@code null},
     *         an empty {@code Set} is returned.
     * @throws NullPointerException if the array contains {@code null} elements and the resulting
     *                              {@code Set} implementation does not support {@code null} values.
     */
    @Contract("!null -> new")
    public static <T> @NotNull Set<T> toSet(T[] array) {
        return array == null ? Collections.emptySet() : new LinkedHashSet<>(java.util.Arrays.asList(array));
    }

    /* ==================================== SORT METHOD'S ========================================= */

    /**
     * Sorts the elements of the specified array in ascending natural order.
     * The elements of the array must implement the {@link Comparable} interface.
     *
     * @param <T>   the type of elements in the array; must extend {@code Comparable<? super T>}.
     * @param array the array to be sorted; must not be {@code null}.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    public static <T extends Comparable<? super T>> void sort(T @NotNull [] array) {
        java.util.Arrays.sort(array);
    }

    /**
     * Sorts the elements of the specified array of integers in ascending numerical order.
     *
     * @param array the array of integers to be sorted; must not be {@code null}.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    public static void sort(int @NotNull [] array) {
        java.util.Arrays.sort(array);
    }

    /**
     * Sorts the elements of the specified array of doubles in ascending numerical order.
     *
     * @param array the array of doubles to be sorted; must not be {@code null}.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    public static void sort(double @NotNull [] array) {
        java.util.Arrays.sort(array);
    }

    /**
     * Sorts the elements of the specified array of floats in ascending numerical order.
     *
     * @param array the array of floats to be sorted; must not be {@code null}.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    public static void sort(float @NotNull [] array) {
        java.util.Arrays.sort(array);
    }

    /* ==================================== SUM METHOD'S ========================================= */

    /**
     * Computes and returns the sum of all elements in the given array of integers.
     *
     * @param array the array of integers whose elements are to be summed; must not be {@code null}.
     * @return the sum of all the integer elements in the array.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    @Contract(pure = true)
    public static int sum(int @NotNull [] array) {
        return MathOperations.sumInt(array);
    }

    /**
     * Computes and returns the sum of all elements in the given array of longs.
     *
     * @param array the array of longs whose elements are to be summed; must not be {@code null}.
     * @return the sum of all the long elements in the array.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    @Contract(pure = true)
    public static long sum(long @NotNull [] array) {
        return MathOperations.sumLong(array);
    }

    /**
     * Computes and returns the sum of all elements in the given array of floats.
     *
     * @param array the array of floats whose elements are to be summed; must not be {@code null}.
     * @return the sum of all the float elements in the array.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    @Contract(pure = true)
    public static float sum(float @NotNull [] array) {
        return MathOperations.sumFloat(array);
    }

    /**
     * Computes and returns the sum of all elements in the given array of doubles.
     *
     * @param array the array of doubles whose elements are to be summed; must not be {@code null}.
     * @return the sum of all the double elements in the array.
     * @throws NullPointerException if the provided {@code array} is {@code null}.
     */
    @Contract(pure = true)
    public static double sum(double @NotNull [] array) {
        return MathOperations.sumDouble(array);
    }

    /* ==================================== REPEAT METHOD ========================================= */

    /**
     * Creates a new array of the specified size, fills it with the given value, and returns it.
     *
     * @param <T> the type of the elements in the array.
     * @param value the value to fill the array with; can be {@code null}.
     * @param times the number of elements in the resulting array; must be non-negative.
     * @param generator a function to generate a new array of the specified size; must not be {@code null}.
     * @return a new array of type {@code T}, filled with the specified value.
     * @throws IllegalArgumentException if {@code times} is negative.
     * @throws NullPointerException if {@code generator} is {@code null}.
     */
    @Contract("_, _, _ -> new")
    public static <T> T[] repeat(T value, int times, @NotNull IntFunction<T[]> generator) {
        Conditionals.ifTrueThrow(times < 0, () -> new IllegalArgumentException("times: " + times));
        T[] array = generator.apply(times);
        Arrays.fill(array, value);
        return array;
    }
}
