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

import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A utility class for performing flexible switch-like operations on an input value.
 * This class allows defining multiple cases based on value matching, type checking,
 * condition testing, and other criteria, and returns a result for matched cases.
 * The class is immutable once created and operates on a builder-like pattern.
 *
 * @param <T> The type of the input value to be evaluated in the switch.
 * @param <R> The type of the result returned by the switch operations.
 */
public final class Switch<T, R> {

    private final T                value;
    private final List<Case<T, R>> cases   = new ArrayList<>();
    private Function<Exception, R> recover;
    private boolean                matched = false;
    private R                      result;

    private Switch(T value) {
        this.value = value;
    }

    @Contract("_ -> new")
    public static <T, R> @NotNull Switch<T, R> on(T value) {
        return new Switch<>(value);
    }

    @Contract("_ -> this")
    public Switch<T, R> caseNull(Supplier<R> action) {
        cases.add(v -> v == null ? action.get() : null);
        return this;
    }

    @Contract("_, _ -> this")
    public Switch<T, R> caseValue(T expected, Supplier<R> action) {
        cases.add(v -> Objects.equals(v, expected) ? action.get() : null);
        return this;
    }

    @Contract("_, _ -> this")
    public <U extends T, R2 extends R> Switch<T, R> caseType(Class<U> type, Function<? super U, ? extends R2> action) {
        cases.add(v -> type.isInstance(v) ? action.apply(type.cast(v)) : null);
        return this;
    }

    @Contract("_, _ -> this")
    public <E extends Enum<E>> Switch<T, R> caseEnum(Class<E> type, Function<E, R> action) {
        cases.add(v -> type.isInstance(v) ? action.apply(type.cast(v)) : null);
        return this;
    }

    @Contract("_, _ -> this")
    public Switch<T, R> caseThrowable(Class<? extends Throwable> type, Function<Throwable, R> action) {
        cases.add(v -> type.isInstance(v) ? action.apply((Throwable) v) : null);
        return this;
    }

    @Contract("_ -> this")
    public Switch<T, R> caseCollection(Function<Collection<?>, R> action) {
        cases.add(v -> v instanceof Collection ? action.apply((Collection<?>) v) : null);
        return this;
    }

    @Contract("_ -> this")
    public Switch<T, R> caseMap(Function<? super Map<?, ?>, R> action) {
        cases.add(v -> v instanceof Map ? action.apply((Map<?, ?>) v) : null);
        return this;
    }

    @Contract("_ -> this")
    public Switch<T, R> caseArray(Function<Object, R> action) {
        cases.add(v -> (v != null && v.getClass().isArray()) ? action.apply(v) : null);
        return this;
    }

    @Contract("_, _ -> this")
    public Switch<T, R> caseCondition(Predicate<T> condition, Function<T, R> action) {
        cases.add(v -> condition.test(v) ? action.apply(v) : null);
        return this;
    }

    @Contract("_ -> this")
    public Switch<T, R> caseDefault(@NotNull Function<T, R> action) {
        cases.add(action::apply);
        return this;
    }

    @Contract(value = "_ -> this")
    public Switch<T, R> recover(Function<Exception, R> recover) {
        this.recover = recover;
        return this;
    }

    public R get() {
        if (matched) return result;

        AtomicReference<R> tempResult = new AtomicReference<>();
        Try<R> tryResult = Try.of(() -> {
            for (Case<T, R> c : cases) {
                R value = c.apply(this.value);
                if (value != null) {
                    tempResult.set(value);
                    matched = true;
                    break;
                }
            }
            If.Throws(!matched, () -> new IllegalStateException("No case matched and no default provided"));
            return tempResult.get();
        });

        result = tryResult.recover(e -> {
            if (recover != null) return recover.apply((Exception) e);
            throw new RuntimeException(e);
        }).orThrow();

        return result;
    }

    public R orElse(R fallback) {
        return Try.of(this::get).orElse(fallback);
    }

    public R orThrow() {
        return get();
    }

    @FunctionalInterface
    private interface Case<T, R> {
        R apply(T t);
    }

    @FunctionalInterface
    public interface Supplier<R> {
        R get();
    }

    @Contract("_ -> new")
    public static <N extends Number> @NotNull Switch<Number, N> onNumber(N value) {
        return new Switch<>(value);
    }

    @Contract("_ -> new")
    public static @NotNull Switch<Object, String> onStringify(Object value) {
        return new Switch<>(value);
    }

    @Contract("_ -> new")
    public static @NotNull Switch<Object, Collection<?>> onCollection(Object value) {
        return new Switch<>(value);
    }

    @Contract("_ -> new")
    public static @NotNull Switch<Object, Object> of(Object value) {
        return new Switch<>(value);
    }

    @Contract("_ -> new")
    public static @NotNull Switch<Object, Map<?, ?>> onMap(Object value) {
        return new Switch<>(value);
    }
}
