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

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.control.Switch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.rickmvi.jtoolbox.control.If.when;

/**
 * Dynamic is a versatile and dynamic collection that adapts its internal storage
 * between different data structures (ARRAY, LINKED, or SET) to optimize performance based
 * on usage patterns.
 * <p>
 * This collection supports configurable duplicate handling, allowing you to enable
 * or disable duplicates. It provides typical collection operations such as adding,
 * removing, merging, filtering, mapping, sorting, reversing, and retrieving elements.
 * The class also offers functional-style utilities for stream operations and iteration.
 * <p>
 * Dynamic can automatically optimize its internal storage after multiple
 * modifications, ensuring efficient memory usage and performance.
 *
 * <h2>Storage Modes</h2>
 * <ul>
 * <li>{@link StorageMode#ARRAY} - Uses an ArrayList internally (Fast random access).</li>
 * <li>{@link StorageMode#LINKED} - Uses a LinkedList internally (Fast insertions/removals).</li>
 * <li>{@link StorageMode#SET} - Uses a LinkedHashSet internally (Guarantees uniqueness).</li>
 * </ul>
 * Switching storage modes can be done dynamically via {@link #use(StorageMode)}.
 *
 * <h2>Duplicate Handling</h2>
 * <p>You can control whether duplicates are allowed using {@link #allowDuplicates(boolean)}.
 * If duplicates are not allowed, the collection automatically converts its storage
 * to a LinkedHashSet ({@code SET} mode) to maintain uniqueness. Indexed operations (like {@link #get(int)})
 * are not supported in {@code SET} mode.</p>
 *
 * <h2>Element Operations</h2>
 * <ul>
 * <li>{@link #add(Object)} / {@link #addAll(Object[])} - Add elements to the collection.</li>
 * <li>{@link #remove(Object)} / {@link #removeIf(Predicate)} - Remove elements based on value or condition.</li>
 * <li>{@link #merge(Dynamic)} - Merge another Dynamic into this one.</li>
 * <li>{@link #contains(Object)} - Checks if an element exists, using binary search if sorted.</li>
 * <li>{@link #getByValue(Object)} - Retrieves the first element matching a specific value (O(n)).</li>
 * <li>{@link #find(Predicate)}, {@link #findFirst()}, {@link #findAny()} - Retrieve elements based on conditions.</li>
 * </ul>
 *
 * <h2>Transformation and Filtering</h2>
 * <ul>
 * <li>{@link #map(Function)} - Transforms elements to another type.</li>
 * <li>{@link #filter(Predicate)} - Returns a new Dynamic containing only elements matching a predicate.</li>
 * <li>{@link #distinct()} - Removes duplicate elements.</li>
 * <li>{@link #distinctBy(Function)} - Removes duplicates based on a key extractor.</li>
 * <li>{@link #reverse()} - Reverses the order of elements.</li>
 * </ul>
 *
 * <h2>Sorting</h2>
 * <ul>
 * <li>{@link #sort(Comparator)} - Sorts elements using a custom comparator.</li>
 * <li>{@link #sortIfComparable()} - Sorts elements if they implement Comparable.</li>
 * </ul>
 *
 * <h2>Conversion and Access</h2>
 * <ul>
 * <li>{@link #get(int)} - Retrieves an element by index (not supported in SET mode).</li>
 * <li>{@link #set(int, Object)} - Replaces an element by index (not supported in SET mode).</li>
 * <li>{@link #toList()} - Returns a List of elements.</li>
 * <li>{@link #toSet()} - Returns a Set of elements.</li>
 * <li>{@link #toArray(IntFunction)} - Returns an array of elements.</li>
 * <li>{@link #stream()} - Returns a Stream of elements.</li>
 * <li>{@link #size()} / {@link #isEmpty()} - Retrieves collection size and emptiness.</li>
 * </ul>
 *
 * <h2>Iteration and Functional Utilities</h2>
 * <ul>
 * <li>{@link #forEachDo(Consumer)} - Executes an action for each element in the collection.</li>
 * <li>{@link #iterator()} - Provides an iterator over the elements.</li>
 * </ul>
 *
 * <h2>Auto-Optimization</h2>
 * <p>The collection tracks modifications and automatically optimizes its storage
 * mode when certain thresholds are exceeded to maintain performance efficiency.</p>
 *
 * @param <T> The type of elements stored in this SmartCollection.
 * @author Rick M. Viana
 * @version 1.2
 * @since 2025
 *
 * @see com.github.rickmvi.jtoolbox.control.If
 */

@SuppressWarnings({"unused", "unchecked"})
@Getter(value = AccessLevel.PRIVATE)
@Setter(value = AccessLevel.PRIVATE)
public class Dynamic<T> implements Iterable<T> {

    public enum StorageMode { ARRAY, LINKED, SET }

    private Collection<T> elements;
    private boolean allowDuplicates;
    private boolean sorted = false;
    private int modificationCount = 0;

    @Getter
    private StorageMode mode;

