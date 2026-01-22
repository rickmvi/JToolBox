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

import com.github.rickmvi.jtoolbox.collections.Dynamic;
import com.github.rickmvi.jtoolbox.control.For;
import com.github.rickmvi.jtoolbox.control.Condition;
import com.github.rickmvi.jtoolbox.control.If;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Function;

import static com.github.rickmvi.jtoolbox.text.StringFormatter.format;

/**
 * High-level utility class that provides a rich, functional, and type-safe API for array manipulation.
 * <p>
 * Built on top of {@link Dynamic}, this class offers a static and lightweight interface for
 * working with Java arrays while preserving immutability and type consistency. It combines the
 * simplicity of array operations with the expressiveness of functional-style programming.
 * </p>
 *
 * <h2>Overview</h2>
 * <p>
 * The {@code Array} class enhances the standard {@link java.util.Arrays} utilities by integrating
 * functional constructs and extended operations such as:
 * </p>
 * <ul>
 *   <li>Adding, inserting, and removing elements safely</li>
 *   <li>Concatenation and range-copying of arrays</li>
 *   <li>Mapping, filtering, and distinct transformations</li>
 *   <li>Sorting and reversing arrays (in-place or returning new instances)</li>
 *   <li>Converting arrays to {@link java.util.List} or {@link java.util.Set}</li>
 *   <li>Repeating and joining array elements</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>All methods are {@code static} and return new arrays unless explicitly documented otherwise.</li>
 *   <li>All parameters must be non-null unless specified; invalid indices throw descriptive exceptions.</li>
 *       and {@link IndexOutOfBoundsException}.</li>
 *   <li>Internally uses {@link Dynamic} for fluent data transformations.</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * // Adding an element
 * Integer[] arr = {1, 2, 3};
 * arr = Array.add(arr, 4); // → [1, 2, 3, 4]
 *
 * // Removing by index
 * arr = Array.remove(arr, 1); // → [1, 3, 4]
 *
 * // Concatenating arrays
 * Integer[] more = {5, 6};
 * arr = Array.concat(arr, more); // → [1, 3, 4, 5, 6]
 *
 * // Filtering and mapping
 * String[] str = Array.map(
 *     Array.filter(arr, i -> i % 2 == 0, String[]::new),
 *     Object::toString,
 *     String[]::new
 * ); // → ["4", "6"]
 *
 * // Distinct elements
 * Integer[] distinct = Array.distinct(new Integer[]{1, 2, 2, 3}, Integer[]::new); // → [1, 2, 3]
 *
 * // Reverse and join
 * String joined = Array.join(Array.reversed(arr), ", "); // "6, 5, 4, 3, 1"
 *
 * // Repeat and sum
 * Integer[] repeated = Array.repeat(7, 3, Integer[]::new); // [7, 7, 7]
 * int sum = Array.sum(new int[]{1, 2, 3}); // 6
 * }</pre>
 *
 * <h2>Key Methods</h2>
 * <table border="1" cellspacing="0" cellpadding="4">
 *   <tr><th>Category</th><th>Examples</th></tr>
 *   <tr><td>Mutation</td><td>{@link #add(Object[], int, Object)}, {@link #remove(Object[], int)}</td></tr>
 *   <tr><td>Transformation</td><td>{@link #map(Object[], Function, IntFunction)}, {@link #filter(Object[], Predicate, IntFunction)}</td></tr>
 *   <tr><td>Query</td><td>{@link #indexOf(Object[], Object)}, {@link #contains(Object[], Object)}</td></tr>
 *   <tr><td>Conversion</td><td>{@link #toList(Object[])}, {@link #toSet(Object[])}</td></tr>
 *   <tr><td>Aggregation</td><td>{@link #sum(int...)}, {@link #join(Object[], String)}</td></tr>
 * </table>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is thread-safe when arrays are not modified concurrently from multiple threads.
 * Methods that modify arrays always return new instances to avoid shared-state issues.
 * </p>
 *
 * <h2>See Also</h2>
 * <ul>
 *   <li>{@link Dynamic}</li>
 *   <li>{@link For}</li>
 *   <li>{@link If}</li>
 * </ul>
 * @author Rick M. Viana
 * @version 1.3
 * @since 2025
 * @see For
 * @see Arrays
 * @see Dynamic
 * @apiNote Provides a functional abstraction over native arrays for use within the Console API ecosystem.
 */
