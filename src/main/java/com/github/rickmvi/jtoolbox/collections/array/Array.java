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
import com.github.rickmvi.jtoolbox.control.For;
import com.github.rickmvi.jtoolbox.util.ArrayUtils;
import com.github.rickmvi.jtoolbox.control.If;

import com.github.rickmvi.jtoolbox.util.MathUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Function;

import static com.github.rickmvi.jtoolbox.text.StringFormatter.format;

/**
 * Utility class for working with arrays in a functional and safe way.
 * <p>
 * This class extends {@link ArrayUtils} and provides additional methods for:
 * <ul>
 *     <li>Adding, removing, and inserting elements</li>
 *     <li>Concatenation of arrays</li>
 *     <li>Finding indices and checking containment</li>
 *     <li>Copying, reversing, filtering, and mapping arrays</li>
 *     <li>Converting arrays to {@link List} or {@link Set}</li>
 *     <li>Sorting arrays of various types</li>
 *     <li>Summing numeric arrays</li>
 *     <li>Repeating values to create arrays</li>
 *     <li>Joining arrays into strings</li>
 *     <li>Getting distinct elements</li>
 * </ul>
 * </p>
 *
 * <h2>Examples:</h2>
 * <pre>{@code
 * // Adding an element
 * Integer[] arr = {1, 2, 3};
 * arr = Array.add(arr, 4); // [1, 2, 3, 4]
 *
 * // Removing an element
 * arr = Array.remove(arr, 1); // [1, 3, 4]
 *
 * // Concatenating arrays
 * Integer[] b = {5, 6};
 * arr = Array.concat(arr, b); // [1, 3, 4, 5, 6]
 *
 * // Mapping an array
 * String[] sArr = Array.map(arr, Object::toString, String[]::new);
 *
 * // Filtering an array
 * Integer[] filtered = Array.filter(arr, i -> i % 2 == 0, Integer[]::new); // [4, 6]
 *
 * // Getting distinct elements
 * Integer[] distinct = Array.distinct(new Integer[]{1,2,2,3}, Integer[]::new); // [1,2,3]
 *
 * // Reversing an array
 * Integer[] reversed = Array.reversed(arr); // [6,5,4,3,1]
 *
 * // Summing numeric arrays
 * int sum = Array.sum(new int[]{1,2,3}); // 6
 *
 * // Repeating a value
 * Integer[] repeated = Array.repeat(7, 3, Integer[]::new); // [7,7,7]
 * }</pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>All array parameters must not be {@code null} unless explicitly allowed.</li>
 *     <li>Methods that modify arrays (like {@code remove}, {@code reverseInPlace}) return new arrays or modify in place depending on the method.</li>
 *     <li>Methods are designed to work with generics and preserve type safety using {@link IntFunction} generators.</li>
 *     <li>Index-based methods throw {@link InvalidStartIndexException} or {@link IndexOutOfBoundsException} when indices are invalid.</li>
 *     <li>Numeric operations delegate to {@link MathUtils} for type-specific calculations.</li>
 * </ul>
 *
 * @apiNote This class provides functional and safe alternatives to {@link java.util.Arrays} methods,
 *          with added convenience for generics, type safety, and array manipulation.
 * @see ArrayUtils
 * @see For
 * @see Arrays
 * @see AdaptiveArray
 * @since 1.2
 */
