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
import java.util.function.*;

/**
 * Utility interface providing safe execution of functional interfaces with
 * built-in exception handling and logging.
 * <p>
 * This interface is designed to wrap operations like {@link Runnable}, {@link Supplier},
 * {@link Function}, {@link IntConsumer}, and {@link Consumer} to handle exceptions gracefully,
 * log errors using {@link com.github.rickmvi.jtoolbox.debug.Logger}, and optionally provide
 * fallback values or containers like {@link Optional}.
 * </p>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Run a Runnable safely
 * SafeExecutor.run(() -> doSomething());
 *
 * // Run a Supplier and get the result, or fallback on error
 * int result = SafeExecutor.attemptOrElse(() -> riskyOperation(), 0);
 *
 * // Wrap a function safely
 * Function<String, Integer> safeParse = SafeExecutor.apply(Integer::parseInt, -1);
 *
 * // Attempt with fallback supplier
 * String value = SafeExecutor.attemptOrElseGet(() -> readFile(), () -> "default", "Failed to read file");
 *
 * // Retry a Runnable multiple times
 * SafeExecutor.retry(() -> unreliableOperation(), 3);
 *
 * // Convert a value safely
 * Optional<Double> number = SafeExecutor.convert("123.45", Double::parseDouble);
 * }</pre>
 *
 * <h2>Key Methods:</h2>
 * <ul>
 *     <li>{@link #run(Runnable)} – Execute a Runnable safely with logging.</li>
 *     <li>{@link #runRunnable(Runnable)} – Wraps a Runnable for safe execution.</li>
 *     <li>{@link #runVoid(Supplier)} – Execute a Supplier returning a value safely.</li>
 *     <li>{@link #runIntConsumer(IntConsumer, int)} – Execute an IntConsumer safely.</li>
 *     <li>{@link #attempt(Supplier)} – Execute a Supplier and return an Optional result.</li>
 *     <li>{@link #attemptOrElse(Supplier, Object)} – Execute a Supplier with a fallback value.</li>
 *     <li>{@link #attemptOrElseGet(Supplier, Supplier, String, Object...)} – Execute a Supplier with a fallback Supplier and log message.</li>
 *     <li>{@link #apply(Function, Object)} – Wrap a Function with fallback value.</li>
 *     <li>{@link #applyTry(Function)} – Wrap a Function to return a {@link com.github.rickmvi.jtoolbox.utils.Try}.</li>
 *     <li>{@link #runWithMessage(Runnable, String, Object...)} – Run a Runnable with custom error message.</li>
 *     <li>{@link #retry(Runnable, int)} – Retry a Runnable multiple times with logging.</li>
 *     <li>{@link #consumer(Consumer)} – Wrap a Consumer safely.</li>
 *     <li>{@link #attemptOrThrow(Supplier, Function)} – Execute a Supplier and map exceptions to RuntimeException.</li>
 *     <li>{@link #convert(Object, Function)} – Convert a value safely to another type.</li>
 * </ul>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>All methods throw {@link NullPointerException} if required arguments are null.</li>
 *     <li>Exceptions during execution are logged internally with {@link com.github.rickmvi.jtoolbox.debug.Logger}.</li>
 *     <li>Designed to work with Java functional interfaces in a fluent and safe style.</li>
 * </ul>
 *
 * @param <T> a general type parameter for methods handling values or computations
 * @since 1.1
 * @author Rick M. Viana
 */
@SuppressWarnings("unused")
public interface SafeExecutor<T> {

    /**
     * Executes the given {@link Runnable}, handling any exceptions that occur during its execution.
     * If an exception is thrown, it is logged along with the class name of the provided {@link Runnable}.
     *
     * @param <T> the type parameter, not directly used in this method but may be inferred
     *            from the context
     * @param runnable the {@link Runnable} to be executed; must not be {@code null}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    static <T> void run(Runnable runnable) {
        Objects.requireNonNull(runnable);
        Try.run(runnable)
                .onFailure(t -> Logger.error("Failed to execute runnable: {}",
                        t, runnable.getClass().getSimpleName())).orThrow();
    }

    /**
     * Wraps a given {@link Runnable} with enhanced error handling and logging capabilities,
     * returning a new {@link Runnable} that executes the provided {@link Runnable}.
     * If an exception occurs during execution, it is logged with the class name of the
     * provided {@link Runnable} and rethrown.
     *
     * @param <T> the type parameter, not directly used in this method but may be inferred
     *            from the context
     * @param runnable the {@link Runnable} to be wrapped and executed; must not be {@code null}
     * @return a new {@link Runnable} that wraps the provided {@link Runnable} with additional
     *         error handling and logging
     * @throws NullPointerException if the provided {@code runnable} is {@code null}
     */
    @Contract(pure = true)
    static <T> @NotNull Runnable runRunnable(Runnable runnable) {
        return () -> {
            Try.run(runnable)
                    .onFailure(t -> Logger.error(
                            "Failed to run runnable: {}",
                            t, 
                            runnable.getClass().getSimpleName()))
                    .orThrow();
        };
    }

