package com.github.rickmvi.jtoolbox.control;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public final class Iteration {
    /**
     * Executes the specified action for every index from the start (inclusive) to the end (exclusive),
     * incrementing by the specified step on each iteration.
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (exclusive)
     * @param step   the increment value for each iteration; must be greater than 0
     * @param action the action to perform in each iteration, receiving the current index
     * @throws IllegalArgumentException if a step is less than or equal to 0
     */
    public static void range(int start, int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(step <= 0 || start > end, IllegalArgumentException::new);
        for (int i = start; i < end; i += step) action.accept(i);
    }

    /**
     * Executes the specified action for every index from the start (inclusive) down to the end (inclusive),
     * decrementing by the specified step on each iteration.
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (inclusive and less than or equal to start)
     * @param step   the decrement value for each iteration; must be greater than 0
     * @param action the action to perform in each iteration, receiving the current index
     * @throws IllegalArgumentException if a step is less than or equal to 0
     */
    public static void rangeDescending(int start, int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(step <= 0 || start < end, IllegalArgumentException::new);
        for (int i = start; i >= end; i -= step) action.accept(i);
    }

    /**
     * Executes the specified action for every index from 0 (inclusive)
     * to half of the provided end value (exclusive).
     *
     * @param end    the upper limit of the range (exclusive); the iteration
     *               will run from 0 to (end / 2-1)
     * @param action the action to perform on each index, accepting the current index as input
     */
    public static void half(int end, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(end < 0, IllegalArgumentException::new);
        for (int i = 0; i < end / 2; i++) {
            action.accept(i);
        }
    }

    /**
     * Executes the specified action for every index from 0 (inclusive)
     * to half of the provided end value (exclusive), incrementing by
     * the specified step on each iteration.
     *
     * @param end    the upper limit of the range (exclusive); the iteration
     *               will run from 0 to (end / 2-1)
     * @param step   the increment value for each iteration; must be greater than 0
     * @param action the action to perform on each index, accepting the current index as input
     * @throws IllegalArgumentException if the step is less than or equal to 0
     */
    public static void half(int end, int step, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(end < 0 || step <= 0, IllegalArgumentException::new);
        for (int i = 0; i < end / 2; i += step) {
            action.accept(i);
        }
    }

    /**
     * Repeats the given action a number of times in ascending order (0 to times - 1).
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     */
    public static void repeat(int times, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = 0; i < times; i++) action.accept(i);
    }

    /**
     * Executes the specified action for each value in the range from start (inclusive) to end (exclusive).
     *
     * @param start  the starting value of the range (inclusive)
     * @param end    the ending value of the range (exclusive)
     * @param action the action to perform on each value in the range; receives the current value as input
     * @throws NullPointerException if the action is null
     */
    public static void repeat(int start, int end, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(start > end, IllegalArgumentException::new);
        for (int i = start; i < end; i++) action.accept(i);
    }

    /**
     * Repeats the given action a number of times in descending order (times - 1 to 0).
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     */
    public static void repeatDescending(int times, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = times - 1; i >= 0; i--) action.accept(i);
    }

    /**
     * Executes the specified action for each index, starting from {@code start} (inclusive)
     * and decrementing down to {@code end} (inclusive).
     *
     * @param start  the starting index (inclusive)
     * @param end    the ending index (inclusive and less than or equal to {@code start})
     * @param action the action to perform, receiving the current index
     * @throws NullPointerException if {@code action} is null
     */
    public static void repeatDescending(int start, int end, IntConsumer action) {
        Conditionals.ifTrueThrow(action == null, NullPointerException::new);
        Conditionals.ifTrueThrow(start < end, IllegalArgumentException::new);
        for (int i = start; i >= end; i--) action.accept(i);
    }

    /**
     * Repeats the given action a number of times but allows cancellation via a boolean supplier.
     *
     * @param times   number of repetitions
     * @param action  the action to perform, receiving the current index
     * @param cancel  the supplier that determines if repetition should stop
     */
    public void repeatCancelable(int times, IntConsumer action, Supplier<Boolean> cancel) {
        Conditionals.ifTrueThrow(action == null || cancel == null, NullPointerException::new);
        Conditionals.ifTrueThrow(times < 0, IllegalArgumentException::new);
        for (int i = 0; i < times && !cancel.get(); i++) action.accept(i);
    }

    /**
     * Asynchronously repeats the given action a number of times.
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     * @return a {@link CompletableFuture} that completes when repetition is done
     */
    @Contract("_, _ -> new")
    public @NotNull CompletableFuture<Void> repeatAsync(int times, IntConsumer action) {
        return CompletableFuture.runAsync(() -> repeat(times, action));
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
                rangeDescending(start, realEnd, step, action);
            } else {
                range(start, realEnd, step, action);
            }
        }
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull static LoopBuilder loop() {
        return new LoopBuilder();
    }
}
