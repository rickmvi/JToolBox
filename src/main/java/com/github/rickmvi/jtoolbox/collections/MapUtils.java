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
package com.github.rickmvi.jtoolbox.collections;

import org.jetbrains.annotations.NotNull;

import java.util.function.*;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;
import java.util.Map;

/**
 * Utility class for working with {@link Map} collections.
 * Provides advanced utilities for filtering, mapping, sorting, and case-insensitive key checking.
 *
 * <p>Part of the Console API - JToolbox.</p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class MapUtils {

    /**
     * Iterates over each entry in the provided map and applies the specified action to it.
     *
     * @param <K>    the type of keys in the map
     * @param <V>    the type of values in the map
     * @param map    the map whose entries are to be processed; must not be null
     * @param action the action to perform on each key-value pair; must not be null
     * @throws NullPointerException if the map or action is null
     */
    public static <K, V> void forEach(@NotNull Map<K, V> map, BiConsumer<K, V> action) {
        map.forEach(action);
    }

    /**
     * Iterates over each entry in the provided map and performs the specified action on it.
     *
     * @param <K>    the type of keys in the map
     * @param <V>    the type of values in the map
     * @param map    the map whose entries are to be processed; must not be null
     * @param action the action to perform on each map entry; must not be null
     * @throws NullPointerException if the map or action is null
     */
    public static <K, V> void forEachEntry(@NotNull Map<K, V> map, Consumer<Map.Entry<K, V>> action) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            action.accept(entry);
        }
    }

    /**
     * Retrieves the value associated with the specified key or returns a default value if not found.
     *
     * @param map          the map to search
     * @param key          the key to look for
     * @param defaultValue the value to return if the key is not present
     * @param <K>          the type of keys
     * @param <V>          the type of values
     * @return the associated value or the default
     */
    public static <K, V> V getOrDefault(@NotNull Map<K, V> map, K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Filters entries of a map using a key-value predicate.
     *
     * @param map       the map to filter
     * @param predicate the condition to apply to each entry
     * @param <K>       the type of keys
     * @param <V>       the type of values
     * @return a new map containing entries that satisfy the predicate
     */
    public static <K, V> Map<K, V> filterMap(@NotNull Map<K, V> map, BiPredicate<K, V> predicate) {
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getKey(), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Transforms each entry in the map to another form using a mapping function.
     *
     * @param map    the source map
     * @param mapper the function to apply to each key-value pair
     * @param <K>    the type of keys
     * @param <V>    the type of values
     * @param <R>    the type of the result elements
     * @return a list containing the mapped results
     */
    public static <K, V, R> List<R> mapValues(@NotNull Map<K, V> map, BiFunction<K, V, R> mapper) {
        return map.entrySet().stream()
                .map(e -> mapper.apply(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Replaces all occurrences of the given keys in the target string with their corresponding values.
     *
     * @param target       the original string to apply replacements on
     * @param replacements the map of keys and their replacement values
     * @return the resulting string after all replacements
     */
    public static @NotNull String replace(@NotNull String target, @NotNull Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            target = target.replace(entry.getKey(), entry.getValue());
        }
        return target;
    }

    /**
     * Replaces all occurrences of the keys in the specified map with their corresponding values within the target string.
     * Each key and value is converted to a string before performing the replacements.
     *
     * @param target       the original string on which the replacements are to be performed
     * @param replacements a map containing the keys to find and their corresponding values to replace them with
     * @param <T>          the type of keys in the map
     * @param <R>          the type of values in the map
     * @return a new string with all the replacements applied
     */
    public static <T, R> @NotNull String replaceAll(@NotNull String target, @NotNull Map<T, R> replacements) {
        for (Map.Entry<T, R> entry : replacements.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            target = target.replace(key, value);
        }
        return target;
    }

    /**
     * Checks whether the map contains a key that matches the given string (case-insensitive).
     *
     * @param map the map to search
     * @param key the key to match
     * @param <K> the type of keys
     * @param <V> the type of values
     * @return true if a matching key is found, false otherwise
     */
    public static <K, V> boolean containsKeyIgnoreCase(@NotNull Map<K, V> map, String key) {
        return map.keySet().stream().anyMatch(k -> k.toString().equalsIgnoreCase(key));
    }

    /**
     * Finds the first value in the map that satisfies the given predicate.
     *
     * @param map       the map to search
     * @param predicate the condition to apply to entries
     * @param <K>       the type of keys
     * @param <V>       the type of values
     * @return an Optional containing the found value, or empty if not found
     */
    public static <K, V> Optional<V> getFirstMatch(@NotNull Map<K, V> map, Predicate<Map.Entry<K, V>> predicate) {
        return map.entrySet().stream().filter(predicate).map(Map.Entry::getValue).findFirst();
    }

    /**
     * Returns a new map sorted by its keys using the provided comparator.
     *
     * @param map        the map to sort
     * @param comparator the comparator to sort the keys
     * @param <K>        the type of keys
     * @param <V>        the type of values
     * @return a sorted map based on keys
     */
    public static <K, V> Map<K, V> sortByKey(@NotNull Map<K, V> map, Comparator<K> comparator) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(comparator))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Returns a new map sorted by its values using the provided comparator.
     *
     * @param map        the map to sort
     * @param comparator the comparator to sort the values
     * @param <K>        the type of keys
     * @param <V>        the type of values
     * @return a sorted map based on values
     */
    public static <K, V> Map<K, V> sortByValue(@NotNull Map<K, V> map, Comparator<V> comparator) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(comparator))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
