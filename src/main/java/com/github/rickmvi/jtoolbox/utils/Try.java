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
package com.github.rickmvi.jtoolbox.utils;

import com.github.rickmvi.jtoolbox.debug.Logger;
import com.github.rickmvi.jtoolbox.utils.functional.ThrowingRunnable;
import com.github.rickmvi.jtoolbox.utils.functional.ThrowingSupplier;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A functional-style container representing the result of an operation that may succeed or fail.
 * <p>
 * This class encapsulates either a successfully computed value or a failure (exception),
 * enabling safer and more expressive error handling without relying on traditional try-catch blocks.
 * It provides a fluent API for mapping, recovering, and handling results in a functional style.
 * </p>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Creating a Try from a Supplier
 * Try<Integer> result = Try.of(() -> 10 / 2);
 *
 * // Handling success and failure
 * result.onSuccess(value -> System.out.println("Success: " + value))
 *       .onFailure(e -> System.err.println("Failed: " + e.getMessage()));
 *
 * // Recovering from failure
 * Try<Integer> recovered = result.recover(e -> 0);
 *
 * // Mapping the value
 * Try<String> mapped = result.map(Object::toString);
 *
 * // Flattening nested Try
 * Try<Integer> flat = result.flatMap(v -> Try.of(() -> v * 2));
 *
 * // Getting value or throwing
 * int value = result.orElse(0);
 * }</pre>
 *
 * <h2>Core Methods:</h2>
 * <ul>
 *     <li>{@link #of(Supplier)} – Wraps a Supplier result into a Try.</li>
 *     <li>{@link #run(Runnable)} – Wraps a Runnable execution into a Try<Void>.</li>
 *     <li>{@link #isSuccess()} / {@link #isFailure()} – Check the state of the Try.</li>
 *     <li>{@link #orThrow()} / {@link #orElse(Object)} / {@link #orElseGet(Supplier)} – Access the value safely.</li>
 *     <li>{@link #map(Function)} / {@link #flatMap(Function)} – Transform the contained value functionally.</li>
 *     <li>{@link #recover(Function)} – Provide a fallback in case of failure.</li>
 *     <li>{@link #onSuccess(Consumer)} / {@link #onFailure(Consumer)} – Execute actions conditionally.</li>
 * </ul>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>All methods are designed to avoid null pointer exceptions.</li>
 *     <li>Exceptions are logged internally using {@link com.github.rickmvi.jtoolbox.debug.Logger}.</li>
 * </ul>
 *
 * @param <T> the type of the successfully computed value
 * @since 1.0
 * @author Rick M. Viana
 */
@Getter(value = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class Try<T> {

    private final T         value;
    private final Throwable failure;

    @Contract("_ -> new")
    public static <T> @NotNull Try<T> of(Supplier<T> supplier) {
        try {
            return new Try<>(supplier.get(), null);
        } catch (Throwable t) {
            Logger.error("Try.of() failed", t);
            return new Try<>(null, t);
        }
    }

    @Contract("_ -> new")
    public static @NotNull Try<Void> run(Runnable runnable) {
        try {
            runnable.run();
            return new Try<>(null, null);
        } catch (Throwable t) {
            Logger.error("Try.run() failed", t);
            return new Try<>(null, t);
        }
    }

    @Contract("_ -> new")
    public static <T> @NotNull Try<T> ofThrowing(ThrowingSupplier<T> supplier) {
        try {
            return new Try<>(supplier.get(), null);
        } catch (Throwable t) {
            Logger.error("Try.of() failed", t);
            return new Try<>(null, t);
        }
    }

    @Contract("_ -> new")
    public static @NotNull Try<Void> runThrowing(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return new Try<>(null, null);
        } catch (Throwable t) {
            Logger.error("Try.run() failed", t);
            return new Try<>(null, t);
        }
    }

    public boolean isSuccess() {
        return failure == null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public T orThrow() {
        if (isFailure()) throw new RuntimeException(failure);
        return value;
    }

    public T orElse(T fallback) {
        return isSuccess() ? value : fallback;
    }

    public T orElseGet(Supplier<T> fallback) {
        return isSuccess() ? value : fallback.get();
    }

    public <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionMapper) throws X {
        if (isFailure()) throw exceptionMapper.apply(failure);
        return value;
    }

    public <R> Try<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        if (isFailure()) return new Try<>(null, failure);
        try {
            return new Try<>(mapper.apply(value), null);
        } catch (Throwable t) {
            Logger.error("Try.map() failed", t);
            return new Try<>(null, t);
        }
    }

    public <R> Try<R> flatMap(Function<? super T, Try<R>> mapper) {
        Objects.requireNonNull(mapper);
        if (isFailure()) return new Try<>(null, failure);
        try {
            return mapper.apply(value);
        } catch (Throwable t) {
            Logger.error("Try.flatMap() failed", t);
            return new Try<>(null, t);
        }
    }

    public Try<T> recover(Function<Throwable, ? extends T> recovery) {
        if (isSuccess()) return this;
        try {
            return new Try<>(recovery.apply(failure), null);
        } catch (Throwable t) {
            Logger.error("Try.recover() failed", t);
            return new Try<>(null, t);
        }
    }

    public Try<T> onFailure(Consumer<Throwable> action) {
        if (isFailure()) action.accept(failure);
        return this;
    }

    public Try<T> onSuccess(Consumer<T> action) {
        if (isSuccess()) action.accept(value);
        return this;
    }

    @Override
    public String toString() {
        return isSuccess()
                ? "Try.Success[" + value + "]"
                : "Try.Failure[" + failure.getMessage() + "]";
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Try<?> aTry = (Try<?>) o;
        return Objects.equals(value, aTry.value) &&
                Objects.equals(failure, aTry.failure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, failure);
    }
}