    private Dynamic(Collection<T> initial, boolean allowDuplicates, StorageMode mode) {
        this.allowDuplicates = allowDuplicates;
        this.mode = mode;
        this.elements = createStorage(mode);
        this.elements.addAll(initial);
        if (!allowDuplicates) {
            applyDuplicateRule();
        }
    }

    @SafeVarargs
    public static <T> @NotNull Dynamic<T> of(T... items) {
        return new Dynamic<>(Arrays.asList(items), true, StorageMode.LINKED);
    }

    public static <T> @NotNull Dynamic<T> empty() {
        return new Dynamic<>(Collections.emptyList(), true, StorageMode.ARRAY);
    }

    private @NotNull Collection<T> createStorage(@NotNull StorageMode mode) {
        return switch (mode) {
            case LINKED -> new LinkedList<>();
            case ARRAY -> new ArrayList<>();
            case SET -> new LinkedHashSet<>();
        };
    }

    /** Helper to retrieve the internal storage as a List, throwing an exception if in SET mode. */
    private List<T> requireList() {
        if (elements instanceof List) return (List<T>) elements;
        throw new UnsupportedOperationException("Indexed operations are not supported in SET storage mode (duplicates disallowed).");
    }

    /**
     * Changes the internal storage mode, transferring all elements to the new structure.
     *
     * @param mode The new storage mode to use.
     * @return This AdaptiveArray instance.
     */
    public Dynamic<T> use(StorageMode mode) {
        if (this.mode != StorageMode.SET && this.mode != mode) {
            Collection<T> newStorage = createStorage(mode);
            newStorage.addAll(elements);
            elements = newStorage;
            this.mode = mode;
        }
        return this;
    }

    /**
     * Configures whether duplicate elements are allowed.
     *
     * @param allow true to allow duplicates, false to enforce uniqueness (switches to SET mode).
     * @return This AdaptiveArray instance.
     */
    public Dynamic<T> allowDuplicates(boolean allow) {
        this.allowDuplicates = allow;
        applyDuplicateRule();
        return this;
    }

    private void applyDuplicateRule() {
        if (!allowDuplicates && this.mode != StorageMode.SET) {
            this.mode = StorageMode.SET;
            elements = new LinkedHashSet<>(elements);
            return;
        }

        if (allowDuplicates && this.mode == StorageMode.SET) {
            this.mode = StorageMode.ARRAY;
            elements = new ArrayList<>(elements);
        }
    }

    /** Adds an element respecting the allowDuplicates rule. */
    public Dynamic<T> add(T element) {
        when(allowDuplicates)
                .or(!elements.contains(element))
                .apply(() -> {
                    elements.add(element);
                    sorted = false;
                    modificationCount++;
                    autoOptimize();
                }).run();
        return this;
    }

    @SafeVarargs
    public final Dynamic<T> addAll(T @NotNull ... items) {
        for (T item : items) add(item);
        return this;
    }

