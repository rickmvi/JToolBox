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

import com.github.rickmvi.jtoolbox.control.Switch;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.rickmvi.jtoolbox.control.If.when;

/**
 * A generic and dynamic collection that adapts its internal storage between different
 * data structures to provide optimized performance based on usage patterns.
 * <p>
 * The {@code AdaptiveArray} allows for configurable duplicate handling, supports sorting,
 * filtering, mapping, and other operations typical of a collection. It also provides
 * flexibility to switch between ARRAY and LINKED storage modes.
 *
 * @param <T> The type of elements stored in this AdaptiveArray.
 */
@ToString(of = { "elements", "allowDuplicates", "mode"})
public class AdaptiveArray<T> implements Iterable<T> {

    public enum StorageMode { ARRAY, LINKED }

    private Collection<T> elements;
    private boolean allowDuplicates;
    private boolean sorted = false;
    private int modificationCount = 0;

    @Getter
    private StorageMode mode;

    private AdaptiveArray(Collection<T> initial, boolean allowDuplicates, StorageMode mode) {
        this.allowDuplicates = allowDuplicates;
        this.mode = mode;
        this.elements = createStorage(mode);
        this.elements.addAll(initial);
        applyDuplicateRule();
    }

    @SafeVarargs
    public static <T> @NotNull AdaptiveArray<T> of(T... items) {
        return new AdaptiveArray<>(Arrays.asList(items), true, StorageMode.ARRAY);
    }

    public static <T> @NotNull AdaptiveArray<T> empty() {
        return new AdaptiveArray<>(Collections.emptyList(), true, StorageMode.ARRAY);
    }

    private @NotNull Collection<T> createStorage(@NotNull StorageMode mode) {
        return mode == StorageMode.LINKED ? new LinkedList<>() : new ArrayList<>();
    }

    public AdaptiveArray<T> use(StorageMode mode) {
        if (this.mode != mode) {
            Collection<T> newStorage = createStorage(mode);
            newStorage.addAll(elements);
            elements = newStorage;
            this.mode = mode;
        }
        return this;
    }

    public AdaptiveArray<T> allowDuplicates(boolean allow) {
        this.allowDuplicates = allow;
        applyDuplicateRule();
        return this;
    }

    private void applyDuplicateRule() {
        if (!allowDuplicates) {
            elements = new LinkedHashSet<>(elements);
        }
    }

    public AdaptiveArray<T> add(T element) {
        if (allowDuplicates || !elements.contains(element)) {
            elements.add(element);
            sorted = false;
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    @SafeVarargs
    public final AdaptiveArray<T> addAll(T @NotNull ... items) {
        for (T item : items) add(item);
        return this;
    }

    public AdaptiveArray<T> remove(T element) {
        elements.remove(element);
        modificationCount++;
        autoOptimize();
        return this;
    }

    public AdaptiveArray<T> removeIf(Predicate<T> filter) {
        elements.removeIf(filter);
        modificationCount++;
        autoOptimize();
        return this;
    }

    @SuppressWarnings("unchecked")
    public AdaptiveArray<T> merge(@NotNull AdaptiveArray<T> other) {
        addAll((T[]) other.elements.toArray());
        return this;
    }

    public AdaptiveArray<T> sort(Comparator<T> comparator) {
        List<T> sortedList = new ArrayList<>(elements);
        sortedList.sort(comparator);
        elements.clear();
        elements.addAll(sortedList);
        sorted = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public AdaptiveArray<T> sortIfComparable() {
        if (!elements.isEmpty() && elements.iterator().next() instanceof Comparable) {
            List<T> sortedList = new ArrayList<>(elements);
            Collections.sort((List<? extends Comparable>) sortedList);
            elements.clear();
            elements.addAll(sortedList);
            sorted = true;
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(T element) {
        if (sorted && element instanceof Comparable && elements instanceof List) {
            List<? extends Comparable<? super T>> list = (List<? extends Comparable<? super T>>) elements;
            return Collections.binarySearch(list, element) >= 0;
        }
        return elements.contains(element);
    }

    public Optional<T> find(Predicate<T> condition) {
        for (T e : elements)
            if (condition.test(e)) return Optional.of(e);
        return Optional.empty();
    }

    public Optional<T> findFirst() {
        return elements.stream().findFirst();
    }

    public Optional<T> findAny() {
        return elements.stream().findAny();
    }

    public <R> AdaptiveArray<R> map(Function<T, R> mapper) {
        return new AdaptiveArray<>(
                elements.stream().map(mapper).collect(Collectors.toList()),
                allowDuplicates,
                mode
        );
    }

    public AdaptiveArray<T> filter(Predicate<T> filter) {
        return new AdaptiveArray<>(
                elements.stream().filter(filter).collect(Collectors.toList()),
                allowDuplicates,
                mode
        );
    }

    public AdaptiveArray<T> distinct() {
        return new AdaptiveArray<>(new LinkedHashSet<>(elements), false, mode);
    }

    public <K> AdaptiveArray<T> distinctBy(Function<T, K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        return new AdaptiveArray<>(
                elements.stream()
                        .filter(e -> seen.add(keyExtractor.apply(e)))
                        .collect(Collectors.toList()),
                false,
                mode
        );
    }

    public AdaptiveArray<T> reverse() {
        if (elements instanceof List) {
            Collections.reverse((List<T>) elements);
            return this;
        }

        elements = new ArrayList<>(elements);
        Collections.reverse((List<T>) elements);
        return this;
    }

    public AdaptiveArray<T> copy() {
        return new AdaptiveArray<>(new ArrayList<>(elements), allowDuplicates, mode);
    }

    public List<T> toList() { return new ArrayList<>(elements); }

    public Set<T> toSet() { return new LinkedHashSet<>(elements); }

    public Stream<T> stream() { return elements.stream(); }

    public T[] toArray(IntFunction<T[]> generator) { return elements.toArray(generator); }

    public int size() { return elements.size(); }

    public boolean isEmpty() { return elements.isEmpty(); }

    public AdaptiveArray<T> forEachDo(Consumer<T> action) {
        elements.forEach(action);
        return this;
    }

    @Override
    public @NotNull Iterator<T> iterator() { return elements.iterator(); }

    private void autoOptimize() {
       when(modificationCount > 100)
                .apply(() -> {
                    Switch.on(mode)
                            .caseCondition(storage  ->
                                    mode == StorageMode.LINKED && elements.size() > 1000,
                                    modes -> use(StorageMode.LINKED))
                            .caseCondition(storage ->
                                    mode == StorageMode.ARRAY && elements.size() < 500,
                                    modes -> use(StorageMode.ARRAY))
                            .get();
                    modificationCount = 0;
                }).run();
    }
}
