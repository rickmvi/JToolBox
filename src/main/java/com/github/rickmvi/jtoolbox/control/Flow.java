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

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.Map;

/**
 * Utility class that provides fluent and simplified alternatives to common control flow structures
 * such as {@code if}, {@code switch}, {@code for}, {@code while}, and {@code do-while}.
 * <p>
 * This class is part of the Console API - JToolbox library and aims to make control structures
 * more expressive, chainable, and reusable. It also provides asynchronous variants for
 * selected control flows using {@link CompletableFuture}.
 * </p>
 *
 * <p><b>Features include:</b></p>
 * <ul>
 *   <li>Conditional execution using {@code ifTrue} and {@code ifFalse}</li>
 *   <li>Map-based {@code switch} and {@code switchReturn}</li>
 *   <li>Repeating blocks with support for ascending and descending loops</li>
 *   <li>{@code while} and {@code do-while} constructs</li>
 *   <li>Asynchronous versions of repeat and while loops</li>
 *   <li>Cancelable loops via {@link Supplier} conditions</li>
 * </ul>
 *
 * <p>All methods are static and should be used statically as part of the utility pattern.</p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class Flow {

    /**
     * Executes the given action if the condition is true.
     *
     * @param condition the condition to evaluate
     * @param action    the action to run if the condition is true
     */
    public void ifTrue(boolean condition, Runnable action) {
        if (condition) action.run();
    }

    /**
     * Executes the given action if the condition is false.
     *
     * @param condition the condition to evaluate
     * @param action    the action to run if the condition is false
     */
    public void ifFalse(boolean condition, Runnable action) {
        if (!condition) action.run();
    }

    /**
     * Executes the given supplier and returns its result if the condition is true,
     * otherwise returns null.
     *
     * @param condition the condition to evaluate
     * @param supplier  the supplier to execute if true
     * @param <T>       the return type
     * @return the supplier result if true, otherwise null
     */
    public <T> T ifTrueReturn(boolean condition, Supplier<T> supplier) {
        return condition ? supplier.get() : null;
    }

    /**
     * Executes the given supplier and returns its result if the condition is false,
     * otherwise returns null.
     *
     * @param condition the condition to evaluate
     * @param supplier  the supplier to execute if false
     * @param <T>       the return type
     * @return the supplier result if false, otherwise null
     */
    public <T> T ifFalseReturn(boolean condition, Supplier<T> supplier) {
        return !condition ? supplier.get() : null;
    }

    /**
     * Executes {@code whenTrue} if {@code condition} is true, otherwise executes {@code orElse},
     * returning the produced value.
     *
     * @param condition the condition to evaluate
     * @param whenTrue supplier executed when the condition is true
     * @param orElse supplier executed when the condition is false
     * @param <R> return type
     * @return the value produced by the selected supplier
     */
    public <R> R supplyIfTrueElse(boolean condition, @NotNull Supplier<R> whenTrue, @NotNull Supplier<R> orElse) {
        return condition ? whenTrue.get() : orElse.get();
    }

    /**
     * Executes {@code whenFalse} if {@code condition} is false, otherwise executes {@code orElse},
     * returning the produced value.
     *
     * @param condition the condition to evaluate
     * @param whenFalse supplier executed when the condition is false
     * @param orElse supplier executed when the condition is true
     * @param <R> return type
     * @return the value produced by the selected supplier
     */
    public <R> R supplyIfFalseElse(boolean condition, @NotNull Supplier<R> whenFalse, @NotNull Supplier<R> orElse) {
        return !condition ? whenFalse.get() : orElse.get();
    }

    /**
     * Executes {@code whenTrue} if {@code condition} is true and returns {@link Optional#ofNullable(Object)}
     * of its result; otherwise returns {@link Optional#empty()}.
     *
     * @param condition the condition to evaluate
     * @param whenTrue supplier executed when the condition is true
     * @param <R> return type
     * @return optional containing the value if executed, otherwise empty
     */
    public <R> Optional<R> optionalIfTrue(boolean condition, @NotNull Supplier<R> whenTrue) {
        return condition ? Optional.ofNullable(whenTrue.get()) : Optional.empty();
    }

    /**
     * Executes {@code whenFalse} if {@code condition} is false and returns {@link Optional#ofNullable(Object)}
     * of its result; otherwise returns {@link Optional#empty()}.
     *
     * @param condition the condition to evaluate
     * @param whenFalse supplier executed when the condition is false
     * @param <R> return type
     * @return optional containing the value if executed, otherwise empty
     */
    public <R> Optional<R> optionalIfFalse(boolean condition, @NotNull Supplier<R> whenFalse) {
        return !condition ? Optional.ofNullable(whenFalse.get()) : Optional.empty();
    }

    /**
     * Executes the given supplier and returns one of two possible values depending on the condition.
     *
     * @param condition the condition to evaluate
     * @param trueSupplier  the supplier executed if true
     * @param falseSupplier the supplier executed if false
     * @param <T>       the return type
     * @return the supplier result depending on condition
     */
    public <T> T supplyByCondition(boolean condition, Supplier<T> trueSupplier, Supplier<T> falseSupplier) {
        return condition ? trueSupplier.get() : falseSupplier.get();
    }

    /**
     * Throws a RuntimeException provided by the exceptionSupplier if the given condition is true.
     *
     * @param condition a boolean value that determines whether the exception should be thrown
     * @param exceptionSupplier a Supplier that provides the RuntimeException to be thrown
     */
    public void ifTrueThrow(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }

    /**
     * Performs a switch-like operation using a map of cases. Executes the matching case or the default if not found.
     *
     * @param value       the input value to match
     * @param cases       a map of values to actions
     * @param defaultCase the action to run if no match is found
     * @param <T>         the type of the input value
     */
    public <T> void switchOn(T value, @NotNull Map<T, Runnable> cases, Runnable defaultCase) {
        cases.getOrDefault(value, defaultCase).run();
    }

    /**
     * Performs a switch-like operation using a map of cases and returns a result from the matched case.
     *
     * @param value       the input value to match
     * @param cases       a map of values to result suppliers
     * @param defaultCase the supplier to run if no match is found
     * @param <T>         the type of the input value
     * @param <R>         the return type
     * @return the result from the matching case or the default supplier
     */
    public <T, R> R switchReturn(T value, @NotNull Map<T, Supplier<R>> cases, Supplier<R> defaultCase) {
        return cases.getOrDefault(value, defaultCase).get();
    }

    /**
     * Repeats the given action a number of times in ascending order (0 to times - 1).
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     */
    public void repeat(int times, IntConsumer action) {
        for (int i = 0; i < times; i++) action.accept(i);
    }

    /**
     * Repeats the given action a number of times in descending order (times - 1 to 0).
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     */
    public void repeatDescending(int times, IntConsumer action) {
        for (int i = times - 1; i >= 0; i--) action.accept(i);
    }

    /**
     * Repeatedly executes the action while the condition returns true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     */
    public void whileTrue(@NotNull BooleanSupplier condition, Runnable action) {
        while (condition.getAsBoolean()) action.run();
    }

    /**
     * Executes the action once and continues as long as the condition returns true (do-while loop).
     *
     * @param condition the condition to evaluate after each iteration
     * @param action    the action to execute
     */
    public void doWhile(@NotNull BooleanSupplier condition, @NotNull Runnable action) {
        do action.run(); while (condition.getAsBoolean());
    }

    /**
     * Executes an action at least once and keeps repeating it while the
     * condition over the last result remains true. Returns the last result.
     *
     * <p>Typical usage: read input until it becomes valid and get the final value.</p>
     *
     * <pre>{@code
     * int option = Flow.doWhile(
     *     () -> {
     *         Out.printFormatted("Enter 0 for {} or 1 for {}: ", "exit", "continue");
     *         return ScannerUtils.nextInt();
     *     },
     *     opt -> opt != 0 && opt != 1 // continue while invalid
     * );
     * }</pre>
     *
     * @param action the action to execute each iteration, producing a result
     * @param continueCondition the condition that, given the last result, determines whether to continue
     * @param <T> the result type
     * @return the last result produced by {@code action}
     */
    public <T> T doWhile(@NotNull Supplier<T> action, @NotNull Predicate<T> continueCondition) {
        T result;
        do {
            result = action.get();
        } while (continueCondition.test(result));
        return result;
    }

    /**
     * Executes the action for each index from start (inclusive) to end (exclusive).
     *
     * @param start  starting index (inclusive)
     * @param end    ending index (exclusive)
     * @param action the action to perform, receiving the current index
     */
    public void forRange(int start, int end, IntConsumer action) {
        for (int i = start; i < end; i++) action.accept(i);
    }

    /**
     * Executes the action for each index from start (inclusive) down to end (inclusive).
     *
     * @param start  starting index (inclusive)
     * @param end    ending index (inclusive and less than or equal to start)
     * @param action the action to perform, receiving the current index
     */
    public void forRangeDescending(int start, int end, IntConsumer action) {
        for (int i = start; i >= end; i--) action.accept(i);
    }

    /**
     * Asynchronously repeats the given action a number of times.
     *
     * @param times  number of repetitions
     * @param action the action to perform, receiving the current index
     * @return a {@link CompletableFuture} that completes when repetition is done
     */
    public CompletableFuture<Void> repeatAsync(int times, IntConsumer action) {
        return CompletableFuture.runAsync(() -> repeat(times, action));
    }

    /**
     * Asynchronously executes the action while the condition is true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @return a {@link CompletableFuture} that completes when the loop ends
     */
    public CompletableFuture<Void> whileTrueAsync(BooleanSupplier condition, Runnable action) {
        return CompletableFuture.runAsync(() -> whileTrue(condition, action));
    }

    /**
     * Asynchronously performs a switch-like operation and returns the result from the matching case.
     *
     * @param value       the input value to match
     * @param cases       a map of values to result suppliers
     * @param defaultCase the supplier to run if no match is found
     * @param <T>         the type of the input and return values
     * @return a {@link CompletableFuture} with the result of the matched case or default
     */
    public <T> CompletableFuture<T> switchReturnAsync(T value, Map<T, Supplier<T>> cases, Supplier<T> defaultCase) {
        return CompletableFuture.supplyAsync(() -> switchReturn(value, cases, defaultCase));
    }

    /**
     * Repeats the given action a number of times but allows cancellation via a boolean supplier.
     *
     * @param times   number of repetitions
     * @param action  the action to perform, receiving the current index
     * @param cancel  the supplier that determines if repetition should stop
     */
    public void repeatCancelable(int times, IntConsumer action, Supplier<Boolean> cancel) {
        for (int i = 0; i < times && !cancel.get(); i++) action.accept(i);
    }

    /**
     * Repeatedly executes the action while the condition is true and not canceled.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @param cancel    the supplier that determines if execution should be canceled
     */
    public void whileTrueCancelable(@NotNull BooleanSupplier condition, Runnable action, Supplier<Boolean> cancel) {
        while (condition.getAsBoolean() && !cancel.get()) action.run();
    }
}