@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Array extends ArrayUtils {

    @SafeVarargs
    @ApiStatus.Internal
    private static <T> @NotNull AdaptiveArray<T> adapt(T @NotNull ... array) {
        return AdaptiveArray.of(array);
    }

    @SafeVarargs
    public static <T> @NotNull AdaptiveArray<T> from(T @NotNull ... array) {
        return adapt(array);
    }

    /* ==================================== ADD METHOD ========================================= */

    public static <T> T @NotNull [] add(T @NotNull [] array, int index, T element) {
        If.Throws(index < 0 || index > array.length, () ->
                new InvalidStartIndexException(format("Index: {}, Length: {}", index, length(array))));
        T[] result = Arrays.copyOf(array, array.length + 1);

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

    public static <T> T @NotNull [] concat(T[] first, T @NotNull [] second) {
        return adapt(first)
                .addAll(second)
                .toArray(size -> Arrays.copyOf(first, size));
    }

    /* ==================================== INDEX METHOD ========================================= */

    public static <T> int indexOf(T @NotNull [] array, T element) {
        return For.range(0, length(array))
                .filter(i -> Objects.equals(array[i], element))
                .findFirst()
                .orElse(-1);
    }

    public static <T> int lastIndexOf(T @NotNull [] array, T element) {
        return For.range(0, length(array))
                .filter(i -> Objects.equals(array[i], element))
                .findLast()
                .orElse(-1);
    }

    /* ==================================== CONTAINS METHOD ========================================= */

    @Contract(pure = true)
    public static <T> boolean contains(T @NotNull [] array, T element) {
        return adapt(array).contains(element);
    }

    /* ==================================== IS EMPTY METHOD ========================================= */

    @Contract(value = "null -> true", pure = true)
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /* ==================================== LENGTH METHOD ========================================= */

    @Contract(pure = true)
    public static int length(Object[] array) {
        return isEmpty(array) ? 0 : array.length;
    }

    @Contract(pure = true)
    public static int lastIndex(Object[] array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    /* =================================== COPY RANGE METHOD ======================================== */

    public static <T> T @NotNull [] copyRange(T @NotNull [] array, int from, int to) {
        If.Throws(from < 0 || to > array.length || from > to,
                () -> new IndexOutOfBoundsException(format("Invalid range: {} to {}", from, to)));
        return Arrays.copyOfRange(array, from, to);
    }

    /* =================================== REMOVER METHOD'S ======================================== */

    public static <T> T @NotNull [] remove(T @NotNull [] array, int index) {
        If.Throws(index < 0 || index >= length(array), () ->
                new InvalidStartIndexException(format("Index: {}, Length: {}", index, length(array))));
        T[] result = Arrays.copyOf(array, array.length - 1);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static <T> T @NotNull [] remove(T @NotNull [] array, T element) {
        return adapt(array)
                .remove(element)
                .toArray(size -> Arrays.copyOf(array, size));
    }

    public static <T> T @NotNull [] removeAll(T @NotNull [] array, T @NotNull [] elements) {
        return adapt(array)
                .removeIf(new HashSet<>(Arrays.asList(elements))::contains)
                .toArray(size -> Arrays.copyOf(array, size));
    }

    public static <T> T @NotNull [] removeAll(T @NotNull [] array, @NotNull Iterable<T> elements) {
        T[] result = array;
        for (T element : elements) {
            result = remove(result, element);
        }
        return result;
    }

    /* ==================================== REVERSE METHOD'S ========================================= */

    public static <T> @NotNull T @NotNull [] reversed(T @NotNull [] array) {
        T[] result = Arrays.copyOf(array, array.length);
        For.range(0, length(array) / 2).forEach(i -> {
            T t = result[i];
            result[i] = result[length(array) - i - 1];
            result[length(array) - i - 1] = t;
        });
        return result;
    }

    public static <T> void reverseInPlace(T @NotNull [] array) {
        For.range(0, length(array) / 2).forEach(i -> {
            T t = array[i];
            array[i] = array[length(array) - i - 1];
            array[length(array) - i - 1] = t;
        });
    }

    /* =================================== FILTER METHOD ======================================= */

    public static <T> T @NotNull [] filter(T @NotNull [] array, @NotNull Predicate<T> predicate, @NotNull IntFunction<T[]> generator) {
        return adapt(array)
                .filter(predicate)
                .toArray(generator);
    }

    /* ==================================== MAP METHOD ========================================= */

    public static <T, R> R[] map(T @NotNull [] array, @NotNull Function<T, R> mapper, @NotNull IntFunction<R[]> generator) {
        R[] result = generator.apply(array.length);
        For.range(0, length(array) - 1).forEach(i -> result[i] = mapper.apply(array[i]));
        return result;
    }
    /* ================================== DISTINCT METHOD ======================================= */

    public static <T> T @NotNull [] distinct(T @NotNull [] array, @NotNull IntFunction<T[]> generator) {
        return adapt(array)
                .distinct()
                .toArray(generator);
    }

    public static <T, K> T @NotNull [] distinctBy(
            T @NotNull [] array,
            @NotNull Function<T, K> keyExtractor,
            @NotNull IntFunction<T[]> generator
    ) {
        return adapt(array)
                .distinctBy(keyExtractor)
                .toArray(generator);
    }

    /* ==================================== JOIN METHOD'S ========================================= */

    @Contract("_, _ -> new")
    public static @NotNull String join(Object @NotNull [] array, @NotNull String delimiter) {
        return String.join(delimiter, Arrays.stream(array)
                .map(String::valueOf)
                .toArray(String[]::new));
    }

    /* ================================ To List / Set METHOD'S ==================================== */

    @Contract("_ -> !null")
    public static <T> List<T> toList(T[] array) {
        return adapt(array).toList();
    }

    @Contract("!null -> new")
    public static <T> @NotNull Set<T> toSet(T[] array) {
        return adapt(array).toSet();
    }

    /* ==================================== SORT METHOD'S ========================================= */

    public static <T extends Comparable<? super T>> void sort(T @NotNull [] array) {
        Arrays.sort(array);
    }

    public static void sort(int @NotNull ... array) {
        Arrays.sort(array);
    }

    public static void sort(double @NotNull ... array) {
        Arrays.sort(array);
    }

    public static void sort(float @NotNull ... array) {
        Arrays.sort(array);
    }

    /* ==================================== SUM METHOD'S ========================================= */

    public static int sum(int @NotNull ... array) {
        return MathUtils.sumInt(array);
    }

    public static long sum(long @NotNull ... array) {
        return MathUtils.sumLong(array);
    }

    public static float sum(float @NotNull ... array) {
        return MathUtils.sumFloat(array);
    }

    public static double sum(double @NotNull ... array) {
        return MathUtils.sumDouble(array);
    }

    /* ==================================== REPEAT METHOD ========================================= */

    @Contract("_, _, _ -> new")
    public static <T> T[] repeat(T value, int times, @NotNull IntFunction<T[]> generator) {
        If.Throws(times < 0, () -> new IllegalArgumentException("times: " + times));
        T[] array = generator.apply(times);
        Arrays.fill(array, value);
        return array;
    }
}
