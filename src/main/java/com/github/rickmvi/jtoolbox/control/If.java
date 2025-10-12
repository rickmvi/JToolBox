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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility interface for conditional execution and functional-style flow control.
 * <p>
 * Provides a fluent API for executing {@link Runnable} or {@link Supplier}
 * operations based on a boolean condition. Supports conditional execution,
 * mapping, optional conversion, and exception handling.
 * <p>
 * The interface is divided into two main concepts:
 * <ul>
 *     <li>{@link Runner} – for executing {@link Runnable} based on conditions.</li>
 *     <li>{@link Suppliers} – for supplying values conditionally and handling results.</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Conditional runnable
 * If.runTrue(obj != null, () -> System.out.println("Object is not null")).run();
 *
 * // Runnable with else
 * If.runFalse(obj == null, () -> System.out.println("Object is null"))
 *     .orElse(() -> System.out.println("Object is not null"));
 *
 * // Conditional supplier
 * Integer value = If.supplyTrue(obj != null, () -> 10)
 *                   .map(i -> i * 2)
 *                   .value();
 *
 * // Supplier with fallback
 * Integer safeValue = If.supplyFalse(obj == null, () -> 5)
 *                       .orElse(() -> 0);
 *
 * // Optional conversion
 * Optional<String> maybe = If.optionalTrue(obj != null, () -> "Hello");
 *
 * // Throw exception on condition
 * If.trueThrow(obj == null, () -> new IllegalStateException("Object cannot be null"));
 * }</pre>
 *
 * <p>
 * All methods are static and designed to be used without instantiating the interface.
 * The API encourages safe, readable, and concise handling of conditional logic.
 * </p>
 *
 * <p>
 * Null values in suppliers are handled gracefully: they return {@code null} or empty {@link Optional}.
 * Exceptions can be rethrown or logged, according to the method used.
 * </p>
 *
 * @author Rick M. Viana
 * @since 1.2
 */
public interface If {

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull If.Runner isTrue(boolean condition, Runnable action) {
        return new Runner(condition, action, true);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull If.Runner isFalse(boolean condition, Runnable action) {
        return new Runner(condition, action, false);
    }

    @Contract("_ -> new")
    static @NotNull If.Runner when(boolean condition) {
        return new Runner(condition, true);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Runner {
        private final boolean  condition;
        private final Runnable action;
        private final boolean  runOnTrue;

        private Runner(boolean condition, boolean runOnTrue) {
            this(condition, null, runOnTrue);
        }

        public void orElse(Runnable elseAction) {
            if (evaluateRunCondition()) {
                action.run();
                return;
            }
            elseAction.run();
        }

        @Contract(pure = true)
        private boolean evaluateRunCondition() {
            return (runOnTrue && this.condition) || (!runOnTrue && !this.condition);
        }

        public void run() {
            if (evaluateRunCondition()) action.run();
        }

        public void orElseThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
            if (evaluateRunCondition()) {
                action.run();
                return;
            }
            throw exceptionSupplier.get();
        }

        public Runner apply(Runnable action) {
            return new Runner(this.condition, action, runOnTrue);
        }

        public Runner not() {
            return new Runner(this.condition, action, !runOnTrue);
        }

        public Runner or(boolean condition) {
            return new Runner(this.condition || condition, action, runOnTrue);
        }

        public Runner and(boolean condition) {
            return new Runner(this.condition && condition, action, runOnTrue);
        }

    }

    static <T> @NotNull Suppliers<T> supplyTrue(boolean condition, Supplier<T> supplier) {
        return new Suppliers<>(condition, supplier, true);
    }
    
    static <T> @NotNull Suppliers<T> supplyFalse(boolean condition, Supplier<T> supplier) {
        return new Suppliers<>(condition, supplier, false);
    }

    static <T> @NotNull Suppliers<T> whens(boolean condition) {
        return new Suppliers<>(condition, true);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Suppliers<T> {
        private final boolean     condition;
        private final Supplier<T> supplier;
        private final boolean     runOnTrue;

        private Suppliers(boolean condition, boolean runOnTrue) {
            this(condition, null, runOnTrue);
        }

        public T value() {
            if (evaluateCondition()) return supplier.get();
            return null;
        }

        @Contract(pure = true)
        private boolean evaluateCondition() {
            return (runOnTrue && this.condition) || (!runOnTrue && !this.condition);
        }

        public T orElseGet(Supplier<T> elseSupplier) {
            if (evaluateCondition()) return supplier.get();
            return elseSupplier.get();
        }

        public T orElseThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
            if (evaluateCondition()) return supplier.get();
            throw exceptionSupplier.get();
        }

        public <R> Suppliers<R> map(Function<T, R> mapper) {
            return new Suppliers<>(this.condition, () -> mapper.apply(supplier.get()), runOnTrue);
        }

        public <R> Suppliers<R> flatMap(Function<T, Suppliers<R>> mapper) {
            if (evaluateCondition()) {
                return mapper.apply(supplier.get());
            }
            return new Suppliers<>(false, null, false);
        }

        public Optional<T> toOptional() {
            if (this.condition == runOnTrue) return Optional.ofNullable(supplier.get());
            return Optional.empty();
        }

        public Suppliers<T> apply(Supplier<T> supplier) {
            return new Suppliers<>(this.condition, supplier, runOnTrue);
        }

        public Suppliers<T> not() {
            return new Suppliers<>(this.condition, supplier, !runOnTrue);
        }

        public Suppliers<T> or(boolean condition) {
            return new Suppliers<>(this.condition || condition, supplier, runOnTrue);
        }

        public Suppliers<T> and(boolean condition) {
            return new Suppliers<>(this.condition && condition, supplier, runOnTrue);
        }

    }

    @Contract("_, _ -> !null")
    static <R> Optional<R> optional(boolean condition, Supplier<R> whenTrue) {
        if (condition) return Optional.ofNullable(whenTrue.get());
        return Optional.empty();
    }

    static String MessageOrDefault(Supplier<String> messageSupplier, String defaultMessage) {
        return Objects.isNull(messageSupplier) ? defaultMessage : messageSupplier.get();
    }

    @Contract("true, _ -> fail")
    static void Throws(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }
}
