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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Try interface serves as a utility interface providing functional methods to
 * handle exceptions in a safe and controlled manner while working with functional programming constructs.
 * It includes methods for executing functions, suppliers, and runnable with built-in exception handling,
 * as well as ways to recover or handle exceptions when they occur.
 * <p>
 * It provides functionality such as safe conversion, retry mechanisms, fallbacks,
 * and safe wrappers for functional interfaces such as {@code Function}, {@code Consumer}, and {@code Runnable}.
 *
 * @param <T> The type of result this utility interface operates on.
 *
 * @since 1.0
 * @author Rick M. Viana
 */
@SuppressWarnings("unused")
public interface Try<T> {

    /**
     * Converts a given value using the specified converter function and returns the result
     * wrapped in an {@link Optional}. If the value is {@code null}, or the conversion fails
     * due to an exception, an empty {@link Optional} is returned.
     *
     * @param <T> the type of the input value
     * @param <R> the type of the result produced by the converter
     * @param value the value to be converted; may be {@code null}
     * @param converter the function used to convert the input value to the desired type;
     *                  must not be {@code null}
     * @return an {@link Optional} describing the converted value; {@link Optional#empty()}
     *         if the input value is {@code null} or if an exception occurs during conversion
     * @throws NullPointerException if the converter function is {@code null}
     */
    @Contract("null, _ -> !null")
    static <T, R> Optional<R> convert(T value, Function<T, R> converter) {
        if (Objects.isNull(value)) return Optional.empty();
        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            Logger.error("Failed to convert value: {} to type: {} ", e, value, converter.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    /**
     * Executes the given {@link Runnable} and handles any exceptions that occur during its execution.
     * If an exception is thrown, it is logged along with the class name of the {@link Runnable}.
     *
     * @param <T> the type parameter, not used directly in this method but may be inferred from context
     * @param runnable the {@link Runnable} to execute; must not be {@code null}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    static <T> void run(Runnable runnable) {
        Objects.requireNonNull(runnable);
        try {
            runnable.run();
        } catch (Throwable t) {
            Logger.error("An error occurred during execution of runnable '{}'", t, runnable.getClass().getSimpleName());
        }
    }

    /**
     * Wraps a given {@link Runnable} in a new {@link Runnable} that executes it within a
     * try-catch block. If an exception occurs during execution, the exception is logged
     * along with the class name of the provided {@link Runnable}.
     *
     * @param <T> the type parameter, not used directly in this method but may be inferred
     *            from the context
     * @param runnable the {@link Runnable} to be executed; must not be {@code null}
     * @return a new {@link Runnable} that handles exceptions when executing the
     *         specified {@link Runnable}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    @Contract(pure = true)
    static <T> @NotNull Runnable runRunnable(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                Logger.error("Failed to run runnable: {}", e, runnable.getClass().getSimpleName());
            }
        };
    }

    /**
     * Executes the given {@link Supplier} and handles any exceptions
     * that occur during its execution. If an exception is thrown, it
     * is logged along with the class name of the {@link Supplier}.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param runnable the {@link Supplier} to execute; must not be {@code null}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    static <T> void runVoid(Supplier<T> runnable) {
        try {
            runnable.get();
        } catch (Throwable t) {
            Logger.error("An error occurred during execution of runnable '{}'", t, runnable.getClass().getSimpleName());
        }
    }

    /**
     * Attempts to execute a given {@link Supplier} and returns its result
     * wrapped in an {@link Optional}. If the supplier throws any exception,
     * the exception is logged, and an empty {@link Optional} is returned.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param supplier the {@link Supplier} to be executed; must not be {@code null}
     * @return an {@link Optional} describing the result of the supplier's execution;
     *         {@link Optional#empty()} if an exception occurs or the supplier returns {@code null}
     * @throws NullPointerException if the {@code supplier} is {@code null}
     */
    static <T> Optional<T> attempt(Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Throwable t) {
            Logger.error("Failed to execute supplier: {}", t, supplier.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    /**
     * Attempts to execute the provided supplier and returns its result.
     * If the supplier throws any exception, it logs the error and returns
     * the specified fallback value.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param supplier the {@link Supplier} to be executed; must not be {@code null}
     * @param fallback the value to return if the supplier execution fails due to an exception
     * @return the result of the supplier execution if successful, or the fallback value if an exception occurs
     * @throws NullPointerException if the {@code supplier} is {@code null}
     */
    static <T> T attemptOrElse(Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            Logger.error("Failed to execute supplier: {}", t, supplier.getClass().getSimpleName());
            return fallback;
        }
    }

    /**
     * Wraps a given {@link Function} in another function that attempts to safely execute
     * the original function. If the execution of the function throws an exception, the
     * provided fallback value is returned instead. The exception is logged for debugging
     * purposes.
     *
     * @param <T> the type of the argument to the function
     * @param <R> the type of the result of the function
     * @param function the {@link Function} to wrap; must not be {@code null}
     * @param fallback the value to return if an exception occurs during the function execution
     * @return a new {@link Function} that safely applies the provided function
     *         and returns the fallback value in case of an exception
     * @throws NullPointerException if the {@code function} is {@code null}
     */
    @Contract(pure = true)
    static <T, R> @NotNull Function<T, R> apply(Function<T, R> function, R fallback) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                Logger.error("Failed to apply function: {}", e, function.getClass().getSimpleName());
                return fallback;
            }
        };
    }

