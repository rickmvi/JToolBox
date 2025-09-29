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
package com.github.rickmvi.jtoolbox.control.ifs;

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
 *     <li>{@link FluentRunner} – for executing {@link Runnable} based on conditions.</li>
 *     <li>{@link FluentSupplier} – for supplying values conditionally and handling results.</li>
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
 * Exceptions can be rethrown or logged according to the method used.
 * </p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
public interface If {

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull FluentRunner runTrue(boolean condition, Runnable action) {
        return new FluentRunner(condition, action, true);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull FluentRunner runFalse(boolean condition, Runnable action) {
        return new FluentRunner(condition, action, false);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class FluentRunner {
        private final boolean  condition;
        private final Runnable action;
        private final boolean  runOnTrue;

        public void orElse(Runnable elseAction) {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) {
                action.run();
                return;
            }
            elseAction.run();
        }

        public void run() {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) action.run();
        }

        public void orElseThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) {
                action.run();
                return;
            }
            throw exceptionSupplier.get();
        }

        public FluentRunner not() {
            return new FluentRunner(condition, action, !runOnTrue);
        }

        public void ifElse(Runnable elseAction) {
            orElse(elseAction);
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull FluentSupplier<T> supplyTrue(boolean condition, Supplier<T> supplier) {
        return new FluentSupplier<>(condition, supplier, true);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull FluentSupplier<T> supplyFalse(boolean condition, Supplier<T> supplier) {
        return new FluentSupplier<>(condition, supplier, false);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class FluentSupplier<T> {
        private final boolean     condition;
        private final Supplier<T> supplier;
        private final boolean     runOnTrue;

        public T orElse(Supplier<T> elseSupplier) {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) return supplier.get();
            return elseSupplier.get();
        }

        public T value() {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) return supplier.get();
            return null;
        }

        public T orElseThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
            if ((runOnTrue && condition) || (!runOnTrue && !condition)) return supplier.get();
            throw exceptionSupplier.get();
        }

        public <R> FluentSupplier<R> map(Function<T, R> mapper) {
            return new FluentSupplier<>(condition, () -> mapper.apply(supplier.get()), runOnTrue);
        }

        public Optional<T> toOptional() {
            if (condition == runOnTrue) return Optional.ofNullable(supplier.get());
            return Optional.empty();
        }

        public T ifElse(Supplier<T> elseSupplier) {
            return orElse(elseSupplier);
        }
    }

    @Contract("_, _ -> !null")
    static <R> Optional<R> optionalTrue(boolean condition, Supplier<R> whenTrue) {
        if (condition) return Optional.ofNullable(whenTrue.get());
        return Optional.empty();
    }

    @Contract("_, _ -> !null")
    static <R> Optional<R> optionalFalse(boolean condition, Supplier<R> whenFalse) {
        if (!condition) return Optional.ofNullable(whenFalse.get());
        return Optional.empty();
    }

    static String messageOrDefault(Supplier<String> messageSupplier, String defaultMessage) {
        return Objects.isNull(messageSupplier) ? defaultMessage : messageSupplier.get();
    }

    @Contract("true, _ -> fail")
    static void trueThrow(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }
}