    /**
     * Executes the given {@link Supplier}, handling any exceptions that occur during its execution.
     * If an exception is thrown, it is logged along with the class name of the provided {@link Supplier}.
     * <p>
     * This method requires the {@code runnable} parameter to be non-null. An exception thrown during
     * execution is logged, and the exception is rethrown after logging.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}, not directly used in this method
     *            but inferred from the context
     * @param runnable the {@link Supplier} to execute; must not be {@code null}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    static <T> void runVoid(Supplier<T> runnable) {
        Objects.requireNonNull(runnable);
        
         Try.of(runnable)
                .onFailure(t -> Logger.error(
                        "An error occurred during execution of runnable '{}'",
                        t,
                        runnable.getClass().getSimpleName()))
                .orThrow();
    }

    /**
     * Executes the given {@link IntConsumer}, providing it with a specified integer value,
     * while handling any exceptions that occur during its execution. If an exception
     * is thrown, it is logged along with the class name of the provided {@link IntConsumer},
     * and then the exception is rethrown.
     *
     * @param <T> a type parameter, not directly used in this method but inferred from context
     * @param runnable the {@link IntConsumer} to be executed; must not be {@code null}
     * @param value the integer value to be passed to the {@link IntConsumer}
     * @throws NullPointerException if the {@code runnable} is {@code null}
     */
    static <T> void runIntConsumer(IntConsumer runnable, int value) {
        Objects.requireNonNull(runnable);
        
        Try.run(() -> runnable.accept(value))
                .onFailure(t -> Logger.error(
                        "An error occurred during execution of runnable '{}'", 
                        t, 
                        runnable.getClass().getSimpleName()))
                .orThrow();
    }

    /**
     * Attempts to execute a given {@link Supplier} and returns its result wrapped in an {@link Optional}.
     * If the supplier throws an exception during execution, the exception is logged, and an empty
     * {@link Optional} is returned.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param supplier the {@link Supplier} to be executed; must not be {@code null}
     * @return an {@link Optional} containing the result of the supplier execution if successful,
     *         or an empty {@link Optional} if an exception occurs
     * @throws NullPointerException if the {@code supplier} is {@code null}
     */
    static <T> Optional<T> attempt(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);