@UtilityClass
@EqualsAndHashCode
@SuppressWarnings({"unused", "WeakerAccess"})
public class  Array {

    @SafeVarargs
    @ApiStatus.Internal
    private static <T> @NotNull Dynamic<T> adapt(T @NotNull ... array) {
        return Dynamic.of(array);
    }

    @SafeVarargs
    public static <T> @NotNull Dynamic<T> wrap(T @NotNull ... array) {
        return adapt(array);
    }

    /* ==================================== ADD METHOD ========================================= */

    @Deprecated
    public static <T> T @NotNull [] add(T @NotNull [] array, int index, T element) {
        If.throwIf(index < 0 || index > array.length,
                () -> new IndexOutOfBoundsException(format("Index: {}, Length: {}", index, length(array))));

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

    public static <T> T[] add(T[] array, T element) {
        return adapt(array)
                .add(element)
                .toArray(size -> Arrays.copyOf(array, size));
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

    public static <T> boolean contains(T @NotNull [] array, T element) {
        return adapt(array).contains(element);
    }

    /* ==================================== IS EMPTY METHOD ========================================= */

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /* ==================================== LENGTH METHOD ========================================= */

    public static int length(Object[] array) {
        return isEmpty(array) ? 0 : array.length;
    }

    public static int lastIndex(Object[] array) {
        return isEmpty(array) ? -1 : array.length - 1;
    }

    /* =================================== COPY RANGE METHOD ======================================== */

    public static <T> T @NotNull [] copyRange(T @NotNull [] array, int from, int to) {
        Condition.ThrowWhen(from < 0 || to > array.length || from > to,
                () -> new IndexOutOfBoundsException(format("Invalid range: {} to {}", from, to)));
        return Arrays.copyOfRange(array, from, to);
    }

    /* =================================== REMOVER METHOD'S ======================================== */

    public static <T> T @NotNull [] remove(T @NotNull [] array, int index) {
        Condition.ThrowWhen(index < 0 || index >= length(array), () ->
                new IndexOutOfBoundsException(format("Index: {}, Length: {}", index, length(array))));
        T[] result = Arrays.copyOf(array, array.length - 1);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static <T> T @NotNull [] remove(T @NotNull [] array, T element) {
        return adapt(array)
                .remove(element)
                .toArray(size -> Arrays.copyOf(array, size));
    }

    @SafeVarargs
    public static <T> T @NotNull [] removeAll(T @NotNull [] array, T @NotNull ... elements) {
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

    public static <T> @NotNull T @NotNull [] reverse(T @NotNull [] array) {
        return adapt(array)
                .reversed()
                .toArray(size -> Arrays.copyOf(array, size));
    }

    public static <T> void reverseInPlace(T @NotNull [] array) {
        For.range(0, length(array) / 2)
                .forEach(i -> {
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
        return adapt(array)
                .map(mapper)
                .toArray(generator);
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

    public static @NotNull String join(Object @NotNull [] array, @NotNull String delimiter) {
        return adapt(array).join(delimiter);
    }

    /* ================================ To List / Set METHOD'S ==================================== */

    public static <T> List<T> toList(T[] array) {
        return adapt(array).toList();
    }

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

    public static void sort(long @NotNull ... array) {
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
        return Arrays.stream(array).sum();
    }

    public static long sum(long @NotNull ... array) {
        return Arrays.stream(array).sum();
    }

    public static double sum(double @NotNull ... array) {
        return Arrays.stream(array).sum();
    }

    /* ==================================== REPEAT METHOD ========================================= */

    public static <T> T[] repeat(T value, int times, @NotNull IntFunction<T[]> generator) {
        If.throwIf(times < 0, () -> new IllegalArgumentException("times: " + times));
        T[] array = generator.apply(times);
        Arrays.fill(array, value);
        return array;
    }
}
