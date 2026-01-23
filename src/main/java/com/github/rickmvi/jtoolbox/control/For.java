package com.github.rickmvi.jtoolbox.control;

import com.github.rickmvi.jtoolbox.logger.log.LogLevel;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.Numbers;
import com.github.rickmvi.jtoolbox.util.Try;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.concurrent.CompletableFuture;

/**
 * Fluent and lazy iterable control structure with logging, error handling,
 * and functional-style composition.
 *
 * <p>Provides a declarative, Try-integrated iteration system with error handling,
 * logging, and parallel execution support.</p>
 *
 * <pre>{@code
 * For.range(1, 10)
 *    .filter(i -> i % 2 == 0)
 *    .map(i -> i * i)
 *    .debug()
 *    .forEach(System.out::println);
 * }</pre>
 *
 * @author Rick
 * @version 2.0
 * @since 2026
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "WeakerAccess"})
public final class For<T> {

    private final Iterable<T> source;
    private final List<UnaryOperator<Stream<T>>> operations;

    private boolean parallel = false;
    private Consumer<Throwable> errorHandler = null;
    private Predicate<T> repeatUntil = null;

    private static int START;
    private static int END;
    private static int STEP;
    private static BiPredicate<Integer, Integer> condition;

    public static @NotNull For<Integer> range(int start, int end) {
        return range(start, end, 1);
    }

    public static @NotNull For<Integer> range(int end) {
        return range(0, end, 1);
    }

    @Contract("_, _ -> new")
    public static @NotNull For<Long> range(long start, long end) {
        return range(start, end, 1L);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull For<Long> range(long start, long end, long step) {
        return rangeInternal(start, end, step);
    }

    @Contract("_, _ -> new")
    public static @NotNull For<Double> range(double start, double end) {
        return range(start, end, 1.0D);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull For<Double> range(double start, double end, double step) {
        return rangeInternal(start, end, step);
    }

    @Contract("_, _ -> new")
    public static @NotNull For<Float> range(float start, float end) {
        return range(start, end, 1.0F);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull For<Float> range(float start, float end, float step) {
        return rangeInternal(start, end, step);
    }

    public static @NotNull For<Integer> range(int start, int end, int step) {
        START = start;
        END = end;
        STEP = step;
        condition = (i, e) -> start <= end ? i <= e : i >= e;
        return buildRange();
    }

    private static <N extends Number & Comparable<N>> @NotNull For<N> rangeInternal(N start, N end, N step) {
        return Try.of(() -> {
            Condition.ThrowWhen(Numbers.isZero(step), () -> new IllegalArgumentException("Step cannot be zero"));

            List<N> list = new ArrayList<>();

            BigDecimal s  = Numbers.toBigDecimal(start);
            BigDecimal e  = Numbers.toBigDecimal(end);
            BigDecimal st = Numbers.toBigDecimal(step);
            boolean ascending = s.compareTo(e) <= 0;

            BigDecimal current = s;
            while (ascending ? current.compareTo(e) <= 0 : current.compareTo(e) >= 0) {
                list.add(Numbers.castNumber(current, start));
                current = current.add(ascending ? st : st.negate());
            }

            return new For<>(list, new ArrayList<>());
        }).orElseThrow(e -> new RuntimeException("Failed to build numeric range", e));
    }

    public @NotNull For<Integer> when(@NotNull BiPredicate<Integer, Integer> predicate) {
        condition = predicate;
        return rebuildRange();
    }

    public @NotNull For<Integer> step(int steps) {
        STEP = steps;
        return rebuildRange();
    }

    private static @NotNull For<Integer> buildRange() {
        return Try.of(() -> {
            Condition.ThrowWhen(Numbers.isZero(STEP), () -> {
                throw new IllegalArgumentException("Step cannot be 0");
            });

            List<Integer> list = new ArrayList<>();
            int i = START;

            while (condition.test(i, END)) {
                list.add(i);
                i += STEP;
            }

            return new For<>(list, new ArrayList<>());
        }).orElseThrow(e -> new RuntimeException("Failed to build range", e));
    }

    private @NotNull For<Integer> rebuildRange() {
        return buildRange();
    }

    @SafeVarargs
    @Contract("_ -> new")
    public static <T> @NotNull For<T> of(T... values) {
        return new For<>(Arrays.asList(values), new ArrayList<>());
    }

    @Contract("_ -> new")
    public static <T> @NotNull For<T> of(Iterable<T> iterable) {
        return new For<>(iterable, new ArrayList<>());
    }

    public For<T> filter(Predicate<T> predicate) {
        return chain(stream -> stream.filter(predicate));
    }

    public <R> For<R> map(Function<? super T, ? extends R> mapper) {
        List<UnaryOperator<Stream<R>>> nextOps = new ArrayList<>();
        for (UnaryOperator<Stream<T>> op : operations) {
            nextOps.add(stream -> {
                @SuppressWarnings("unchecked")
                Stream<T> prev = (Stream<T>) stream;
                return op.apply(prev).map(mapper);
            });
        }
        @SuppressWarnings("unchecked")
        Iterable<R> castedSource = (Iterable<R>) source;

        return new For<>(castedSource, nextOps)
                .setParallel(parallel)
                .setErrorHandler(errorHandler)
                .setRepeatUntil(null);
    }

    public For<T> onEach(Consumer<T> action) {
        return chain(stream -> stream.peek(action));
    }

    public For<T> limit(int limit) {
        return chain(stream -> stream.limit(limit));
    }

    public For<T> skip(int skip) {
        return chain(stream -> stream.skip(skip));
    }

    public For<T> reverse() {
        return chain(stream -> {
            List<T> list = stream.collect(Collectors.toList());
            Collections.reverse(list);
            return list.stream();
        });
    }

    @Contract(value = "_ -> this")
    public For<T> repeatUntil(Predicate<T> condition) {
        this.repeatUntil = condition;
        return this;
    }

    @Contract(value = " -> this")
    public For<T> parallel() {
        this.parallel = true;
        return this;
    }

    @Contract(value = "_ -> this")
    public For<T> onError(Consumer<Throwable> handler) {
        this.errorHandler = handler;
        return this;
    }

    public For<T> debug() {
        return debug(LogLevel.DEBUG);
    }

    public For<T> debug(LogLevel level) {
        return onEach(e -> Logger.log(level, "Processing element: {}", e));
    }

    public void forEach(Consumer<T> action) {
        Try.run(() -> Condition.isTrue(repeatUntil != null, () -> repeatStream(build(), action))
                .orElse(() -> build().forEach(action))
        ).onFailure(this::handleError);
    }

    @Contract("_ -> new")
    public @NotNull CompletableFuture<Void> forEachAsync(Consumer<T> action) {
        return CompletableFuture.runAsync(() -> forEach(action));
    }

    public Optional<T> findFirst() {
        return Try.of(() -> build().findFirst())
                .onFailure(this::handleError)
                .orElse(Optional.empty());
    }

    @SuppressWarnings("unchecked")
    public Optional<T> findLast() {
        return (Optional<T>) Try.of(() -> {
            List<T> list = build().toList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.getLast());
        }).onFailure(this::handleError).orElse(Optional.empty());
    }

    public List<T> toList() {
        return Try.of(() -> build().collect(Collectors.toList()))
                .onFailure(this::handleError)
                .orElse(Collections.emptyList());
    }

    public long count() {
        return Try.of(() -> build().count())
                .onFailure(this::handleError)
                .orElse(0L);
    }

    public Stream<T> stream() {
        return Try.of(this::build)
                .onFailure(this::handleError)
                .orElse(Stream.empty());
    }

    private For<T> chain(UnaryOperator<Stream<T>> op) {
        List<UnaryOperator<Stream<T>>> next = new ArrayList<>(operations);
        next.add(op);
        return new For<>(source, next)
                .setParallel(parallel)
                .setErrorHandler(errorHandler)
                .setRepeatUntil(repeatUntil);
    }

    private Stream<T> build() {
        Stream<T> stream = StreamSupport.stream(source.spliterator(), parallel);
        for (UnaryOperator<Stream<T>> op : operations)
            stream = op.apply(stream);
        return stream;
    }

    private void repeatStream(@NotNull Stream<T> stream, Consumer<T> action) {
        List<T> list = stream.toList();
        boolean shouldStop;
        do {
            shouldStop = false;
            for (T element : list) {
                action.accept(element);
                if (repeatUntil != null && repeatUntil.test(element)) {
                    shouldStop = true;
                    break;
                }
            }
        } while (!shouldStop);
    }

    private void handleError(Throwable t) {
        Condition.isTrue(errorHandler != null, () -> errorHandler.accept(t))
                .orElse(() -> Logger.error("Error in For pipeline: {}", t));
    }

    @Contract(value = "_ -> this")
    private For<T> setParallel(boolean parallel) {
        this.parallel = parallel;
        return this;
    }

    @Contract(value = "_ -> this")
    private For<T> setErrorHandler(Consumer<Throwable> handler) {
        this.errorHandler = handler;
        return this;
    }

    @Contract(value = "_ -> this")
    private For<T> setRepeatUntil(Predicate<T> condition) {
        this.repeatUntil = condition;
        return this;
    }
}
