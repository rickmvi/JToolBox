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
package com.github.rickmvi.jtoolbox.collections.list;

import com.github.rickmvi.jtoolbox.control.Iteration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;

/**
 * Utility class that provides a collection of helper methods for working with {@link List}s and arrays.
 * Part of the Console API - JToolbox library.
 *
 * <p>Includes methods for common operations such as reversing, sorting, filtering, grouping,
 * and retrieving elements from lists and arrays.</p>
 *
 * <p>This class is intended to simplify and streamline common collection operations.</p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class Listing {

    /**
     * Checks if the given list is empty or null.
     * A list is considered empty if it is null or contains no elements.
     *
     * @param list the list to check for emptiness; may be null
     * @return {@code true} if the list is null or empty, {@code false} otherwise
     * @throws NullPointerException if any unexpected null behavior occurs in processing
     */
    @Contract("null -> true")
    public static boolean isEmpty(List<?> list) {
        return Objects.isNull(list) || list.isEmpty() && list.equals(Collections.emptyList());
    }

    /**
     * Returns a new list with the elements in reverse order.
     *
     * @param list the list to reverse
     * @param <T>  the type of elements
     * @return a new list containing the elements in reverse order
     */
    public static <T> @NotNull List<T> reverse(@NotNull List<T> list) {
        List<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Returns a new array with the elements in reverse order.
     *
     * @param array the array to reverse
     * @param <T>   the type of elements
     * @return a new reversed array
     */
    public static <T> @NotNull T @NotNull [] reverseArray(@NotNull T[] array) {
        T[] result = Arrays.copyOf(array, array.length);
        Iteration.forEachHalf(array, i -> {
            T temp = result[i];
            result[i] = result[array.length - 1 - i];
            result[array.length - 1 - i] = temp;
        });
        return result;
    }

    /**
     * Returns an {@link Optional} containing the first element of the list,
     * or empty if the list is empty.
     *
     * @param list the list to get the first element from
     * @param <T>  the type of elements
     * @return an Optional containing the first element, or empty
     */
    public static <T> @NotNull Optional<T> first(@NotNull List<T> list) {
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    /**
     * Returns an {@link Optional} containing the last element of the list,
     * or empty if the list is empty.
     *
     * @param list the list to get the last element from
     * @param <T>  the type of elements
     * @return an Optional containing the last element, or empty
     */
    public static <T> @NotNull Optional<T> last(@NotNull List<T> list) {
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getLast());
    }

    /**
     * Returns a new list with elements sorted in natural ascending order.
     *
     * @param list the list to sort
     * @param <T>  the type of elements (must be {@link Comparable})
     * @return a new sorted list
     */
    public static <T extends Comparable<T>> @NotNull List<T> sort(@NotNull List<T> list) {
        List<T> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.naturalOrder());
        return sorted;
    }

    /**
     * Returns a new list with elements sorted in natural descending order.
     *
     * @param list the list to sort
     * @param <T>  the type of elements (must be {@link Comparable})
     * @return a new sorted list in descending order
     */
    public static <T extends Comparable<T>> @NotNull List<T> sortDescending(@NotNull List<T> list) {
        List<T> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.reverseOrder());
        return sorted;
    }

    /**
     * Returns the maximum element of the list, if present.
     *
     * @param list the list to search
     * @param <T>  the type of elements (must be {@link Comparable})
     * @return an Optional containing the maximum element, or empty
     */
    public static <T extends Comparable<T>> @NotNull Optional<T> max(@NotNull List<T> list) {
        return list.stream().max(Comparator.naturalOrder());
    }

    /**
     * Returns the minimum element of the list, if present.
     *
     * @param list the list to search
     * @param <T>  the type of elements (must be {@link Comparable})
     * @return an Optional containing the minimum element, or empty
     */
    public static <T extends Comparable<T>> @NotNull Optional<T> min(@NotNull List<T> list) {
        return list.stream().min(Comparator.naturalOrder());
    }

    /**
     * Shuffles the elements of the given list in place.
     *
     * @param list the list to shuffle
     * @param <T>  the type of elements
     */
    public static <T> void shuffle(@NotNull List<T> list) {
        Collections.shuffle(list);
    }

    /**
     * Returns the index of the first occurrence of the specified value in the list,
     * or -1 if the list does not contain the value.
     *
     * @param list  the list to search
     * @param value the value to find
     * @param <T>   the type of elements
     * @return the index of the value, or -1 if not found
     */
    public static <T> int indexOf(@NotNull List<T> list, T value) {
        return list.indexOf(value);
    }

    /**
     * Returns a new list containing only the elements that match the given predicate.
     *
     * @param list      the original list
     * @param predicate the condition to filter elements
     * @param <T>       the type of elements
     * @return a new filtered list
     */
    public static <T> @NotNull List<T> filter(@NotNull List<T> list, @NotNull Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) result.add(t);
        }
        return result;
    }

    /**
     * Groups the elements of the list based on a classification function.
     *
     * @param list       the list to group
     * @param classifier the function that classifies elements into keys
     * @param <T>        the type of elements
     * @param <K>        the type of keys
     * @return a map where each key is a group and the value is the list of elements in that group
     */
    public static <T, K> @NotNull Map<K, List<T>> groupBy(@NotNull List<T> list, @NotNull Function<T, K> classifier) {
        Map<K, List<T>> grouped = new HashMap<>();
        for (T t : list) {
            grouped.computeIfAbsent(classifier.apply(t), k -> new ArrayList<>()).add(t);
        }
        return grouped;
    }

    /**
     * Counts the number of elements in the list that match the given predicate.
     *
     * @param list      the list to check
     * @param predicate the condition to match
     * @param <T>       the type of elements
     * @return the number of matching elements
     */
    public static <T> long count(@NotNull List<T> list, @NotNull Predicate<T> predicate) {
        return list.stream().filter(predicate).count();
    }
}
