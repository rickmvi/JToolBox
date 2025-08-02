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

import com.github.rickmvi.jtoolbox.control.internal.MathUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntPredicate;
import java.util.function.IntFunction;
import java.util.function.IntConsumer;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;

@lombok.experimental.UtilityClass
public final class Iteration extends MathUtils {

    /**
     * Executes the specified action for every index from the start (inclusive) to the end (exclusive),
     * incrementing by the specified step on each iteration.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachRange(0, 10, 2, i -> {
     *     System.out.println("Even index: " + i);
     * });
     * }</pre>
     * // Output:
     * // Even index: 0
     * // Even index: 2
     * // Even index: 4
     * // Even index: 6
     * // Even index: 8
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (exclusive)
     * @param step   the increment value for each iteration; must be greater than 0
     * @param action the action to perform in each iteration, receiving the current index
     * @throws IllegalArgumentException if a step is less than or equal to 0
     */
    public static void forEachRange(int start, int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(step <= 0 || start > end, IllegalArgumentException::new);
        for (int i = start; i < end; i += step) action.accept(i);
    }

    /**
     * Executes the specified action for every index from the start (inclusive) down to the end (inclusive),
     * decrementing by the specified step on each iteration.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachRangeDescending(10, 0, 2, i -> {
     *     System.out.println("Descending even index: " + i);
     * });
     * }</pre>
     * // Output:
     * // Descending even index: 10
     * // Descending even index: 8
     * // Descending even index: 6
     * // Descending even index: 4
     * // Descending even index: 2
     * // Descending even index: 0
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (inclusive and less than or equal to start)
     * @param step   the decrement value for each iteration; must be greater than 0
     * @param action the action to perform in each iteration, receiving the current index
     * @throws IllegalArgumentException if a step is less than or equal to 0
     */
    public static void forEachRangeDescending(int start, int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(step <= 0 || start < end, IllegalArgumentException::new);
        for (int i = start; i >= end; i -= step) action.accept(i);
    }

    /**
     * Executes the specified action for every index from 0 (inclusive)
     * to half of the provided end value (exclusive).
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachHalf(10, i -> {
     *     System.out.println("First half index: " + i);
     * });
     * }</pre>
     * // Output:
     * // First half index: 0
     * // First half index: 1
     * // First half index: 2
     * // First half index: 3
     * // First half index: 4
     *
     * @param end    the upper limit of the range (exclusive); the iteration
     *               will run from 0 to (end / 2-1)
     * @param action the action to perform on each index, accepting the current index as input
     */
    public static void forEachHalf(int end, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(end < 0, IllegalArgumentException::new);
        for (int i = 0; i < end / 2; i++) {
            action.accept(i);
        }
    }

    /**
     * Executes the specified action for every index from 0 (inclusive)
     * to half the length of the provided array (exclusive).
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * String[] names = {"Alice", "Bob", "Charlie", "David", "Eve"};
     * Iteration.forEachHalf(names, i -> {
     *     System.out.println("First half name: " + names[i]);
     * });
     * }</pre>
     * // Output:
     * // First half name: Alice
     * // First half name: Bob
     *
     * @param <T>    the type of elements in the array
     * @param array  the array to iterate over; must not be null
     * @param action the action to perform on each index, accepting the current index as input
     * @throws NullPointerException if {@code array} or {@code action} is null
     */
    public static <T> void forEachHalf(T @NotNull [] array, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        for (int i = 0; i < array.length / 2; i++) {
            action.accept(i);
        }
    }

    /**
     * Executes the specified action for every index from 0 (inclusive)
     * to half of the provided end value (exclusive), incrementing by
     * the specified step on each iteration.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachHalf(10, 2, i -> {
     *     System.out.println("Stepped first half index: " + i);
     * });
     * }</pre>
     * // Output:
     * // Stepped first half index: 0
     * // Stepped first half index: 2
     * // Stepped first half index: 4
     *
     * @param end    the upper limit of the range (exclusive); the iteration
     *               will run from 0 to (end / 2-1)
     * @param step   the increment value for each iteration; must be greater than 0
     * @param action the action to perform on each index, accepting the current index as input
     * @throws IllegalArgumentException if the step is less than or equal to 0
     */
    public static void forEachHalf(int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(end < 0 || step <= 0, IllegalArgumentException::new);
        for (int i = 0; i < end / 2; i += step) {
            action.accept(i);
        }
    }

