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
package com.github.rickmvi.jtoolbox.control;

import com.github.rickmvi.jtoolbox.lang.exceptions.InvalidStartIndexException;
import com.github.rickmvi.jtoolbox.lang.exceptions.InvalidEndIndexException;
import com.github.rickmvi.jtoolbox.lang.exceptions.InvalidStepOutOfBounds;
import com.github.rickmvi.jtoolbox.lang.exceptions.ErrorMessage;
import com.github.rickmvi.jtoolbox.collections.array.Array;

import com.github.rickmvi.jtoolbox.utils.Primitives;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntPredicate;
import java.util.function.IntFunction;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;


@lombok.experimental.UtilityClass
public class Iteration {

    /**
     * Iterates over a range of integers in ascending order with a given step.
     *
     * <pre>{@code
     * Iteration.forEachRange(0, 10, 2, i -> System.out.print(i + " "));
     * // Output: 0 2 4 6 8
     * }</pre>
     */
    public static void forEachRange(int start, int end, int step, IntConsumer action) {
        int init = Primitives.nonNegative(start);
        int finish = Primitives.requiredPositive(end);
        int stepVal = Primitives.requiredPositive(step);

        Condition.ifTrueThrow(start > end, () -> new InvalidEndIndexException(start, end));

        IntConsumer act = Objects.requireNonNull(action);
        for (int i = init; i < finish; i += stepVal) act.accept(i);
    }

    /**
     * Iterates over a range of integers in descending order with a given step.
     *
     * <pre>{@code
     * Iteration.forEachRangeDescending(10, 0, 2, i -> System.out.print(i + " "));
     * // Output: 10 8 6 4 2 0
     * }</pre>
     */
    public static void forEachRangeDescending(int start, int end, int step, IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);

