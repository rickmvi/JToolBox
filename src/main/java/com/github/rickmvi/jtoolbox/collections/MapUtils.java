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

import java.util.function.BiPredicate;
import java.util.function.BiFunction;
import java.util.function.Predicate;
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
    public <K, V> Map<K, V> filterMap(@NotNull Map<K, V> map, BiPredicate<K, V> predicate) {
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
    public <K, V, R> List<R> mapValues(@NotNull Map<K, V> map, BiFunction<K, V, R> mapper) {
        return map.entrySet().stream()
                .map(e -> mapper.apply(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
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
    public <K, V> boolean containsKeyIgnoreCase(@NotNull Map<K, V> map, String key) {
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
    public <K, V> Optional<V> getFirstMatch(@NotNull Map<K, V> map, Predicate<Map.Entry<K, V>> predicate) {
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
    public <K, V> Map<K, V> sortByKey(@NotNull Map<K, V> map, Comparator<K> comparator) {
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
    public <K, V> Map<K, V> sortByValue(@NotNull Map<K, V> map, Comparator<V> comparator) {
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