    /**
     * Evaluates the provided predicate for a range of integers from 0 to {@code length - 1}.
     * Returns {@code true} if the predicate evaluates to {@code true} for any value in the range.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * boolean hasEven = Iteration.anyMatch(5, i -> i % 2 == 0);
     * System.out.println("Contains even number: " + hasEven); // true
     * }</pre>
     *
     * @param length the length of the range, indicating the number of iterations.
     *               Must be non-negative.
     * @param predicate the {@code IntPredicate} used to test each integer in the range.
     *                  Must not be null.
     * @return {@code true} if the predicate evaluates to {@code true} for any integer in
     *         the range; {@code false} otherwise.
     * @throws IllegalArgumentException if {@code length} is negative.
     * @throws NullPointerException if {@code predicate} is null.
     */
    public static boolean anyMatch(int length, @NotNull IntPredicate predicate) {
        for (int i = 0; i < length; i++) {
            if (predicate.test(i)) return true;
        }
        return false;
    }

    /**
     * Executes the specified action for each value in the range from start (inclusive) to end (exclusive).
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachInRange(5, 10, i -> {
     *     System.out.println("Number: " + i);
     * });
     * }</pre>
     * // Output:
     * // Number: 5
     * // Number: 6
     * // Number: 7
     * // Number: 8
     * // Number: 9
     *
     * @param start  the starting value of the range (inclusive)
     * @param end    the ending value of the range (exclusive)
     * @param action the action to perform on each value in the range; receives the current value as input
     * @throws NullPointerException if the action is null
     */
    public static void forEachInRange(int start, int end, @NotNull IntConsumer action) {
        Conditionals.ifTrueThrow(start > end, IllegalArgumentException::new);
        for (int i = start; i < end; i++) action.accept(i);
    }

    /**
     * Repeats the given action a number of times in ascending order, from index {@code 0} to {@code repetitions - 1}.
     * <p>
     * This method is useful for iterating a fixed number of times without the need to write explicit {@code for} loops.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachIndex(5, i -> {
     *     System.out.println("Index: " + i);
     * });
     * }</pre>
     * // Output:
     * // Index: 0
     * // Index: 1
     * // Index: 2
     * // Index: 3
     * // Index: 4
     *
     * @param repetitions the number of times to repeat the action; must be non-negative
     * @param action      the action to perform, receiving the current index (from 0 to repetitions - 1)
     * @throws NullPointerException     if {@code action} is null
     * @throws IllegalArgumentException if {@code repetitions} is negative
     */
    public static void forEachIndex(int repetitions, @NotNull IntConsumer action) {
        Conditionals.ifTrueThrow(repetitions < 0, IllegalArgumentException::new);
        for (int i = 0; i < repetitions; i++) action.accept(i);
    }

