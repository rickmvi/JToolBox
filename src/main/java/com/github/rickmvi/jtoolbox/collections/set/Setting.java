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
package com.github.rickmvi.jtoolbox.collections.set;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for common operations on {@link Set} collections.
 * <p>
 * Provides helper methods for case-insensitive lookups, filtering, mapping,
 * and performing standard set operations such as union, intersection, and difference.
 * </p>
 *
 * <p>
 * All methods are static and null-safe when used with non-null arguments.
 * </p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@UtilityClass
public class Setting {

    /**
     * Checks if the set contains a value, ignoring case.
     *
     * @param set   the set to check
     * @param value the value to look for (as string representation)
     * @param <T>   the type of the set elements
     * @return {@code true} if the set contains the value (case-insensitive), {@code false} otherwise
     */
    public static <T> boolean containsIgnoreCase(@NotNull java.util.Set<T> set, @NotNull T value) {
        return set.stream().anyMatch(v -> v.toString().equalsIgnoreCase(value.toString()));
    }

    /**
     * Filters the elements of a set using the given predicate.
     *
     * @param set       the input set
     * @param predicate the filter condition
     * @param <T>       the type of elements
     * @return a new set containing only elements that match the predicate
     */
    public static <T> Set<T> filterSet(@NotNull Set<T> set, Predicate<T> predicate) {
        return set.stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * Maps the elements of a set to another type using a mapper function.
     *
     * @param set    the input set
     * @param mapper the mapping function
     * @param <T>    the type of original elements
     * @param <R>    the type of resulting elements
     * @return a new set with mapped values
     */
    public static <T, R> Set<R> mapSet(@NotNull Set<T> set, @NotNull Function<T, R> mapper) {
        return set.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * Checks if any element in the set matches the given predicate.
     *
     * @param set       the input set
     * @param predicate the condition to test
     * @param <T>       the type of elements
     * @return {@code true} if any element matches, {@code false} otherwise
     */
    public static <T> boolean anyInSet(@NotNull Set<T> set, Predicate<T> predicate) {
        return set.stream().anyMatch(predicate);
    }

    /**
     * Checks if all elements in the set match the given predicate.
     *
     * @param set       the input set
     * @param predicate the condition to test
     * @param <T>       the type of elements
     * @return {@code true} if all elements match, {@code false} otherwise
     */
    public static <T> boolean allInSet(@NotNull Set<T> set, Predicate<T> predicate) {
        return set.stream().allMatch(predicate);
    }

    /**
     * Finds the first element in the set that matches the predicate.
     *
     * @param set       the input set
     * @param predicate the condition to test
     * @param <T>       the type of elements
     * @return an {@link Optional} containing the first matching element, or empty if none found
     */
    public static <T> @NotNull Optional<T> findFirstInSet(@NotNull Set<T> set, Predicate<T> predicate) {
        return set.stream().filter(predicate).findFirst();
    }

    /**
     * Gets the first element in the set, or {@code null} if the set is empty.
     *
     * @param set the input set
     * @param <T> the type of elements
     * @return the first element or {@code null} if the set is empty
     */
    public static <T> T firstInSet(@NotNull Set<T> set) {
        return set.stream().findFirst().orElse(null);
    }

    /**
     * Returns the union of two sets (all unique elements from both).
     *
     * @param a   the first set
     * @param b   the second set
     * @param <T> the type of elements
     * @return a new set containing the union of {@code a} and {@code b}
     */
    public static <T> @NotNull Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }

    /**
     * Returns the intersection of two sets (common elements).
     *
     * @param a   the first set
     * @param b   the second set
     * @param <T> the type of elements
     * @return a new set containing the elements common to both {@code a} and {@code b}
     */
    public static <T> @NotNull Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    /**
     * Returns the difference of two sets (elements in {@code a} that are not in {@code b}).
     *
     * @param a   the first set
     * @param b   the second set
     * @param <T> the type of elements
     * @return a new set containing elements from {@code a} excluding those in {@code b}
     */
    public static <T> @NotNull Set<T> difference(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }
}

