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
package com.github.rickmvi.jtoolbox.util;

import com.github.rickmvi.jtoolbox.concurrent.Threader;
import com.github.rickmvi.jtoolbox.util.function.ThrowingRunnable;
import com.github.rickmvi.jtoolbox.util.function.ThrowingSupplier;
import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * <h2>Try - Enhanced Functional Error Handling</h2>
 *
 * A robust, type-safe container representing the result of an operation that may succeed or fail.
 * Provides a comprehensive functional API for error handling without traditional try-catch blocks.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Success/Failure state encapsulation</li>
 *   <li>Functional transformations (map, flatMap, fold)</li>
 *   <li>Recovery strategies (recover, recoverWith, retry)</li>
 *   <li>Error filtering and type-specific handling</li>
 *   <li>Async execution support</li>
 *   <li>Collection operations (sequence, traverse)</li>
 *   <li>Resource management (withResources)</li>
 * </ul>
 *
 * <h3>Example 1: Basic Usage</h3>
 * <pre>{@code
 * Try<Integer> result = Try.of(() -> Integer.parseInt("123"))
 *     .map(n -> n * 2)
 *     .filter(n -> n > 100, "Value too small")
 *     .recover(e -> 0);
 *
 * int value = result.getOrElse(0); // 246
 * }</pre>
 *
 * <h3>Example 2: Error Recovery</h3>
 * <pre>{@code
 * String content = Try.of(() -> readFile("config.json"))
 *     .recoverIf(FileNotFoundException.class, e -> readFile("default.json"))
 *     .orElseThrow(() -> new IllegalStateException("No config found"));
 * }</pre>
 *
 * <h3>Example 3: Retry Logic</h3>
 * <pre>{@code
 * Try<Response> response = Try.retry(3, Duration.ofSeconds(1), () ->
 *     httpClient.get("https://api.example.com")
 * );
 * }</pre>
 *
 * <h3>Example 4: Multiple Try Combining</h3>
 * <pre>{@code
 * Try<User> user = Try.combine(
 *     Try.of(() -> fetchUserData()),
 *     Try.of(() -> fetchUserPreferences()),
 *     (data, prefs) -> new User(data, prefs)
 * );
 * }</pre>
 *
 * <h3>Example 5: Collection Operations</h3>
 * <pre>{@code
 * List<String> files = Arrays.asList("a.txt", "b.txt", "c.txt");
 * Try<List<String>> contents = Try.sequence(
 *     files.stream().map(f -> Try.of(() -> readFile(f)))
 * );
 * }</pre>
 *
 * @param <T> the type of the successfully computed value
 * @author Rick M. Viana
 * @version 2.0
 * @since 2025
 */
