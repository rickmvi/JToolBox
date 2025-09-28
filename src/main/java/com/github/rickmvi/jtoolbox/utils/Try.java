package com.github.rickmvi.jtoolbox.utils;

import com.github.rickmvi.jtoolbox.debug.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Try<T> {

    private final T value;
    private final Throwable failure;

    @Contract(pure = true)
    private Try(T value, Throwable failure) {
        this.value = value;
        this.failure = failure;
    }

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

    public boolean isSuccess() {
        return failure == null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public T get() {
        if (failure != null) throw new RuntimeException(failure);
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
}
