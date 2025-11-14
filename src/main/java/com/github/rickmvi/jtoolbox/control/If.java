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
 *
 * @author Rick
 * @since 1.3
 */
public interface If {

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull If.Runner isTrue(boolean condition, Runnable action) {
        return new Runner(condition, action);
    }

    @Contract("_ -> new")
    static @NotNull If.Runner when(boolean condition) {
        return new Runner(condition, null);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Runner {
        private final boolean condition;
        private final Runnable action;

        public void orElse(Runnable elseAction) {
            if (condition) {
                if (action != null) action.run();
            } else {
                elseAction.run();
            }
        }

        public void run() {
            if (condition && action != null) action.run();
        }

        public void orElseThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (condition) {
                if (action != null) action.run();
            } else {
                throw exceptionSupplier.get();
            }
        }

        public Runner apply(Runnable action) {
            return new Runner(this.condition, action);
        }

        public Runner negate() {
            return new Runner(!this.condition, this.action);
        }

        public Runner or(boolean otherCondition) {
            return new Runner(this.condition || otherCondition, this.action);
        }

        public Runner and(boolean otherCondition) {
            return new Runner(this.condition && otherCondition, this.action);
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull Suppliers<T> supplyTrue(boolean condition, Supplier<T> supplier) {
        return new Suppliers<>(condition, supplier);
    }

    @Contract("_ -> new")
    static <T> @NotNull Suppliers<T> whenSupply(boolean condition) {
        return new Suppliers<>(condition, null);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Suppliers<T> {
        private final boolean condition;
        private final Supplier<T> supplier;

        public @NotNull Optional<T> getOptional() {
            return condition ? Optional.ofNullable(supplier.get()) : Optional.empty();
        }

        public T orElseGet(@NotNull Supplier<T> elseSupplier) {
            return condition ? supplier.get() : elseSupplier.get();
        }

        public T orElseThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (condition) return supplier.get();
            throw exceptionSupplier.get();
        }

        public <R> Suppliers<R> map(@NotNull Function<T, R> mapper) {
            return new Suppliers<>(this.condition, () -> mapper.apply(supplier.get()));
        }

        public <R> Suppliers<R> flatMap(@NotNull Function<T, Suppliers<R>> mapper) {
            if (condition) {
                return mapper.apply(supplier.get());
            }
            return new Suppliers<>(false, null);
        }

        public Suppliers<T> apply(Supplier<T> supplier) {
            return new Suppliers<>(this.condition, supplier);
        }

        /**
         * Inverts the stored condition.
         */
        public Suppliers<T> negate() {
            return new Suppliers<>(!this.condition, this.supplier);
        }

        public Suppliers<T> or(boolean otherCondition) {
            return new Suppliers<>(this.condition || otherCondition, this.supplier);
        }

        public Suppliers<T> and(boolean otherCondition) {
            return new Suppliers<>(this.condition && otherCondition, this.supplier);
        }
    }

    /* ---------------------------- UTILITIES ---------------------------- */

    /**
     * Throws a RuntimeException if the condition is TRUE. Use this for assertions.
     *
     * @param condition The condition that, if true, triggers the exception.
     * @param exceptionSupplier A supplier for the exception to be thrown.
     * @throws RuntimeException The supplied exception if the condition is true.
     */
    @Contract("true, _ -> fail")
    static void ThrowWhen(boolean condition, @NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) throw exceptionSupplier.get();
    }

    /**
     * Helper utility to convert a condition and a supplier into an Optional.
     * This method is superseded by {@link Suppliers#getOptional()}.
     *
     * @deprecated Use {@link If#supplyTrue(boolean, Supplier)} followed by {@link Suppliers#getOptional()}.
     */
    @Deprecated
    @Contract("_, _ -> !null")
    static <R> @NotNull Optional<R> optional(boolean condition, Supplier<R> whenTrue) {
        return supplyTrue(condition, whenTrue).getOptional();
    }

    /**
     * Returns a message supplied by the provided {@code messageSupplier} if it is not {@code null},
     * otherwise returns the {@code defaultMessage}.
     *
     * @param messageSupplier A {@link Supplier} that provides the message. If {@code null},
     *                        the {@code defaultMessage} will be returned instead.
     * @param defaultMessage  The default message to return if {@code messageSupplier} is {@code null}.
     * @return The message provided by {@code messageSupplier} if it is not {@code null}, or
     *         {@code defaultMessage} otherwise.
     * @throws NullPointerException If {@code messageSupplier} is not {@code null} but its
     *                               {@code get()} method returns {@code null}.
     */
    static String MessageOrDefault(Supplier<String> messageSupplier, String defaultMessage) {
        return Objects.isNull(messageSupplier) ? defaultMessage : messageSupplier.get();
    }

    /**
     * Throws a {@link RuntimeException} if the specified condition is {@code true}.
     * This method is deprecated and should be replaced by {@link #ThrowWhen(boolean, Supplier)}.
     *
     * @param condition The condition that, if {@code true}, triggers the exception.
     * @param exceptionSupplier A supplier for the exception to be thrown.
     * @throws RuntimeException The exception provided by {@code exceptionSupplier} if {@code condition} is {@code true}.
     * @deprecated Use {@link #ThrowWhen(boolean, Supplier)} instead.
     */
    @Deprecated
    @Contract("true, _ -> fail")
    static void Throws(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        ThrowWhen(condition, exceptionSupplier);
    }
}