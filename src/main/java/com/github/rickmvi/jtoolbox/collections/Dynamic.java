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
import com.github.rickmvi.jtoolbox.text.Stringifier;
import com.github.rickmvi.jtoolbox.util.Try;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2>Dynamic - Adaptive High-Performance Collection</h2>
 *
 * An intelligent, self-optimizing collection that dynamically adapts its internal storage
 * based on usage patterns to maximize performance. Provides a rich, fluent API combining
 * the best features of List, Set, and functional programming.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Automatic storage optimization (ArrayList, LinkedList, LinkedHashSet)</li>
 *   <li>Configurable duplicate handling with type-safe operations</li>
 *   <li>Functional transformations (map, flatMap, filter, reduce)</li>
 *   <li>Advanced querying (find, search, partition, group)</li>
 *   <li>Performance-optimized operations (binary search when sorted)</li>
 *   <li>Seamless integration with Java Collections Framework</li>
 *   <li>Builder pattern for fluent construction</li>
 *   <li>Thread-safe variant support</li>
 * </ul>
 *
 * <h3>Storage Modes:</h3>
 * <ul>
 *   <li>{@link StorageMode#ARRAY} - ArrayList (fast random access, iteration)</li>
 *   <li>{@link StorageMode#LINKED} - LinkedList (fast insertions/deletions)</li>
 *   <li>{@link StorageMode#SET} - LinkedHashSet (guaranteed uniqueness, insertion order)</li>
 *   <li>{@link StorageMode#AUTO} - Automatically optimizes based on usage</li>
 * </ul>
 *
 * <h3>Example 1: Construction and Basic Operations</h3>
 * <pre>{@code
 * // Simple construction
 * Dynamic<String> names = Dynamic.of("Alice", "Bob", "Charlie");
 *
 * // From existing collection
 * Dynamic<Integer> numbers = Dynamic.of(List.of(1, 2, 3, 4, 5));
 *
 * // Using builder
 * Dynamic<String> unique = Dynamic.<String>builder()
 *     .withoutDuplicates()
 *     .withInitialCapacity(100)
 *     .build()
 *     .addAll("A", "B", "C", "A"); // Only A, B, C stored
 * }</pre>
 *
 * <h3>Example 2: Functional Operations</h3>
 * <pre>{@code
 * Dynamic<Integer> numbers = Dynamic.of(1, 2, 3, 4, 5, 6);
 *
 * Dynamic<Integer> result = numbers
 *     .filter(n -> n % 2 == 0)  // [2, 4, 6]
 *     .map(n -> n * 2)           // [4, 8, 12]
 *     .sorted()                  // Maintains order
 *     .takeFirst(2);             // [4, 8]
 *
 * int sum = numbers.reduce(0, Integer::sum); // 21
 * }</pre>
 *
 * <h3>Example 3: Advanced Queries</h3>
 * <pre>{@code
 * Dynamic<Person> people = Dynamic.of(personList);
 *
 * // Partition by age
 * var (adults, minors) = people.partition(p -> p.getAge() >= 18);
 *
 * // Group by city
 * Map<String, List<Person>> byCity = people.groupBy(Person::getCity);
 *
 * // Find with fallback
 * Person admin = people
 *     .findFirst(p -> p.hasRole("ADMIN"))
 *     .orElseThrow(() -> new IllegalStateException("No admin"));
 * }</pre>
 *
 * <h3>Example 4: Performance Optimization</h3>
 * <pre>{@code
 * Dynamic<String> data = Dynamic.<String>builder()
 *     .withStorageMode(StorageMode.AUTO)  // Auto-optimize
 *     .build();
 *
 * // Frequent insertions at beginning - auto-switches to LINKED
 * for (int i = 0; i < 1000; i++) {
 *     data.addFirst("item-" + i);
 * }
 *
 * // Frequent random access - auto-switches to ARRAY
 * for (int i = 0; i < 1000; i++) {
 *     String value = data.get(i);
 * }
 * }</pre>
 *
 * @param <T> the type of elements in this collection
 * @author Rick M. Viana
 * @version 2.0
 * @since 2025
 */
@SuppressWarnings({"unchecked", "unused"})
public final class Dynamic<T> implements Iterable<T> {

    /** Storage mode strategies */
    public enum StorageMode {
        /** ArrayList - O(1) random access, O(1) amortized append */
        ARRAY,
        /** LinkedList - O(1) head/tail operations, O(n) random access */
        LINKED,
        /** LinkedHashSet - O(1) contains, guaranteed uniqueness */
        SET,
        /** Automatically optimizes based on usage patterns */
        AUTO
    }

    @Getter(AccessLevel.PRIVATE)
    private Collection<T> storage;

    @Getter
    private StorageMode mode;

    private boolean allowDuplicates;
    private boolean sorted;
    private Comparator<T> lastComparator;

    // Performance tracking
    private int modificationCount;
    private int randomAccessCount;
    private int sequentialAccessCount;
    private int headOperationCount;

    // Configuration
    private static final int OPTIMIZATION_THRESHOLD = 100;
    private static final int LARGE_COLLECTION_SIZE  = 5000;
    private static final int SMALL_COLLECTION_SIZE  = 100;

    // ==================== CONSTRUCTORS ====================

    private Dynamic(Collection<T> initial, StorageMode mode, boolean allowDuplicates) {
        this.mode = mode;
        this.allowDuplicates = allowDuplicates;
        this.storage = createStorage(mode);
        if (initial != null && !initial.isEmpty()) {
            this.storage.addAll(initial);
            if (!allowDuplicates) {
                ensureUniqueness();
            }
        }
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Creates an empty Dynamic collection with default settings (ArrayList, duplicates allowed).
     */
    public static <T> @NotNull Dynamic<T> empty() {
        return new Dynamic<>(null, StorageMode.ARRAY, true);
    }

    /**
     * Creates a Dynamic collection from varargs elements.
     */
    @SafeVarargs
    public static <T> @NotNull Dynamic<T> of(T... elements) {
        return new Dynamic<>(Arrays.asList(elements), StorageMode.ARRAY, true);
    }

    /**
     * Creates a Dynamic collection from an existing collection.
     * Automatically detects optimal storage mode based on collection type.
     */
    public static <T> @NotNull Dynamic<T> of(@NotNull Collection<? extends T> collection) {
        Objects.requireNonNull(collection, "Collection cannot be null");

        StorageMode detectedMode;
        boolean duplicates;

        if (collection instanceof Set) {
            detectedMode = StorageMode.SET;
            duplicates = false;
        } else if (collection instanceof LinkedList) {
            detectedMode = StorageMode.LINKED;
            duplicates = true;
        } else {
            detectedMode = StorageMode.ARRAY;
            duplicates = true;
        }

        return new Dynamic<>((Collection<T>) collection, detectedMode, duplicates);
    }

    /**
     * Creates a Dynamic collection from a stream.
     */
    public static <T> @NotNull Dynamic<T> of(@NotNull Stream<T> stream) {
        return new Dynamic<>(stream.collect(Collectors.toList()), StorageMode.ARRAY, true);
    }

    /**
     * Creates a Dynamic collection with specified capacity (ARRAY mode).
     */
    public static <T> @NotNull Dynamic<T> withCapacity(int capacity) {
        Dynamic<T> dynamic = new Dynamic<>(null, StorageMode.ARRAY, true);
        dynamic.storage = new ArrayList<>(capacity);
        return dynamic;
    }

    /**
     * Creates a Dynamic collection without duplicates (SET mode).
     */
    @SafeVarargs
    public static <T> @NotNull Dynamic<T> unique(T... elements) {
        return new Dynamic<>(Arrays.asList(elements), StorageMode.SET, false);
    }

    /**
     * Creates a Dynamic collection from a range of integers.
     */
    public static @NotNull Dynamic<Integer> range(int start, int end) {
        return range(start, end, 1);
    }

    /**
     * Creates a Dynamic collection from a range with step.
     */
    public static @NotNull Dynamic<Integer> range(int start, int end, int step) {
        if (step == 0) throw new IllegalArgumentException("Step cannot be zero");

        Dynamic<Integer> result = empty();
        if (step > 0) {
            for (int i = start; i < end; i += step) {
                result.add(i);
            }
        } else {
            for (int i = start; i > end; i += step) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Creates a Dynamic collection by generating values.
     */
    public static <T> @NotNull Dynamic<T> generate(int count, @NotNull Supplier<T> generator) {
        Dynamic<T> result = withCapacity(count);
        for (int i = 0; i < count; i++) {
            result.add(generator.get());
        }
        return result;
    }

    /**
     * Creates a Dynamic collection by generating values with index.
     */
    public static <T> @NotNull Dynamic<T> generate(int count, @NotNull IntFunction<T> generator) {
        Dynamic<T> result = withCapacity(count);
        for (int i = 0; i < count; i++) {
            result.add(generator.apply(i));
        }
        return result;
    }

    /**
     * Returns a builder for fluent construction.
     */
    public static <T> @NotNull Builder<T> builder() {
        return new Builder<>();
    }

    // ==================== BUILDER ====================

    public static final class Builder<T> {
        private StorageMode mode = StorageMode.ARRAY;
        private boolean allowDuplicates = true;
        private Collection<T> initial = new ArrayList<>();
        private Integer initialCapacity;

        private Builder() {}

        public Builder<T> withStorageMode(@NotNull StorageMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder<T> withDuplicates() {
            this.allowDuplicates = true;
            return this;
        }

        public Builder<T> withoutDuplicates() {
            this.allowDuplicates = false;
            this.mode = StorageMode.SET;
            return this;
        }

        public Builder<T> withInitialCapacity(int capacity) {
            this.initialCapacity = capacity;
            return this;
        }

        @SafeVarargs
        public final Builder<T> withElements(T... elements) {
            Collections.addAll(initial, elements);
            return this;
        }

        public Builder<T> withElements(@NotNull Collection<? extends T> elements) {
            this.initial.addAll(elements);
            return this;
        }

        public @NotNull Dynamic<T> build() {
            Dynamic<T> dynamic = new Dynamic<>(initial, mode, allowDuplicates);
            if (initialCapacity != null && dynamic.storage instanceof ArrayList) {
                ((ArrayList<T>) dynamic.storage).ensureCapacity(initialCapacity);
            }
            return dynamic;
        }
    }

    // ==================== STORAGE MANAGEMENT ====================

    private @NotNull Collection<T> createStorage(@NotNull StorageMode mode) {
        return switch (mode) {
            case ARRAY, AUTO -> new ArrayList<>();
            case LINKED -> new LinkedList<>();
            case SET -> new LinkedHashSet<>();
        };
    }

    /**
     * Switches to a specific storage mode, preserving all elements.
     */
    public @NotNull Dynamic<T> switchTo(@NotNull StorageMode mode) {
        if (this.mode == mode) {
            return this;
        }

        If.throwIf(this.mode == StorageMode.SET && mode != StorageMode.SET && !allowDuplicates, () ->
                new IllegalStateException("Cannot switch from SET mode when duplicates are not allowed"));

        Collection<T> newStorage = createStorage(mode);
        newStorage.addAll(storage);
        this.storage = newStorage;
        this.mode = mode;
        resetMetrics();

        return this;
    }

    /**
     * Enables/disables duplicate elements.
     */
    public @NotNull Dynamic<T> allowDuplicates(boolean allow) {
        if (this.allowDuplicates == allow) {
            return this;
        }

        this.allowDuplicates = allow;

        if (!allow) {
            ensureUniqueness();
        } else if (mode == StorageMode.SET) {
            switchTo(StorageMode.ARRAY);
        }

        return this;
    }

    private void ensureUniqueness() {
        if (mode != StorageMode.SET) {
            Collection<T> newStorage = new LinkedHashSet<>(storage);
            this.storage = newStorage;
            this.mode = StorageMode.SET;
        }
    }

    private void autoOptimize() {
        if (mode != StorageMode.AUTO || modificationCount < OPTIMIZATION_THRESHOLD) {
            return;
        }

        int size = size();

        if (randomAccessCount > sequentialAccessCount * 2 && size > SMALL_COLLECTION_SIZE) {
            if (!(storage instanceof ArrayList)) {
                switchTo(StorageMode.ARRAY);
            }
        } else if (headOperationCount > randomAccessCount * 2) {
            if (!(storage instanceof LinkedList)) {
                switchTo(StorageMode.LINKED);
            }
        } else if (size < SMALL_COLLECTION_SIZE && storage instanceof ArrayList) {
            switchTo(StorageMode.LINKED);
        } else if (size > LARGE_COLLECTION_SIZE && storage instanceof LinkedList) {
            switchTo(StorageMode.ARRAY);
        }

        resetMetrics();
    }

    private void resetMetrics() {
        modificationCount = 0;
        randomAccessCount = 0;
        sequentialAccessCount = 0;
        headOperationCount = 0;
    }

    // ==================== ELEMENT ADDITION ====================

    /**
     * Adds an element to the collection.
     */
    public @NotNull Dynamic<T> add(@Nullable T element) {
        if (allowDuplicates || !storage.contains(element)) {
            storage.add(element);
            sorted = false;
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Adds multiple elements.
     */
    @SafeVarargs
    public final @NotNull Dynamic<T> addAll(T... elements) {
        for (T element : elements) {
            add(element);
        }
        return this;
    }

    /**
     * Adds all elements from a collection.
     */
    public @NotNull Dynamic<T> addAll(@NotNull Collection<? extends T> elements) {
        if (allowDuplicates) {
            storage.addAll(elements);
            sorted = false;
            modificationCount += elements.size();
            autoOptimize();
        } else {
            elements.forEach(this::add);
        }
        return this;
    }

    /**
     * Adds element at the beginning (O(1) for LINKED, O(n) for ARRAY).
     */
    public @NotNull Dynamic<T> addFirst(@Nullable T element) {
        If.when(storage instanceof List)
                .then(() -> ((List<T>) storage).addFirst(element))
                .otherwise(() -> {
                    Collection<T> newStorage = createStorage(mode);
                    newStorage.add(element);
                    newStorage.addAll(storage);
                    storage = newStorage;
                });

        sorted = false;
        headOperationCount++;
        modificationCount++;
        autoOptimize();
        return this;
    }

    /**
     * Adds element at the end (same as add, but explicit).
     */
    public @NotNull Dynamic<T> addLast(@Nullable T element) {
        return add(element);
    }

    /**
     * Adds element at specific index (not available in SET mode).
     */
    public @NotNull Dynamic<T> addAt(int index, @Nullable T element) {
        requireListMode("addAt");
        ((List<T>) storage).add(index, element);
        sorted = false;
        modificationCount++;
        autoOptimize();
        return this;
    }

    /**
     * Adds element only if condition is true.
     */
    public @NotNull Dynamic<T> addIf(boolean condition, @Nullable T element) {
        if (condition) {
            add(element);
        }
        return this;
    }

    /**
     * Adds element only if it's not null.
     */
    public @NotNull Dynamic<T> addIfNotNull(@Nullable T element) {
        if (element != null) {
            add(element);
        }
        return this;
    }

    // ==================== ELEMENT REMOVAL ====================

    /**
     * Removes the first occurrence of the element.
     */
    public @NotNull Dynamic<T> remove(@Nullable T element) {
        if (storage.remove(element)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Removes all elements matching the predicate.
     */
    public @NotNull Dynamic<T> removeIf(@NotNull Predicate<? super T> predicate) {
        if (storage.removeIf(predicate)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Removes element at index.
     */
    public T removeAt(int index) {
        requireListMode("removeAt");
        T removed = ((List<T>) storage).remove(index);
        modificationCount++;
        autoOptimize();
        return removed;
    }

    /**
     * Removes and returns the first element.
     */
    public @NotNull Optional<T> removeFirst() {
        if (isEmpty()) {
            return Optional.empty();
        }

        if (storage instanceof LinkedList) {
            headOperationCount++;
            return Optional.of(((LinkedList<T>) storage).removeFirst());
        }

        if (!(storage instanceof List)) {
            Iterator<T> it = storage.iterator();
            T first = it.next();
            it.remove();
            return Optional.of(first);
        }

        return Optional.of(removeAt(0));
    }

    /**
     * Removes and returns the last element.
     */
    public @NotNull Optional<T> removeLast() {
        if (isEmpty()) {
            return Optional.empty();
        }

        if (storage instanceof LinkedList) {
            return Optional.of(((LinkedList<T>) storage).removeLast());
        }

        if (!(storage instanceof List<T> list)) {
            T last = null;
            for (T t : storage) {
                last = t;
            }
            storage.remove(last);
            return Optional.ofNullable(last);
        }

        return Optional.of(removeAt(list.size() - 1));
    }

    /**
     * Removes all elements.
     */
    public @NotNull Dynamic<T> clear() {
        storage.clear();
        sorted = false;
        modificationCount = 0;
        return this;
    }

    /**
     * Removes all elements present in the given collection.
     */
    public @NotNull Dynamic<T> removeAll(@NotNull Collection<? extends T> elements) {
        if (storage.removeAll(elements)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    /**
     * Retains only elements present in the given collection.
     */
    public @NotNull Dynamic<T> retainAll(@NotNull Collection<? extends T> elements) {
        if (storage.retainAll(elements)) {
            modificationCount++;
            autoOptimize();
        }
        return this;
    }

    // ==================== ELEMENT ACCESS ====================

    /**
     * Gets element at index (not available in SET mode).
     */
    public T get(int index) {
        requireListMode("get");
        randomAccessCount++;
        autoOptimize();
        return ((List<T>) storage).get(index);
    }

    /**
     * Gets element at index or returns fallback if out of bounds.
     */
    public T getOrDefault(int index, T defaultValue) {
        try {
            return get(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets element at index wrapped in Optional.
     */
    public @NotNull Optional<T> getOptional(int index) {
        try {
            return Optional.ofNullable(get(index));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Sets element at index (not available in SET mode).
     */
    public T set(int index, T element) {
        requireListMode("set");
        T old = ((List<T>) storage).set(index, element);
        sorted = false;
        modificationCount++;
        return old;
    }

    /**
     * Gets the first element.
     */
    public @NotNull Optional<T> first() {
        if (isEmpty()) {
            return Optional.empty();
        }

        if (storage instanceof List) {
            return Optional.of(((List<T>) storage).get(0));
        } else {
            return Optional.of(storage.iterator().next());
        }
    }

    /**
     * Gets the last element.
     */
    public @NotNull Optional<T> last() {
        if (isEmpty()) {
            return Optional.empty();
        }

        if (storage instanceof List) {
            List<T> list = (List<T>) storage;
            return Optional.of(list.get(list.size() - 1));
        } else if (storage instanceof LinkedList) {
            return Optional.of(((LinkedList<T>) storage).getLast());
        } else {
            T last = null;
            for (T element : storage) {
                last = element;
            }
            return Optional.ofNullable(last);
        }
    }

    // ==================== QUERYING ====================

    /**
     * Checks if collection contains the element.
     * Uses binary search if sorted.
     */
    public boolean contains(@Nullable T element) {
        if (element == null) {
            return storage.contains(null);
        }
        if (sorted && storage instanceof List<?> list && element instanceof Comparable<?> comparableElement) {
            try {
                return Collections.binarySearch((List<? extends Comparable<Object>>) list, comparableElement) >= 0;
            } catch (ClassCastException e) {
                return storage.contains(element);
            }
        }
        return storage.contains(element);
    }

    /**
     * Checks if all elements match the predicate.
     */
    public boolean allMatch(@NotNull Predicate<? super T> predicate) {
        return storage.stream().allMatch(predicate);
    }

    /**
     * Checks if any element matches the predicate.
     */
    public boolean anyMatch(@NotNull Predicate<? super T> predicate) {
        return storage.stream().anyMatch(predicate);
    }

    /**
     * Checks if no elements match the predicate.
     */
    public boolean noneMatch(@NotNull Predicate<? super T> predicate) {
        return storage.stream().noneMatch(predicate);
    }

    /**
     * Finds first element matching predicate.
     */
    public @NotNull Optional<T> findFirst(@NotNull Predicate<? super T> predicate) {
        return storage.stream().filter(predicate).findFirst();
    }

    /**
     * Finds any element matching predicate.
     */
    public @NotNull Optional<T> findAny(@NotNull Predicate<? super T> predicate) {
        return storage.stream().filter(predicate).findAny();
    }

    /**
     * Finds all elements matching predicate.
     */
    public @NotNull Dynamic<T> findAll(@NotNull Predicate<? super T> predicate) {
        return filter(predicate);
    }

    /**
     * Counts elements matching predicate.
     */
    public long count(@NotNull Predicate<? super T> predicate) {
        return storage.stream().filter(predicate).count();
    }

    /**
     * Gets index of first occurrence of element.
     */
    public int indexOf(@Nullable T element) {
        if (storage instanceof List) {
            return ((List<T>) storage).indexOf(element);
        }

        int index = 0;
        for (T item : storage) {
            if (Objects.equals(item, element)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Gets index of last occurrence of element.
     */
    public int lastIndexOf(@Nullable T element) {
        requireListMode("lastIndexOf");
        return ((List<T>) storage).lastIndexOf(element);
    }

    // ==================== TRANSFORMATION ====================

    /**
     * Maps elements to a new type.
     */
    public <R> @NotNull Dynamic<R> map(@NotNull Function<? super T, ? extends R> mapper) {
        List<R> mapped = storage.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new Dynamic<>(mapped, mode, allowDuplicates);
    }

    /**
     * Flat maps elements.
     */
    public <R> @NotNull Dynamic<R> flatMap(@NotNull Function<? super T, ? extends Collection<? extends R>> mapper) {
        List<R> result = storage.stream()
                .flatMap(e -> mapper.apply(e).stream())
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    /**
     * Filters elements.
     */
    public @NotNull Dynamic<T> filter(@NotNull Predicate<? super T> predicate) {
        List<T> filtered = storage.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        return new Dynamic<>(filtered, mode, allowDuplicates);
    }

    /**
     * Removes duplicates.
     */
    public @NotNull Dynamic<T> distinct() {
        return new Dynamic<>(new LinkedHashSet<>(storage), StorageMode.SET, false);
    }

    /**
     * Removes duplicates by key extractor.
     */
    public <K> @NotNull Dynamic<T> distinctBy(@NotNull Function<? super T, ? extends K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        List<T> result = storage.stream()
                .filter(e -> seen.add(keyExtractor.apply(e)))
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, false);
    }

    /**
     * Takes first N elements.
     */
    public @NotNull Dynamic<T> takeFirst(int n) {
        List<T> result = storage.stream()
                .limit(n)
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    /**
     * Takes last N elements.
     */
    public @NotNull Dynamic<T> takeLast(int n) {
        int size = size();
        if (n >= size) {
            return copy();
        }

        List<T> result = storage.stream()
                .skip(size - n)
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    /**
     * Skips first N elements.
     */
    public @NotNull Dynamic<T> skip(int n) {
        List<T> result = storage.stream()
                .skip(n)
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    /**
     * Takes elements while predicate is true.
     */
    public @NotNull Dynamic<T> takeWhile(@NotNull Predicate<? super T> predicate) {
        List<T> result = storage.stream()
                .takeWhile(predicate)
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    /**
     * Skips elements while predicate is true.
     */
    public @NotNull Dynamic<T> dropWhile(@NotNull Predicate<? super T> predicate) {
        List<T> result = storage.stream()
                .dropWhile(predicate)
                .collect(Collectors.toList());
        return new Dynamic<>(result, mode, allowDuplicates);
    }

    // ==================== SORTING ====================

    /**
     * Sorts using natural order (requires Comparable).
     */
    public @NotNull Dynamic<T> sorted() {
        if (storage.isEmpty() || !(storage.iterator().next() instanceof Comparable)) {
            return this;
        }

        List<T> list = new ArrayList<>(storage);
        Collections.sort((List<? extends Comparable>) list);
        storage.clear();
        storage.addAll(list);
        sorted = true;
        lastComparator = null;
        return this;
    }

    /**
     * Sorts using a comparator.
     */
    public @NotNull Dynamic<T> sorted(@NotNull Comparator<? super T> comparator) {
        List<T> list = new ArrayList<>(storage);
        list.sort(comparator);
        storage.clear();
        storage.addAll(list);
        sorted = true;
        lastComparator = (Comparator<T>) comparator;
        return this;
    }

    /**
     * Sorts by a key extractor.
     */
    public <U extends Comparable<? super U>> @NotNull Dynamic<T> sortedBy(@NotNull Function<? super T, ? extends U> keyExtractor) {
        return sorted(Comparator.comparing(keyExtractor));
    }

    /**
     * Reverses the order of elements.
     */
    public @NotNull Dynamic<T> reversed() {
        If.when(storage instanceof List)
                .then(() -> Collections.reverse((List<T>) storage))
                .otherwise(() -> {
                    List<T> list = new ArrayList<>(storage);
                    Collections.reverse(list);
                    storage.clear();
                    storage.addAll(list);
                });
        return this;
    }

    /**
     * Shuffles elements randomly.
     */
    public @NotNull Dynamic<T> shuffled() {
        List<T> list = new ArrayList<>(storage);
        Collections.shuffle(list);
        storage.clear();
        storage.addAll(list);
        sorted = false;
        return this;
    }

    // ==================== REDUCTION ====================

    /**
     * Reduces to a single value.
     */
    public <U> U reduce(U identity, @NotNull BiFunction<U, ? super T, U> accumulator) {
        U result = identity;
        for (T element : storage) {
            result = accumulator.apply(result, element);
        }
        return result;
    }

    /**
     * Reduces to Optional.
     */
    public @NotNull Optional<T> reduce(@NotNull BinaryOperator<T> accumulator) {
        return storage.stream().reduce(accumulator);
    }

    /**
     * Joins elements to string.
     */
    public @NotNull String join(@NotNull CharSequence delimiter) {
        return storage.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Joins with prefix and suffix.
     */
    public @NotNull String join(@NotNull CharSequence delimiter, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        return storage.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }

    // ==================== GROUPING & PARTITIONING ====================

    /**
     * Groups elements by key.
     */
    public <K> @NotNull Map<K, List<T>> groupBy(@NotNull Function<? super T, ? extends K> classifier) {
        return storage.stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * Groups and maps values.
     */
    public <K, V> @NotNull Map<K, List<V>> groupBy(@NotNull Function<? super T, ? extends K> classifier,
                                                   @NotNull Function<? super T, ? extends V> valueMapper) {
        return storage.stream().collect(
                Collectors.groupingBy(classifier, Collectors.mapping(valueMapper, Collectors.toList()))
        );
    }

    /**
     * Partitions into two groups.
     */
    public @NotNull Partition<T> partition(@NotNull Predicate<? super T> predicate) {
        Map<Boolean, List<T>> partitioned = storage.stream()
                .collect(Collectors.partitioningBy(predicate));
        return new Partition<>(
                Dynamic.of(partitioned.get(true)),
                Dynamic.of(partitioned.get(false))
        );
    }

    public record Partition<T>(Dynamic<T> matching, Dynamic<T> notMatching) {}

    // ==================== AGGREGATION ====================

    /**
     * Gets minimum element.
     */
    public @NotNull Optional<T> min(@NotNull Comparator<? super T> comparator) {
        return storage.stream().min(comparator);
    }

    /**
     * Gets maximum element.
     */
    public @NotNull Optional<T> max(@NotNull Comparator<? super T> comparator) {
        return storage.stream().max(comparator);
    }

    /**
     * Calculates sum for numeric elements.
     */
    public <N extends Number> double sum(@NotNull Function<? super T, N> extractor) {
        return storage.stream()
                .mapToDouble(e -> extractor.apply(e).doubleValue())
                .sum();
    }

    /**
     * Calculates average for numeric elements.
     */
    public <N extends Number> @NotNull OptionalDouble average(@NotNull Function<? super T, N> extractor) {
        return storage.stream()
                .mapToDouble(e -> extractor.apply(e).doubleValue())
                .average();
    }

    // ==================== COMBINING ====================

    /**
     * Merges with another Dynamic.
     */
    public @NotNull Dynamic<T> merge(@NotNull Dynamic<T> other) {
        addAll(other.storage);
        return this;
    }

    /**
     * Creates union with another collection.
     */
    public @NotNull Dynamic<T> union(@NotNull Collection<? extends T> other) {
        Dynamic<T> result = copy();
        result.addAll(other);
        return result.distinct();
    }

    /**
     * Creates intersection with another collection.
     */
    public @NotNull Dynamic<T> intersection(@NotNull Collection<? extends T> other) {
        return filter(other::contains);
    }

    /**
     * Creates difference (elements in this but not in other).
     */
    public @NotNull Dynamic<T> difference(@NotNull Collection<? extends T> other) {
        return filter(e -> !other.contains(e));
    }

    /**
     * Zips with another Dynamic.
     */
    public <U, R> @NotNull Dynamic<R> zip(@NotNull Dynamic<U> other, @NotNull BiFunction<? super T, ? super U, ? extends R> combiner) {
        Iterator<T> it1 = iterator();
        Iterator<U> it2 = other.iterator();
        Dynamic<R> result = empty();

        while (it1.hasNext() && it2.hasNext()) {
            result.add(combiner.apply(it1.next(), it2.next()));
        }

        return result;
    }

    // ==================== CONVERSION ====================

    /**
     * Converts to ArrayList.
     */
    public @NotNull List<T> toList() {
        return new ArrayList<>(storage);
    }

    /**
     * Converts to LinkedHashSet.
     */
    public @NotNull Set<T> toSet() {
        return new LinkedHashSet<>(storage);
    }

    /**
     * Converts to array.
     */
    public T[] toArray(@NotNull IntFunction<T[]> generator) {
        return storage.toArray(generator);
    }

    /**
     * Converts to stream.
     */
    public @NotNull Stream<T> stream() {
        sequentialAccessCount++;
        autoOptimize();
        return storage.stream();
    }

    /**
     * Converts to parallel stream.
     */
    public @NotNull Stream<T> parallelStream() {
        return storage.stream().parallel();
    }

    /**
     * Creates a collector for Dynamic.
     */
    public static <T> @NotNull Collector<T, ?, Dynamic<T>> collector() {
        return Collector.of(
                Dynamic::empty,
                Dynamic::add,
                (d1, d2) -> { d1.addAll(d2.storage); return d1; }
        );
    }

    // ==================== UTILITY ====================

    public int size() {
        return storage.size();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public boolean isNotEmpty() {
        return !storage.isEmpty();
    }

    /**
     * Creates a shallow copy.
     */
    public @NotNull Dynamic<T> copy() {
        return new Dynamic<>(new ArrayList<>(storage), mode, allowDuplicates);
    }

    /**
     * Executes action for each element.
     */
    public void forEach(@NotNull Consumer<? super T> action) {
        storage.forEach(action);
    }

    /**
     * Executes action with index.
     */
    public @NotNull Dynamic<T> forEachIndexed(@NotNull BiConsumer<Integer, ? super T> action) {
        int index = 0;
        for (T element : storage) {
            action.accept(index++, element);
        }
        return this;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return storage.iterator();
    }

    @Override
    public String toString() {
        return storage.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dynamic<?> other)) return false;
        return Objects.equals(storage, other.storage);
    }

    @Override
    public int hashCode() {
        return storage.hashCode();
    }

    // ==================== HELPERS ====================

    private void requireListMode(String operation) {
        If.throwIf(!(storage instanceof List),
                () -> new UnsupportedOperationException(
                        operation + Stringifier.format(" is not supported in {} mode. Switch to ARRAY or LINKED mode.", mode)));
    }
}