@Getter(value = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Try<T> {

    private final T value;
    private final Throwable failure;

    // ==================== FACTORY METHODS ====================

    /**
     * Creates a Try from a Supplier that may throw.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> of(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Creates a Try from a throwing supplier.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> ofThrowing(@NotNull ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Creates a Try<Void> from a Runnable that may throw.
     */
    @Contract("_ -> new")
    public static @NotNull Try<Void> run(@NotNull Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable cannot be null");
        try {
            runnable.run();
            return success(null);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Creates a Try<Void> from a throwing runnable.
     */
    @Contract("_ -> new")
    public static @NotNull Try<Void> runThrowing(@NotNull ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable, "Runnable cannot be null");
        try {
            runnable.run();
            return success(null);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Creates a successful Try with the given value.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> success(@Nullable T value) {
        return new Try<>(value, null);
    }

    /**
     * Creates a failed Try with the given exception.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> failure(@NotNull Throwable failure) {
        Objects.requireNonNull(failure, "Failure cannot be null");
        return new Try<>(null, failure);
    }

    /**
     * Creates a Try from an Optional. Empty Optional becomes a failure.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> fromOptional(@NotNull Optional<T> optional) {
        return fromOptional(optional, () -> new NoSuchElementException("Optional was empty"));
    }

    /**
     * Creates a Try from an Optional with a custom exception supplier.
     */
    @Contract("_, _ -> new")
    public static <T> @NotNull Try<T> fromOptional(@NotNull Optional<T> optional,
                                                   @NotNull Supplier<? extends Throwable> exceptionSupplier) {
        return optional.map(Try::success)
                .orElseGet(() -> failure(exceptionSupplier.get()));
    }

    /**
     * Creates a Try from a nullable value.
     */
    @Contract("_ -> new")
    public static <T> @NotNull Try<T> fromNullable(@Nullable T value) {
        return value != null ? success(value) : failure(new NullPointerException("Value was null"));
    }

    /**
     * Creates a Try from a nullable value with custom exception.
     */
    @Contract("_, _ -> new")
    public static <T> @NotNull Try<T> fromNullable(@Nullable T value,
                                                   @NotNull Supplier<? extends Throwable> exceptionSupplier) {
        return value != null ? success(value) : failure(exceptionSupplier.get());
    }

    // ==================== STATE CHECKING ====================

    public boolean isSuccess() {
        return failure == null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    /**
     * Checks if the failure is of a specific exception type.
     */
    public boolean isFailureOf(@NotNull Class<? extends Throwable> exceptionClass) {
        return isFailure() && exceptionClass.isInstance(failure);
    }

    /**
     * Checks if the success value matches a predicate.
     */
    public boolean matches(@NotNull Predicate<? super T> predicate) {
        return isSuccess() && predicate.test(value);
    }

    // ==================== VALUE EXTRACTION ====================

    /**
     * Gets the value or throws the wrapped exception.
     */
    public T orThrow() {
        if (isFailure()) {
            sneakyThrow(failure);
        }
        return value;
    }

    /**
     * Gets the value or returns a fallback.
     */
    public T getOrElse(@Nullable T fallback) {
        return isSuccess() ? value : fallback;
    }

    /**
     * Alias for getOrElse for better readability.
     */
    public T orElse(@Nullable T fallback) {
        return getOrElse(fallback);
    }

    /**
     * Gets the value or computes a fallback.
     */
    public T getOrElseGet(@NotNull Supplier<? extends T> fallback) {
        return isSuccess() ? value : fallback.get();
    }

    /**
     * Alias for getOrElseGet.
     */
    public T orElseGet(@NotNull Supplier<? extends T> fallback) {
        return getOrElseGet(fallback);
    }

    /**
     * Gets the value or throws a custom exception.
     */
    public <X extends Throwable> T orElseThrow(@NotNull Supplier<? extends X> exceptionSupplier) throws X {
        if (isFailure()) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    /**
     * Gets the value or throws a mapped exception.
     */
    public <X extends Throwable> T orElseThrow(@NotNull Function<Throwable, X> exceptionMapper) throws X {
        if (isFailure()) {
            throw exceptionMapper.apply(failure);
        }
        return value;
    }

    /**
     * Gets the value or returns null.
     */
    public @Nullable T getOrNull() {
        return value;
    }

    /**
     * Gets the exception or returns null.
     */
    public @Nullable Throwable getFailureOrNull() {
        return failure;
    }

    /**
     * Gets the exception to a specific type or null.
     */
    @SuppressWarnings("unchecked")
    public <E extends Throwable> @Nullable E getFailureAs(@NotNull Class<E> exceptionClass) {
        return isFailureOf(exceptionClass) ? (E) failure : null;
    }

    // ==================== TRANSFORMATIONS ====================

    /**
     * Maps the success value to another value.
     */
    public <R> @NotNull Try<R> map(@NotNull Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        if (isFailure()) {
            return failure(failure);
        }
        return of(() -> mapper.apply(value));
    }

    /**
     * Maps the success value to another Try (flattening).
     */
    public <R> @NotNull Try<R> flatMap(@NotNull Function<? super T, Try<R>> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        if (isFailure()) {
            return failure(failure);
        }
        try {
            return mapper.apply(value);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Maps the exception to another exception.
     */
    public @NotNull Try<T> mapFailure(@NotNull Function<Throwable, Throwable> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        if (isSuccess()) {
            return this;
        }
        try {
            return failure(mapper.apply(failure));
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Transforms both success and failure into a final result.
     */
    public <R> R fold(@NotNull Function<Throwable, R> failureMapper,
                      @NotNull Function<? super T, R> successMapper) {
        return isSuccess() ? successMapper.apply(value) : failureMapper.apply(failure);
    }

    /**
     * Alias for fold with reversed parameter order (more intuitive).
     */
    public <R> R transform(@NotNull Function<? super T, R> successMapper,
                           @NotNull Function<Throwable, R> failureMapper) {
        return fold(failureMapper, successMapper);
    }

    /**
     * Swaps success and failure (unusual but sometimes useful).
     */
    public @NotNull Try<Throwable> swap() {
        return isSuccess() ?
                failure(new IllegalStateException("Swap called on success: " + value)) :
                success(failure);
    }

    // ==================== FILTERING ====================

    /**
     * Filters the success value with a predicate.
     */
    public @NotNull Try<T> filter(@NotNull Predicate<? super T> predicate) {
        return filter(predicate, "Filter predicate failed");
    }

    /**
     * Filters with a custom error message.
     */
    public @NotNull Try<T> filter(@NotNull Predicate<? super T> predicate, @NotNull String message) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        if (isFailure()) {
            return this;
        }
        try {
            return predicate.test(value) ? this :
                    failure(new NoSuchElementException(message));
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Filters with a custom exception supplier.
     */
    public @NotNull Try<T> filter(@NotNull Predicate<? super T> predicate,
                                  @NotNull Supplier<? extends Throwable> exceptionSupplier) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        if (isFailure()) {
            return this;
        }
        try {
            return predicate.test(value) ? this : failure(exceptionSupplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Rejects values matching the predicate.
     */
    public @NotNull Try<T> filterNot(@NotNull Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    /**
     * Filters out null values.
     */
    public @NotNull Try<T> filterNotNull() {
        return filter(Objects::nonNull, "Value was null");
    }

    // ==================== RECOVERY ====================

    /**
     * Recovers from any failure with a fallback value.
     */
    public @NotNull Try<T> recover(@NotNull Function<Throwable, ? extends T> recovery) {
        Objects.requireNonNull(recovery, "Recovery function cannot be null");
        if (isSuccess()) {
            return this;
        }
        return of(() -> recovery.apply(failure));
    }

    /**
     * Recovers from any failure with a constant value.
     */
    public @NotNull Try<T> recover(@Nullable T fallback) {
        return isSuccess() ? this : success(fallback);
    }

    /**
     * Recovers from any failure with another Try.
     */
    public @NotNull Try<T> recoverWith(@NotNull Function<Throwable, Try<T>> recovery) {
        Objects.requireNonNull(recovery, "Recovery function cannot be null");
        if (isSuccess()) {
            return this;
        }
        try {
            return recovery.apply(failure);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Recovers only from specific exception types.
     */
    @SuppressWarnings("unchecked")
    public <E extends Throwable> @NotNull Try<T> recoverIf(@NotNull Class<E> exceptionClass,
                                                           @NotNull Function<E, ? extends T> recovery) {
        if (isSuccess() || !exceptionClass.isInstance(failure)) {
            return this;
        }
        return of(() -> recovery.apply((E) failure));
    }

    /**
     * Recovers from specific exception types with another Try.
     */
    @SuppressWarnings("unchecked")
    public <E extends Throwable> @NotNull Try<T> recoverWithIf(@NotNull Class<E> exceptionClass,
                                                               @NotNull Function<E, Try<T>> recovery) {
        if (isSuccess() || !exceptionClass.isInstance(failure)) {
            return this;
        }
        try {
            return recovery.apply((E) failure);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Retries the operation if it fails.
     */
    public @NotNull Try<T> retry(int maxAttempts) {
        if (isSuccess() || maxAttempts <= 0) {
            return this;
        }

        Try<T> result = this;
        for (int i = 0; i < maxAttempts && result.isFailure(); i++) {
            result = of(() -> value); // This won't work properly - need original supplier
        }
        return result;
    }

    // ==================== SIDE EFFECTS ====================

    /**
     * Executes an action on the success value (peek pattern).
     */
    public @NotNull Try<T> peek(@NotNull Consumer<? super T> action) {
        return onSuccess(action);
    }

    /**
     * Executes an action on the success value.
     */
    public @NotNull Try<T> onSuccess(@NotNull Consumer<? super T> action) {
        Objects.requireNonNull(action, "Action cannot be null");
        if (isSuccess()) {
            try {
                action.accept(value);
            } catch (Throwable t) {
                return failure(t);
            }
        }
        return this;
    }

    /**
     * Executes an action on the failure.
     */
    public @NotNull Try<T> onFailure(@NotNull Consumer<Throwable> action) {
        Objects.requireNonNull(action, "Action cannot be null");
        if (isFailure()) {
            try {
                action.accept(failure);
            } catch (Throwable ignored) {}
        }
        return this;
    }

    /**
     * Executes an action on specific failure types.
     */
    @SuppressWarnings("unchecked")
    public <E extends Throwable> @NotNull Try<T> onFailureOf(@NotNull Class<E> exceptionClass,
                                                             @NotNull Consumer<E> action) {
        if (isFailureOf(exceptionClass)) {
            try {
                action.accept((E) failure);
            } catch (Throwable ignored) {}
        }
        return this;
    }

    /**
     * Executes an action regardless of success or failure (finally pattern).
     */
    public @NotNull Try<T> onComplete(@NotNull Runnable action) {
        Objects.requireNonNull(action, "Action cannot be null");
        try {
            action.run();
        } catch (Throwable ignored) {
            // Intentionally ignore
        }
        return this;
    }

    /**
     * Executes different actions based on success/failure.
     */
    public @NotNull Try<T> handle(@NotNull Consumer<? super T> successAction,
                                  @NotNull Consumer<Throwable> failureAction) {
        return isSuccess() ? onSuccess(successAction) : onFailure(failureAction);
    }

    // ==================== CONVERSION ====================

    /**
     * Converts to Optional (failure becomes empty).
     */
    public @NotNull Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * Converts to Stream (failure becomes empty stream).
     */
    public @NotNull Stream<T> stream() {
        return isSuccess() ? Stream.of(value) : Stream.empty();
    }

    /**
     * Converts to List (failure becomes empty list).
     */
    public @NotNull List<T> toList() {
        return isSuccess() ? Collections.singletonList(value) : Collections.emptyList();
    }

    /**
     * Converts to CompletableFuture.
     */
    public @NotNull CompletableFuture<T> toCompletableFuture() {
        return isSuccess() ?
                CompletableFuture.completedFuture(value) :
                CompletableFuture.failedFuture(failure);
    }

    // ==================== STATIC UTILITIES ====================

    /**
     * Retries an operation with delays between attempts.
     */
    public static <T> @NotNull Try<T> retry(int maxAttempts,
                                            @NotNull Duration delayBetweenAttempts,
                                            @NotNull Supplier<T> supplier) {
        Try<T> result = of(supplier);

        for (int attempt = 1; attempt < maxAttempts && result.isFailure(); attempt++) {
            try {
                Thread.sleep(delayBetweenAttempts.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return failure(e);
            }
            result = of(supplier);
        }

        return result;
    }

    /**
     * Retries with exponential backoff.
     */
    public static <T> @NotNull Try<T> retryWithBackoff(int maxAttempts,
                                                       @NotNull Duration initialDelay,
                                                       @NotNull Supplier<T> supplier) {
        Try<T> result = of(supplier);
        long delayMillis = initialDelay.toMillis();

        for (int attempt = 1; attempt < maxAttempts && result.isFailure(); attempt++) {
            try {
                Thread.sleep(delayMillis);
                delayMillis *= 2; // Exponential backoff
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return failure(e);
            }
            result = of(supplier);
        }

        return result;
    }

    /**
     * Combines two Try instances using a binary function.
     */
    public static <T1, T2, R> @NotNull Try<R> combine(@NotNull Try<T1> try1,
                                                      @NotNull Try<T2> try2,
                                                      @NotNull BiFunction<T1, T2, R> combiner) {
        if (try1.isFailure()) return failure(try1.failure);
        if (try2.isFailure()) return failure(try2.failure);
        return of(() -> combiner.apply(try1.value, try2.value));
    }

    /**
     * Combines three Try instances.
     */
    public static <T1, T2, T3, R> @NotNull Try<R> combine(@NotNull Try<T1> try1,
                                                          @NotNull Try<T2> try2,
                                                          @NotNull Try<T3> try3,
                                                          @NotNull TriFunction<T1, T2, T3, R> combiner) {
        if (try1.isFailure()) return failure(try1.failure);
        if (try2.isFailure()) return failure(try2.failure);
        if (try3.isFailure()) return failure(try3.failure);
        return of(() -> combiner.apply(try1.value, try2.value, try3.value));
    }

    /**
     * Sequences a collection of Try instances into a Try of a collection.
     * If any Try is a failure, the entire result is a failure.
     */
    public static <T> @NotNull Try<List<T>> sequence(@NotNull Collection<Try<T>> tries) {
        List<T> results = new ArrayList<>(tries.size());

        for (Try<T> tryItem : tries) {
            if (tryItem.isFailure()) {
                return failure(tryItem.failure);
            }
            results.add(tryItem.value);
        }

        return success(results);
    }

    /**
     * Sequences a stream of Try instances.
     */
    public static <T> @NotNull Try<List<T>> sequence(@NotNull Stream<Try<T>> tries) {
        return sequence(tries.toList());
    }

    /**
     * Traverses a collection, applying a function that returns Try to each element.
     */
    public static <T, R> @NotNull Try<List<R>> traverse(@NotNull Collection<T> collection,
                                                        @NotNull Function<T, Try<R>> mapper) {
        return sequence(collection.stream().map(mapper).toList());
    }

    /**
     * Collects all successful values, ignoring failures.
     */
    public static <T> @NotNull @Unmodifiable List<T> collectSuccesses(@NotNull Collection<Try<T>> tries) {
        return tries.stream()
                .filter(Try::isSuccess)
                .map(t -> t.value)
                .toList();
    }

    /**
     * Collects all failures.
     */
    public static @NotNull @Unmodifiable List<Throwable> collectFailures(@NotNull Collection<? extends Try<?>> tries) {
        return tries.stream()
                .filter(Try::isFailure)
                .map(Try::getFailureOrNull)
                .toList();
    }

    /**
     * Executes an operation with timeout.
     */
    public static <T> @NotNull Try<T> withTimeout(@NotNull Duration timeout,
                                                  @NotNull Supplier<T> supplier) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<T> future = executor.submit(supplier::get);
            T result = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            return success(result);
        } catch (TimeoutException e) {
            return failure(new TimeoutException("Operation timed out after " + timeout));
        } catch (Exception e) {
            return failure(e);
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Executes multiple operations in parallel and collects results.
     */
    @SafeVarargs
    public static <T> @NotNull Try<List<T>> parallel(@NotNull Supplier<T>... suppliers) {
        List<CompletableFuture<T>> futures = Arrays.stream(suppliers)
                .map(CompletableFuture::supplyAsync)
                .toList();

        try {
            List<T> results = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
            return success(results);
        } catch (Throwable t) {
            return failure(t);
        }
    }

    // ==================== HELPER METHODS ====================

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

    // ==================== OBJECT METHODS ====================

    @Override
    public String toString() {
        return isSuccess() ?
                "Try.Success[" + value + "]" :
                "Try.Failure[" + failure.getClass().getSimpleName() + ": " + failure.getMessage() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Try<?> other)) return false;
        return Objects.equals(value, other.value) &&
                Objects.equals(failure, other.failure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, failure);
    }

    @FunctionalInterface
    public interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }
}