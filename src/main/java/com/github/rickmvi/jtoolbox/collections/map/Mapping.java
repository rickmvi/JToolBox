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
package com.github.rickmvi.jtoolbox.collections.map;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
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
@UtilityClass
public class Mapping {

    /* ========================== Case Handling ========================== */

    public static <T> void on(T value, @NotNull Map<T, Runnable> cases, Runnable defaultAction) {
        cases.getOrDefault(value, defaultAction).run();
    }

    public static <T, R> R returning(T value, @NotNull Map<T, Supplier<R>> cases, Supplier<R> defaultAction) {
        return cases.getOrDefault(value, defaultAction).get();
    }

    @Contract("_, _, _ -> new")
    public static <T> @NotNull CompletableFuture<T> returnAsync(
            T value,
            Map<T, Supplier<T>> cases,
            Supplier<T> defaultCase
    ) {
        return CompletableFuture.supplyAsync(() -> returning(value, cases, defaultCase));
    }

    /* ========================== Iteration ========================== */

    public static <K, V> void forEach(@NotNull Map<K, V> map, BiConsumer<K, V> action) {
        map.forEach(action);
    }

    public static <K, V> void forEachEntry(@NotNull Map<K, V> map, Consumer<Map.Entry<K, V>> action) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            action.accept(entry);
        }
    }

    /* ========================== Retrieval ========================== */

    public static <K, V> V getOrDefault(@NotNull Map<K, V> map, K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    public static <K, V> @NotNull Optional<V> getFirstMatch(
            @NotNull Map<K, V> map,
            Predicate<Map.Entry<K, V>> predicate
    ) {
        return map.entrySet().stream()
                .filter(predicate)
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public static <K, V> boolean containsKeyIgnoreCase(@NotNull Map<K, V> map, String key) {
        return map.keySet().stream().anyMatch(k -> k.toString().equalsIgnoreCase(key));
    }

    /* ========================== Transformations ========================== */

    public static <K, V> Map<K, V> filterMap(@NotNull Map<K, V> map, BiPredicate<K, V> predicate) {
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getKey(), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V, R> List<R> mapValues(@NotNull Map<K, V> map, BiFunction<K, V, R> mapper) {
        return map.entrySet().stream()
                .map(e -> mapper.apply(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /* ========================== String Replacements ========================== */

    public static @NotNull String applyReplacements(
            @NotNull String target,
            @NotNull Map<String, Object> replacements
    ) {
        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            target = target.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return target;
    }

    public static <K, V> @NotNull String replaceAll(
            @NotNull String target,
            @NotNull Map<K, V> replacements
    ) {
        for (Map.Entry<K, V> entry : replacements.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            target = target.replace(key, value);
        }
        return target;
    }

    /* ========================== Sorting ========================== */

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