package com.github.rickmvi.jtoolbox.control.fors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;

public interface For<T> {

    void forEach(Consumer<T> action);

    CompletableFuture<Void> forEachAsync(Consumer<T> action);

    boolean anyMatch(Predicate<T> predicate);

    T findFirst(Predicate<T> predicate);

    T findLast(Predicate<T> predicate);

    <R> R findFirstValue(Function<T, R> mapper);

    <R> List<R> collect(Function<T, R> mapper);

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Integer> range(int start, int end) {
        return new ForInt(start, end, 1, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Integer> rangeDescentive(int start, int end) {
        return new ForInt(start, end, 1, true);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull For<Integer> range(int start, int end, int step) {
        return new ForInt(start, end, step, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Long> range(long start, long end) {
        return new ForLong(start, end, 1, false);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull For<Long> rangeDescentive(long start, long end) {
        return new ForLong(start, end, 1, true);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull For<Long> range(long start, long end, long step) {
        return new ForLong(start, end, step, false);
    }

    @Contract("_, _ -> new")
    static @NotNull For<Double> rangeDescentive(double start, double end) {
        return new ForDouble(start, end, 1, true);
    }

    @Contract("_, _, _ -> new")
    static @NotNull For<Double> range(double start, double end, double step) {
        return new ForDouble(start, end, step, false);
    }

    @Contract("_, _ -> new")
    static @NotNull For<Double> range(double start, double end) {
        return new ForDouble(start, end, 1, false);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull For<T> of(Iterable<T> iterable) {
        return new ForIterable<>(iterable);
    }

    @SafeVarargs
    @Contract("_ -> new")
    static <T> @NotNull For<T> of(T ... item) {
        return new ForIterable<>(List.of(item));
    }

}