    /**
     * Executes the provided {@link Runnable} and handles any exceptions that occur during its execution.
     * If an exception is thrown, it is logged along with the provided message and optional arguments.
     *
     * @param runnable the {@link Runnable} to be executed; must not be {@code null}.
     * @param message the message to log in case of an exception; must not be {@code null}.
     * @param args optional arguments to format the log message; may be {@code null}.
     * @throws NullPointerException if the {@code runnable} or {@code message} is {@code null}.
     */
    static void runWithMessage(Runnable runnable, String message, Object @Nullable ... args) {
        try {
            runnable.run();
        } catch (Throwable t) {
            Logger.error("{} - Exception: {}", t, message, args);
        }
    }

    /**
     * Repeatedly attempts to execute the given {@link Runnable} for a specified number of attempts.
     * If the {@link Runnable} throws an exception during execution, the exception is logged along with
     * the attempt number and the total number of attempts. The process stops and returns as soon as
     * the {@link Runnable} executes successfully without throwing any exceptions.
     *
     * @param runnable the {@link Runnable} to be executed; must not be {@code null}.
     * @param attempts the number of times to attempt to execute the {@link Runnable}.
     * @throws NullPointerException if the {@code runnable} is {@code null}.
     */
    static void retry(Runnable runnable, int attempts) {
        Objects.requireNonNull(runnable);
        for (int i = 0; i < attempts; i++) {
            try {
                runnable.run();
                return;
            } catch (Throwable t) {
                Logger.error("Attempt {}/{} failed: {}", t, i + 1, attempts, runnable.getClass().getSimpleName());
            }
        }
    }

    /**
     * Wraps an existing {@link Consumer} in a new {@link Consumer} that ensures any exceptions
     * thrown during the execution of the original consumer are caught and logged. The exception
     * is logged along with the class name of the provided {@link Consumer}.
     *
     * @param <T> the type of the input*/
    @Contract(pure = true)
    static <T> @NotNull Consumer<T> consumer(Consumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                Logger.error("Failed to consume: {}", e, consumer.getClass().getSimpleName());
            }
        };
    }

    /**
     * Attempts to execute a given {@link Supplier} and returns its result.
     * If the supplier throws an exception, the exception is mapped to a {@link RuntimeException}
     * using the provided {@link Function} and subsequently thrown.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param supplier the {@link Supplier} to be executed; must not be {@code null}
     * @param exceptionMapper a {@link Function} that maps the thrown exception
     *                        to a {@link RuntimeException}; must not be {@code null}
     * @return the result of the supplier execution if successful
     * @throws NullPointerException if the {@code supplier} or {@code exceptionMapper} is {@code null}
     * @throws RuntimeException if the supplier throws an exception that is mapped by the {@code exceptionMapper}
     */
    static <T> T attemptOrThrow(Supplier<T> supplier, Function<Throwable, RuntimeException> exceptionMapper) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            throw exceptionMapper.apply(t);
        }
    }

}