        Condition.ifTrueThrow(Primitives.isNonPositive(step), InvalidStepOutOfBounds::new);
        Condition.ifTrueThrow(start > end, () -> new InvalidEndIndexException(start, end));
        Condition.ifTrueThrow(Primitives.isNegative(start), InvalidStartIndexException::new);
        for (int i = start; i >= end; i -= step) act.accept(i);
    }

    /**
     * Iterates from {@code start} (inclusive) to {@code end} (exclusive).
     *
     * <pre>{@code
     * Iteration.forEachInRange(3, 6, i -> System.out.print(i + " "));
     * // Output: 3 4 5
     * }</pre>
     */
    public static void forEachInRange(int start, int end, @NotNull IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);

        Condition.ifTrueThrow(start > end, () -> new InvalidEndIndexException(start, end));
        Condition.ifTrueThrow(Primitives.isNegative(start), InvalidStartIndexException::new);
        for (int i = start; i < end; i++) act.accept(i);
    }

    /**
     * Iterates from {@code 0} to {@code repetitions - 1}.
     *
     * <pre>{@code
     * Iteration.forEachIndex(5, i -> System.out.print(i + " "));
     * // Output: 0 1 2 3 4
     * }</pre>
     */
    public static void forEachIndex(int repetitions, @NotNull IntConsumer action) {
        Condition.ifTrueThrow(Primitives.isNegative(repetitions), InvalidStepOutOfBounds::new);
        for (int i = 0; i < repetitions; i++) action.accept(i);
    }

    /**
     * Iterates in reverse order from {@code times - 1} down to {@code 0}.
     *
     * <pre>{@code
     * Iteration.forEachDescending(3, i -> System.out.print(i + " "));
     * // Output: 2 1 0
     * }</pre>
     */
    public static void forEachDescending(int times, IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);

        Condition.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = times - 1; i >= 0; i--) act.accept(i);
    }

    /**
     * Iterates from {@code start} down to {@code end}.
     *
     * <pre>{@code
     * Iteration.forEachDescending(5, 2, i -> System.out.print(i + " "));
     * // Output: 5 4 3 2
     * }</pre>
     */
    public static void forEachDescending(int start, int end, IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);

        Condition.ifTrueThrow(start < end, IllegalArgumentException::new);
        for (int i = start; i >= end; i--) act.accept(i);
    }

    /**
     * Iterates up to {@code times}, stopping early if {@code cancel} returns {@code true}.
     *
     * <pre>{@code
     * Iteration.forEachWhile(10, i -> System.out.print(i + " "), () -> i == 5);
     * // Output: 0 1 2 3 4
     * }</pre>
     */
    public static void forEachWhile(int times, IntConsumer action, Supplier<Boolean> cancel) {
        IntConsumer act = Objects.requireNonNull(action);
        Supplier<Boolean> can = Objects.requireNonNull(cancel);

        Condition.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = 0; i < times && !can.get(); i++) act.accept(i);
    }

    /**
     * Returns {@code true} if any index in {@code length} satisfies the predicate.
     *
     * <pre>{@code
     * boolean result = Iteration.anyMatch(5, i -> i == 3);
     * // result -> true
     * }</pre>
     */
    public static boolean anyMatch(int length, @NotNull IntPredicate predicate) {
        int size = Primitives.nonNegative(length);
        IntPredicate pred = Objects.requireNonNull(predicate);
        for (int i = 0; i < size; i++) {
            if (pred.test(i)) return true;
        }
        return false;
    }

    /**
     * Finds the first index that matches the given predicate.
     *
     * <pre>{@code
     * int idx = Iteration.findFirstIndexMatching(5, i -> i > 2);
     * // idx -> 3
     * }</pre>
     */
    public static int findFirstIndexMatching(int length, @NotNull IntPredicate predicate) {
        int lengthy = Primitives.nonNegative(length);
        for (int i = 0; i < lengthy; i++) {
            if (predicate.test(i)) return i;
        }
        return -1;
    }

    /**
     * Iterates only through the first half of indices (exclusive of the midpoint).
     *
     * <pre>{@code
     * Iteration.forEachHalf(6, i -> System.out.print(i + " "));
     * // Output: 0 1 2
     * }</pre>
     */
    public static void forEachHalf(int length, IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);

        Condition.ifTrueThrow(
                Primitives.isZero(length),
                () -> new InvalidEndIndexException(0, length));
        Condition.ifTrueThrow(
                Primitives.isNegative(length),
                () -> new InvalidEndIndexException(length, 0, ErrorMessage.INDEX_NEGATIVE));

        for (int i = 0; i < length / 2; i++) { act.accept(i); }
    }

    public static <T> void forEachHalf(T @NotNull [] array, IntConsumer action) {
        if (Array.isEmpty(array)) return;
        IntConsumer act = Objects.requireNonNull(action);
        for (int i = 0; i < Array.length(array) / 2; i++) {
            act.accept(i);
        }
    }

    /**
     * Finds the last index that matches the given predicate.
     *
     * <pre>{@code
     * int idx = Iteration.findLastIndexMatching(5, i -> i % 2 == 0);
     * // idx -> 4
     * }</pre>
     */
    public static int findLastIndexMatching(int repetitions, @NotNull IntPredicate predicate) {
        int repetition = Primitives.nonNegative(repetitions);
        for (int i = repetition - 1; i >= 0; i--) {
            if (predicate.test(i)) return i;
        }
        return -1;
    }

    /**
     * Finds the first non-null mapped value.
     *
     * <pre>{@code
     * String val = Iteration.findFirstMatchingValue(5, i -> i == 3 ? "found" : null);
     * // val -> "found"
     * }</pre>
     */
    public static <T> @Nullable T findFirstMatchingValue(
            int repetitions,
            @NotNull IntFunction<@Nullable T> mapper
    ) {
        int repetition = Primitives.nonNegative(repetitions);
        for (int i = 0; i < repetition; i++) {
            T result = mapper.apply(i);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Collects all non-null values returned by the mapper.
     *
     * <pre>{@code
     * List<String> values = Iteration.collectAllMatching(5, i -> i % 2 == 0 ? "even" + i : null);
     * // values -> ["even0", "even2", "even4"]
     * }</pre>
     */
    public static <T> @NotNull List<T> collectAllMatching(
            int repetitions,
            @NotNull IntFunction<@Nullable T> mapper
    ) {
        int repetition = Primitives.nonNegative(repetitions);
        List<T> results = new ArrayList<>();
        for (int i = 0; i < repetition; i++) {
            T value = mapper.apply(i);
            if (Objects.nonNull(value)) results.add(value);
        }
        return results;
    }

    /**
     * Runs {@link #forEachIndex(int, IntConsumer)} asynchronously in a {@link CompletableFuture}.
     *
     * <pre>{@code
     * Iteration.forEachAsync(3, i -> System.out.println("Async: " + i))
     *          .join();
     * // Output (async order):
     * // Async: 0
     * // Async: 1
     * // Async: 2
     * }</pre>
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<Void> forEachAsync(int times, IntConsumer action) {
        IntConsumer act = Objects.requireNonNull(action);
        return CompletableFuture.runAsync(() -> forEachIndex(times, act));
    }
}