    /**
     * Repeats the given function until it returns a non-null result or the maximum number of repetitions is reached.
     * <p>
     * Iterates from {@code 0} to {@code length - 1}, passing each index to the provided function. If the function returns
     * a non-null result, the loop stops and that result is returned. If no result is found, {@code null} is returned.
     * <p>
     * Useful for searching or evaluating a sequence until a match is found.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * String[] names = {"Alice", "Bob", null, "David"};
     * String firstNonNull = Iteration.findFirstByFunction(names.length, i -> names[i]);
     * System.out.println("First non-null name: " + firstNonNull); // "Alice"
     * }</pre>
     *
     * @param length   the number of iterations (typically the length of an array or list)
     * @param function a function that receives the index and returns a result or {@code null} to continue
     * @param <T>      the type of result returned
     * @return the first non-null result returned by the function, or {@code null} if none found
     * @throws NullPointerException if {@code function} is null
     */
    public static <T> @Nullable T findFirstByFunction(int length, @NotNull IntFunction<T> function) {
        for (int i = 0; i < length; i++) {
            T result = function.apply(i);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Repeats the given predicate over a range of indices and returns the first index
     * for which the predicate evaluates to {@code true}.
     * <p>
     * If the predicate does not match any index, returns {@code -1}.
     * <p>
     * Useful as a flexible replacement for {@code indexOf} when more complex conditions are needed.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * String[] names = {"Alice", "Bob", "Charlie"};
     * int index = Iteration.findFirstIndexMatching(names.length, i -> names[i].startsWith("B"));
     * System.out.println("Index of name starting with B: " + index); // 1
     * }</pre>
     *
     * @param length    the number of iterations (typically array or list length)
     * @param predicate the condition to evaluate for each index
     * @return the index of the first match, or {@code -1} if none matched
     * @throws NullPointerException if {@code predicate} is null
     */
    public static int findFirstIndexMatching(int length, @NotNull IntPredicate predicate) {
        for (int i = 0; i < length; i++) {
            if (predicate.test(i)) return i;
        }
        return -1;
    }

    /**
     * Repeats the given predicate over a range of indices and returns the last index
     * for which the predicate returns {@code true}, or -1 if no match is found.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * int lastEvenIndex = Iteration.findLastIndexMatching(10, i -> i % 2 == 0);
     * System.out.println("Last even index: " + lastEvenIndex); // 8
     * }</pre>
     *
     * @param repetitions the number of iterations to perform
     * @param predicate the predicate to test for each index
     * @return the last index for which the predicate returns true, or -1 if none match
     */
    public static int findLastIndexMatching(int repetitions, @NotNull IntPredicate predicate) {
        Conditionals.ifTrueThrow(repetitions < 0, IllegalArgumentException::new);
        int last = -1;
        for (int i = 0; i < repetitions; i++) {
            if (predicate.test(i)) last = i;
        }
        return last;
    }

    /**
     * Applies the given mapper function over a number of repetitions and collects
     * all non-null results into a list.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * List<String> values = Iteration.collectAllMatching(5, i -> i % 2 == 0 ? "even" + i : null);
     * System.out.println(values); // ["even0", "even2", "even4"]
     * }</pre>
     *
     * @param repetitions the number of iterations to perform
     * @param mapper the function to apply for each index
     * @param <T> the type of result collected
     * @return a list of non-null results collected from the mapper function
     */
    public static <T> @NotNull List<T> collectAllMatching(int repetitions, @NotNull IntFunction<@Nullable T> mapper) {
        Conditionals.ifTrueThrow(repetitions < 0, IllegalArgumentException::new);
        List<T> results = new ArrayList<>();
        for (int i = 0; i < repetitions; i++) {
            T value = mapper.apply(i);
            if (value != null) results.add(value);
        }
        return results;
    }

    /**
     * Applies the given mapper function over a number of repetitions and returns
     * the first non-null result found, or {@code null} if none are found.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * String firstMatch = Iteration.findFirstMatchingValue(5, i -> i == 3 ? "found" : null);
     * System.out.println(firstMatch); // "found"
     * }</pre>
     *
     * @param repetitions the number of iterations to perform
     * @param mapper the function to apply for each index
     * @param <T> the type of result to return
     * @return the first non-null value found, or null if none match
     */
    public static <T> @Nullable T findFirstMatchingValue(int repetitions, @NotNull IntFunction<@Nullable T> mapper) {
        Conditionals.ifTrueThrow(repetitions < 0, IllegalArgumentException::new);
        for (int i = 0; i < repetitions; i++) {
            T result = mapper.apply(i);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Repeats the given action a number of times in descending order (times - 1 to 0).
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachDescending(5, i -> {
     *     System.out.println("Descending index: " + i);
     * });
     * }</pre>
     * // Output:
     * // Descending index: 4
     * // Descending index: 3
     * // Descending index: 2
     * // Descending index: 1
     * // Descending index: 0
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     */
    public static void forEachDescending(int times, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = times - 1; i >= 0; i--) action.accept(i);
    }

    /**
     * Executes the specified action for each pair of (forward index, reverse index) from 0 to times-1.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachReversedWithIndex(5, (forward, reverse) -> {
     *     System.out.println("Forward: " + forward + ", Reverse: " + reverse);
     * });
     * }</pre>
     * // Output:
     * // Forward: 0, Reverse: 4
     * // Forward: 1, Reverse: 3
     * // Forward: 2, Reverse: 2
     * // Forward: 3, Reverse: 1
     * // Forward: 4, Reverse: 0
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving both forward and reverse indices
     */
    @Contract(pure = true)
    public static void forEachReversedWithIndex(int times, BiConsumer<Integer, Integer> action) {
        int index = 0;
        for (int i = times - 1; i >= 0; i--) {

            action.accept(index++, i);
        }
    }

    /**
     * Executes the specified action for each index, starting from {@code start} (inclusive)
     * and decrementing down to {@code end} (inclusive).
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachDescending(10, 5, i -> {
     *     System.out.println("Countdown: " + i);
     * });
     * }</pre>
     * // Output:
     * // Countdown: 10
     * // Countdown: 9
     * // Countdown: 8
     * // Countdown: 7
     * // Countdown: 6
     * // Countdown: 5
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (inclusive and less than or equal to {@code start})
     * @param action the action to perform, receiving the current index
     * @throws NullPointerException if {@code action} is null
     */
    public static void forEachDescending(int start, int end, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(start < end, IllegalArgumentException::new);
        for (int i = start; i >= end; i--) action.accept(i);
    }

    /**
     * Repeats the given action a number of times but allows cancellation via a boolean supplier.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * AtomicInteger counter = new AtomicInteger(0);
     * Iteration.forEachWhile(10,
     *     i -> {
     *         System.out.println("Processing: " + i);
     *         counter.incrementAndGet();
     *     },
     *     () -> counter.get() >= 3
     * );
     * }</pre>
     * // Output:
     * // Processing: 0
     * // Processing: 1
     * // Processing: 2
     *
     * @param times   number of repetitions
     * @param action  the action to perform, receiving the current index
     * @param cancel  the supplier that determines if repetition should stop
     */
    public static void forEachWhile(int times, IntConsumer action, Supplier<Boolean> cancel) {
        Conditionals.ifTrueThrow(action == null || cancel == null, NullPointerException::new);
        Conditionals.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = 0; i < times && !cancel.get(); i++) action.accept(i);
    }

