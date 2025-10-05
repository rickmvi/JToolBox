package com.github.rickmvi.jtoolbox.control.fors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;

/**
 * Functional iteration utility interface for processing sequences of elements.
 * <p>
 * Provides a fluent and uniform API to iterate, filter, map, and collect values
 * from ranges of numbers or generic collections.
 * </p>
 *
 * <h2>Core Capabilities:</h2>
 * <ul>
 *     <li>{@link #forEach(Consumer)} – Iterate through each element sequentially.</li>
 *     <li>{@link #forEachAsync(Consumer)} – Iterate asynchronously using a {@link CompletableFuture}.</li>
 *     <li>{@link #anyMatch(Predicate)} – Test whether any element satisfies a condition.</li>
 *     <li>{@link #findFirst(Predicate)} / {@link #findLast(Predicate)} – Search for the first or last element matching a condition.</li>
 *     <li>{@link #findFirstValue(Function)} – Apply a mapper to elements and return the first non-null result.</li>
 *     <li>{@link #collect(Function)} – Transform elements with a mapper and collect non-null results into a {@link List}.</li>
 * </ul>
 *
 * <h2>Static Factory Methods:</h2>
 * <p>
 * Supports iteration over primitive ranges or collections:
 * <ul>
 *     <li>{@link #range(int, int)} / {@link #range(long, long)} / {@link #range(double, double)} – Inclusive ascending ranges.</li>
 *     <li>{@link #rangeDescentive(int, int)} / {@link #rangeDescentive(long, long)} / {@link #rangeDescentive(double, double)} – Inclusive descending ranges.</li>
 *     <li>{@link #range(int, int, int)} / {@link #range(long, long, long)} / {@link #range(double, double, double)} – Ranges with custom step values.</li>
 *     <li>{@link #of(Iterable)} / {@link #of(Object...)} – Create a For from a collection or array of elements.</li>
 * </ul>
 * </p>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Iterate over an integer range
 * For.range(1, 5).forEach(System.out::println);
 *
 * // Find first even number in a range
 * Integer firstEven = For.range(1, 10)
 *                        .findFirst(i -> i % 2 == 0);
 *
 * // Map elements to strings and collect results
 * List<String> words = For.of("a", "bb", "ccc")
 *                         .collect(String::toUpperCase);
 *
 * // Async iteration
 * For.range(1, 3).forEachAsync(i -> System.out.println("Async: " + i));
 * }</pre>
 *
 * <p>
 * This interface unifies iteration over numeric ranges and generic collections,
 * providing a functional and fluent alternative to standard for-loops.
 * </p>
 *
 * @param <T> the type of elements to iterate over
 * @author Rick
 * @since 1.0
 */
public interface For<T> {

    void forEach(Consumer<T> action);

    CompletableFuture<Void> forEachAsync(Consumer<T> action);

    boolean anyMatch(Predicate<T> predicate);

    T findFirst(Predicate<T> predicate);

    T findLast(Predicate<T> predicate);

    <R> R findFirstValue(Function<T, R> mapper);

    <R> List<R> collect(Function<T, R> mapper);

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Integer> range(int start, int end) {
        return new ForInt(start, end, 1, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Integer> rangeDescentive(int start, int end) {
        return new ForInt(start, end, 1, true);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull For<Integer> range(int start, int end, int step) {
        return new ForInt(start, end, step, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Long> range(long start, long end) {
        return new ForLong(start, end, 1, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Long> rangeDescentive(long start, long end) {
        return new ForLong(start, end, 1, true);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull For<Long> range(long start, long end, long step) {
        return new ForLong(start, end, step, false);
    }

    @Contract("_, _ -> new")
    static @NotNull For<Double> rangeDescentive(double start, double end) {
        return new ForDouble(start, end, 1, true);
    }

    @Contract("_, _, _ -> new")
    static @NotNull For<Double> range(double start, double end, double step) {
        return new ForDouble(start, end, step, false);
    }

    @Contract("_, _ -> new")
    static @NotNull For<Double> range(double start, double end) {
        return new ForDouble(start, end, 1, false);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull For<T> of(Iterable<T> iterable) {
        return new ForIterable<>(iterable);
    }

    @SafeVarargs
    @Contract("_ -> new")
    static <T> @NotNull For<T> of(T ... item) {
        return new ForIterable<>(List.of(item));
    }

}