    /**
     * Removes the first occurrence of the specified element from this collection, if it is present.
     * <p>
     * If the collection does not contain the element, it remains unchanged and the operation succeeds silently.
     * This method respects the underlying storage mode's behavior regarding element removal.
     *
     * @param element the element to be removed from this collection.
     * @return This {@code Dynamic} instance for method chaining.
     */
    public Dynamic<T> remove(T element) {
        if (elements.remove(element)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Removes elements from the collection that satisfy the given predicate.
     * <p>
     * If any elements are removed, the internal state is updated, and the
     * storage may be optimized automatically based on modification count
     * and size heuristics.
     *
     * @param filter A predicate to test each element. Only elements that return
     *               {@code true} for this predicate will be removed.
     * @return This {@code Dynamic} instance for method chaining.
     * @throws NullPointerException if the provided filter is {@code null}.
     */
    public Dynamic<T> removeIf(Predicate<T> filter) {
        if (elements.removeIf(filter)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Retrieves the element at the specified position in this list.
     * <p>
     * This operation is typically O(1) in ARRAY mode and O(n) in LINKED mode.
     *
     * @param index The index of the element to return.
     * @return The element at the specified index.
     * @throws UnsupportedOperationException if in SET mode.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public T get(int index) {
        assertIndexInRange(index);
        return requireList().get(index);
    }

    /**
     * Retrieves the first occurrence of the specified element by value, wrapped in an Optional.
     * <p>
     * The search is performed by iterating through the collection and comparing elements
     * using the {@code equals} method. This operation is O(n) in complexity.
     * If the element is not found, an empty Optional is returned.
     *
     * @param element The element whose first occurrence should be found.
     * @return An Optional containing the first matching element, or an empty Optional if not found.
     */
    public Optional<T> getByValue(@NotNull T element) {
        return elements.stream()
                .filter(e -> Objects.equals(e, element))
                .findFirst();
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @param index The index of the element to replace.
     * @param element The element to be stored at the specified position.
     * @return The element previously at the specified position.
     * @throws UnsupportedOperationException if in SET mode.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public T set(int index, T element) {
        assertIndexInRange(index);
        T oldValue = requireList().set(index, element);
        modificationCount++;
        return oldValue;
    }

    /**
     * Removes the element at the specified position in this list.
     *
     * @param index The index of the element to be removed.
     * @return The element previously at the specified position.
     * @throws UnsupportedOperationException if in SET mode.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public T remove(int index) {
        assertIndexInRange(index);
        T removed = requireList().remove(index);
        modificationCount++;
        autoOptimize();
        return removed;
    }

    private void assertIndexInRange(int index) {
        If.ThrowWhen(IndexOutOfBounds(index), () -> new IndexOutOfBoundsException("Index: " + index + ", Size: " + size()));
    }

    private boolean IndexOutOfBounds(int index) {
        return index < 0 || index >= size();
    }

    public Dynamic<T> merge(@NotNull Dynamic<T> other) {
        elements.addAll(other.elements);
        if (!allowDuplicates) {
            applyDuplicateRule();
        }
        return this;
    }

    public Dynamic<T> sort(Comparator<T> comparator) {
        List<T> sortedList = getElementsAsList().get();
        sortedList.sort(comparator);
        elements.clear();
        elements.addAll(sortedList);
        sorted = true;
        return this;
    }

    public Dynamic<T> sortIfComparable() {
        if (!elements.isEmpty() && elements.iterator().next() instanceof Comparable) {
            List<T> sortedList = getElementsAsList().get();
            Collections.sort((List<? extends Comparable>) sortedList);
            elements.clear();
            elements.addAll(sortedList);
            sorted = true;
        }
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean contains(T element) {
        if (sorted && element instanceof Comparable rawElement && elements instanceof List rawList) {
            return Collections.binarySearch(rawList, rawElement) >= 0;
        }

        return elements.contains(element);
    }

    public Optional<T> find(Predicate<T> condition) {
        return elements.stream().filter(condition).findFirst();
    }

    public Optional<T> findFirst() {
        return elements.stream().findFirst();
    }

    public Optional<T> findAny() {
        return elements.stream().findAny();
    }

    public <R> Dynamic<R> map(Function<T, R> mapper) {
        return new Dynamic<>(
                elements.stream().map(mapper).collect(Collectors.toList()),
                allowDuplicates,
                mode
        );
    }

    public Dynamic<T> filter(Predicate<T> filter) {
        return new Dynamic<>(
                elements.stream().filter(filter).collect(Collectors.toList()),
                allowDuplicates,
                mode
        );
    }

    public Dynamic<T> distinct() {
        return new Dynamic<>(new LinkedHashSet<>(elements), false, StorageMode.SET);
    }

    public <K> Dynamic<T> distinctBy(Function<T, K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        return new Dynamic<>(
                elements.stream()
                        .filter(e -> seen.add(keyExtractor.apply(e)))
                        .collect(Collectors.toList()),
                false,
                mode
        );
    }

    public Dynamic<T> reverse() {
        if (!(elements instanceof List)) {
            elements = getElementsAsList().get();
        }
        Collections.reverse((List<T>) elements);
        return this;
    }

    private @NotNull Supplier<ArrayList<T>> getElementsAsList() {
        return () -> new ArrayList<>(elements);
    }

    public Dynamic<T> copy() {
        return new Dynamic<>(getElementsAsList().get(), allowDuplicates, mode);
    }

    public List<T> toList() { return getElementsAsList().get(); }

    public Set<T> toSet() { return new LinkedHashSet<>(elements); }

    public Stream<T> stream() { return elements.stream(); }

    public T[] toArray(IntFunction<T[]> generator) { return elements.toArray(generator); }

    public int size() { return elements.size(); }

    public boolean isEmpty() { return elements.isEmpty(); }

    public Dynamic<T> forEachDo(Consumer<T> action) {
        elements.forEach(action);
        return this;
    }

    public String join(CharSequence separator) {
        return elements.stream().map(Object::toString).collect(Collectors.joining(separator));
    }

    @Override
    public @NotNull Iterator<T> iterator() { return elements.iterator(); }

    @Override
    public String toString() {
        return String.format("elements=%s", Arrays.deepToString(elements.toArray()));
    }

    /** Automatically changes storage mode based on modification count and size heuristics. */
    private void autoOptimize() {
        final int OPT_THRESHOLD = 100;
        final int SIZE_LARGE = 5000;
        final int SIZE_SMALL = 100;

        if (modificationCount > OPT_THRESHOLD && allowDuplicates) {
            Switch.on(mode)
                    .caseCondition(m -> m == StorageMode.LINKED && elements.size() > SIZE_LARGE,
                            m -> use(StorageMode.ARRAY))
                    .caseCondition(m -> m == StorageMode.ARRAY && elements.size() < SIZE_SMALL,
                            m -> use(StorageMode.LINKED))
                    .get();
            modificationCount = 0;
        }
    }
}