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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.Optional;

@lombok.experimental.UtilityClass
public class Conditionals {
    /**
     * Executes the given action if the condition is true.
     *
     * @param condition the condition to evaluate
     * @param action    the action to run if the condition is true
     */
    public static void ifTrue(boolean condition, Runnable action) {
        if (condition) action.run();
    }

    /**
     * Executes the given action if the condition is false.
     *
     * @param condition the condition to evaluate
     * @param action    the action to run if the condition is false
     */
    public static void ifFalse(boolean condition, Runnable action) {
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
    @Contract("false, _ -> null")
    public static <T> T ifTrueReturn(boolean condition, Supplier<T> supplier) {
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
    @Contract("true, _ -> null")
    public static <T> T ifFalseReturn(boolean condition, Supplier<T> supplier) {
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
    public static <R> R supplyIfTrueElse(boolean condition, @NotNull Supplier<R> whenTrue, @NotNull Supplier<R> orElse) {
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
    public static <R> R supplyIfFalseElse(boolean condition, @NotNull Supplier<R> whenFalse, @NotNull Supplier<R> orElse) {
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
    @Contract("_, _ -> !null")
    public static <R> Optional<R> optionalIfTrue(boolean condition, @NotNull Supplier<R> whenTrue) {
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
    @Contract("_, _ -> !null")
    public static <R> Optional<R> optionalIfFalse(boolean condition, @NotNull Supplier<R> whenFalse) {
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
    public static <T> T supplyByCondition(boolean condition, Supplier<T> trueSupplier, Supplier<T> falseSupplier) {
        return condition ? trueSupplier.get() : falseSupplier.get();
    }

    /**
     * Throws a RuntimeException provided by the exceptionSupplier if the given condition is true.
     *
     * @param condition a boolean value that determines whether the exception should be thrown
     * @param exceptionSupplier a Supplier that provides the RuntimeException to be thrown
     */
    @Contract("true, _ -> fail")
    public static void ifTrueThrow(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }
}