    /**
     * Asynchronously repeats the given action a number of times.
     *
     * <p><b>Example usage:</b>
     * <pre>{@code
     * Iteration.forEachAsync(5, i -> {
     *     System.out.println("Async index: " + i);
     * }).thenRun(() -> System.out.println("Done!"));
     * }</pre>
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     * @return a {@link CompletableFuture} that completes when repetition is done
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<Void> forEachAsync(int times, IntConsumer action) {
        return CompletableFuture.runAsync(() -> forEachIndex(times, action));
    }

    /**
     * A builder class for constructing and executing customized loops with flexible
     * start, end, step, direction, and range-halving configurations.
     */
    public static class LoopBuilder {
        private int start = 0;
        private int end = 0;
        private int step = 1;
        private boolean descending = false;
        private boolean half = false;

        public LoopBuilder from(int start) {
            this.start = start;
            return this;
        }

        public LoopBuilder to(int end) {
            this.end = end;
            return this;
        }

        public LoopBuilder step(int step) {
            this.step = step;
            return this;
        }

        public LoopBuilder descending() {
            this.descending = true;
            return this;
        }

        public LoopBuilder half() {
            this.half = true;
            return this;
        }

        public void run(IntConsumer action) {
            int realEnd = half ? start + (end - start) / 2 : end;
            if (descending) {
                forEachRangeDescending(start, realEnd, step, action);
            } else {
                forEachRange(start, realEnd, step, action);
            }
        }
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull static LoopBuilder loop() {
        return new LoopBuilder();
    }
}