        return Try.of(supplier)
                .onFailure(t -> Logger.error(
                        "Failed to execute supplier: {}",
                        t,
                        supplier.getClass().getSimpleName()))
                .toOptional();
    }

    /**
     * Attempts to execute the given {@link Supplier} and returns its result. If an exception
     * occurs during the execution of the supplier, the exception is logged and the provided
     * fallback value is returned.
     *
     * @param <T> the type of the result supplied by the {@link Supplier}
     * @param supplier the {@link Supplier} to be executed; must not be {@code null}
     * @param fallback the value to return if an exception occurs while executing the supplier
     * @return the result of the {@link Supplier} execution if successful, or the provided
     *         fallback value if an exception occurs
     * @throws NullPointerException if the {@code supplier} is {@code null}
     */
    static <T> T attemptOrElse(Supplier<T> supplier, T fallback) {
        Objects.requireNonNull(supplier);

        return Try.of(supplier)
                .onFailure(t -> Logger.error(
                        "Failed to execute supplier: {}",
                        t,
                        supplier.getClass().getSimpleName()))
                .orElse(fallback);
    }

    /**
     * Attempts to obtain a value using the primary supplier. If the supplier fails,
     * logs the error message along with any provided arguments and attempts to obtain
     * a fallback value using the fallback supplier.
     *
     * @param <T> the type of the value to be retrieved
     * @param supplier the primary supplier to provide the value
     * @param fallback the fallback supplier to provide a value if the primary supplier fails
     * @param message the error message to be logged if the primary supplier fails
     * @param args optional arguments to be included in the error message
     * @return the value retrieved from the primary supplier, or the fallback supplier
     *         if the primary supplier fails
     * @throws NullPointerException if the supplier, fallback, or message arguments are null
     */
    static <T> T attemptOrElseGet(
            Supplier<T> supplier,
            Supplier<T> fallback,
            String message,
            Object @Nullable ... args) {

        return Try.of(supplier)
                .onFailure(t -> Logger.error(message, t, t.getMessage(), args))
                .orElseGet(fallback);
    }

    /**
     * Applies the given function to the input and returns the result. If the function fails,
     * the specified fallback value is returned instead.
     *
     * @param <T> the type of the input parameter to the function
     * @param <R> the type of the result of the function
     * @param function the function to be applied
     * @param fallback the fallback value to be returned in case of an exception
     * @return a function that applies the given function to the input and returns the result,
     *         or the fallback value if the function fails
     * @throws NullPointerException if the function or fallback value is null
     */
    @Contract(pure = true)
    static <T, R> @NotNull Function<T, R> apply(Function<T, R> function, R fallback) {
        return t ->
                Try.of(() -> function.apply(t)).onFailure(e -> Logger.error(
                "Failed to apply function: {}",
                e,
                function.getClass().getSimpleName()))
                .orElse(fallback);
    }

    /**
     * Wraps a given function in a {@link Try} context, allowing safe execution
     * of the function with failure handling.
     *
     * @param <T> the input type of the function
     * @param <R> the return type of the function
     * @param function the function to be wrapped in a Try context, must not be null
     * @return a new function that, when applied, returns a {@link Try} instance containing
     *         either the result of the given function or an exception if the function fails
     * @throws NullPointerException if the provided function is null
     */
    @Contract(pure = true)
    static <T, R> @NotNull Function<T, Try<R>> applyTry(Function<T, R> function) {
        Objects.requireNonNull(function);

        return t -> Try.of(() -> function.apply(t));
    }

    /**
     * Executes the given {@link Runnable} and logs an error message with optional arguments if an exception occurs.
     *
     * @param runnable the {@link Runnable} to be executed; must*/
    static void runWithMessage(Runnable runnable, String message, Object @Nullable ... args) {
        Objects.requireNonNull(runnable);

        Try.run(runnable).onFailure(t -> Logger.error(
                "{} - Exception: {}", t, message, args)).orThrow();
    }

    /**
     * Executes the provided {@code runnable} up to the specified number of
     * {@code attempts} until it succeeds. Logs an error message for each failed
     * attempt.
     *
     * @param runnable the task to be executed, must not be {@code null}.
     * @param attempts the maximum number of attempts to retry the task execution.
     *                 Must be greater than or equal to 1.
     * @throws NullPointerException if {@code runnable} is {@code null}.
     */
    static void retry(Runnable runnable, int attempts) {
        Objects.requireNonNull(runnable);

        for (int i = 0; i < attempts; i++) {
            Try<Void> result = Try.run(runnable);

            if (result.isSuccess()) return;

            int finalI = i;
            result.onFailure(t -> Logger.error(
                    "Attempt {}/{} failed: {}",
                    t,
                    finalI + 1,
                    attempts,
                    runnable.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Wraps the provided consumer in another consumer that handles any exceptions
     * occurring during execution. If an exception is thrown while consuming an
     * input, it logs an error message and rethrows the exception.
     *
     * @param <T>      the type of the input to the consumer
     * @param consumer the consumer to be wrapped; must not be null
     * @return a new consumer that wraps and handles potential errors for the given consumer
     * @throws NullPointerException if the provided consumer is null
     */
    @Contract(pure = true)
    static <T> @NotNull Consumer<T> consumer(Consumer<T> consumer) {
        return t -> {
            Try.run(() -> consumer.accept(t)).onFailure(e -> Logger.error(
                    "Failed to consume: {}",
                    e,
                    consumer.getClass().getSimpleName()))
                    .orThrow();

        };
    }

    /**
     * Executes the given supplier and returns its result if successful.
     * If the supplier throws an exception, the exception is passed to the
     * provided exception mapper function, which generates a runtime exception
     * to be thrown.
     *
     * @param <T> the type of the result supplied by the supplier
     * @param supplier the operation to be executed, must not be null
     * @param exceptionMapper a function that maps a thrown exception to a
     *        {@link RuntimeException}, must not be null
     * @return the result supplied by the supplier if execution is successful
     * @throws NullPointerException if either the supplier or exceptionMapper is null
     * @throws RuntimeException if the supplier throws an exception and the
     *         exceptionMapper produces a runtime exception
     */
    static <T> T attemptOrThrow(Supplier<T> supplier, @NotNull Function<Throwable, RuntimeException> exceptionMapper) {
        Objects.requireNonNull(supplier);
        return Try.of(supplier).onFailure(exceptionMapper::apply).orThrow();
    }

    /**
     * Converts a given value using the provided converter function.
     * <p>
     * If the input value is null, an empty {@code Optional} is returned. If the conversion
     * fails, an error is logged, and an empty {@code Optional} is returned.
     *
     * @param <T> the type of the input value to be converted
     * @param <R> the type of the resulting value after conversion
     * @param value the input value to be converted; may be null
     * @param converter a {@code Function} that defines how to convert the input value
     * @return an {@code Optional} containing the converted value if successful, or an empty
     *         {@code Optional} if the input is null or the conversion fails
     * @throws NullPointerException if the converter function is null
     */
    @Contract("null, _ -> !null")
    static <T, R> Optional<R> convert(T value, Function<T, R> converter) {
        if (Objects.isNull(value)) return Optional.empty();
        return Try.of(() -> converter.apply(value)).onFailure(e -> Logger.error(
                "Failed to convert value: {} to type: {} ",
                e,
                value,
                converter.getClass().getSimpleName()))
                .toOptional();
    }

}
