package com.github.rickmvi.jtoolbox.control;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 * <h2>Conditional - Fluent Conditional Execution API</h2>
 *
 * A powerful, type-safe utility for conditional execution with a fluent interface.
 * Provides functional-style flow control, eliminating the need for verbose if-else blocks.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Automatic execution without explicit .run() calls</li>
 *   <li>Type-safe value transformation and mapping</li>
 *   <li>Pattern matching capabilities</li>
 *   <li>Lazy evaluation support</li>
 *   <li>Exception handling integration</li>
 *   <li>Null-safe operations</li>
 * </ul>
 *
 * <h3>Example 1: Basic Execution (Auto-run)</h3>
 * <pre>{@code
 * // Old way (verbose)
 * if (user.isActive()) {
 *     sendNotification(user);
 * } else {
 *     logInactiveUser(user);
 * }
 *
 * // New way (fluent, auto-executes)
 * Conditional
 *     .when(user.isActive())
 *     .then(() -> sendNotification(user))
 *     .otherwise(() -> logInactiveUser(user));
 * }</pre>
 *
 * <h3>Example 2: Value Transformation</h3>
 * <pre>{@code
 * String result = Conditional
 *     .when(score >= 90)
 *     .compute(() -> "Excellent")
 *     .orWhen(score >= 70)
 *     .compute(() -> "Good")
 *     .orElse(() -> "Needs Improvement");
 * }</pre>
 *
 * <h3>Example 3: Pattern Matching</h3>
 * <pre>{@code
 * String message = Conditional
 *     .match(statusCode)
 *     .when(200, () -> "Success")
 *     .when(404, () -> "Not Found")
 *     .when(500, () -> "Server Error")
 *     .orElse(() -> "Unknown Status");
 * }</pre>
 *
 * <h3>Example 4: Predicate Chains</h3>
 * <pre>{@code
 * Conditional
 *     .when(user.isActive())
 *     .and(user.hasPermission("ADMIN"))
 *     .then(() -> grantAccess())
 *     .otherwise(() -> denyAccess());
 * }</pre>
 *
 * @author Rick M. Viana
 * @version 2.0
 * @since 2025
 */
@UtilityClass
@SuppressWarnings("unused")
public final class If {

    // ==================== ACTION-BASED CONDITIONALS ====================

    /**
     * Creates a conditional execution context based on a boolean condition.
     * Actions execute immediately when terminal operations are called.
     *
     * @param condition the condition to evaluate
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder when(boolean condition) {
        return new ActionBuilder(condition);
    }

    /**
     * Creates a conditional execution context based on a lazy predicate.
     * The predicate is evaluated only when needed.
     *
     * @param predicate supplier that provides the condition
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder when(@NotNull BooleanSupplier predicate) {
        return new ActionBuilder(predicate.getAsBoolean());
    }

    /**
     * Creates a conditional for object null checking.
     *
     * @param object the object to check
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder whenNotNull(@Nullable Object object) {
        return new ActionBuilder(object != null);
    }

    /**
     * Creates a conditional for object null checking.
     *
     * @param object the object to check
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder whenNull(@Nullable Object object) {
        return new ActionBuilder(object == null);
    }

    /**
     * Creates a conditional based on an Optional.
     *
     * @param optional the optional to check
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder whenPresent(@NotNull Optional<?> optional) {
        return new ActionBuilder(optional.isPresent());
    }

    /**
     * Creates a conditional based on an Optional being empty.
     *
     * @param optional the optional to check
     * @return a fluent action builder
     */
    @Contract("_ -> new")
    public static @NotNull ActionBuilder whenEmpty(@NotNull Optional<?> optional) {
        return new ActionBuilder(optional.isEmpty());
    }

    /**
     * Fluent builder for conditional action execution.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ActionBuilder {
        private final boolean condition;

        /**
         * Executes the action if the condition is true, otherwise executes the else action.
         * This is a terminal operation that executes immediately.
         *
         * @param action the action to execute if the condition is true
         * @return chained else builder
         */
        public @NotNull ElseBuilder then(@NotNull Runnable action) {
            Objects.requireNonNull(action, "Action cannot be null");
            return new ElseBuilder(condition, action);
        }

        /**
         * Executes the consumer if the condition is true.
         *
         * @param value the value to pass to the consumer
         * @param consumer the consumer to execute
         * @param <T> the type of value
         * @return chained else builder
         */
        public <T> @NotNull ElseBuilder thenAccept(T value, @NotNull Consumer<T> consumer) {
            return then(() -> consumer.accept(value));
        }

        /**
         * Combines this condition with another using logical AND.
         *
         * @param other the other condition
         * @return a new builder with a combined condition
         */
        public @NotNull ActionBuilder and(boolean other) {
            return new ActionBuilder(this.condition && other);
        }

        /**
         * Combines this condition with another using logical AND (lazy).
         *
         * @param other supplier for the other condition
         * @return a new builder with a combined condition
         */
        public @NotNull ActionBuilder and(@NotNull BooleanSupplier other) {
            return new ActionBuilder(this.condition && other.getAsBoolean());
        }

        /**
         * Combines this condition with another using logical OR.
         *
         * @param other the other condition
         * @return a new builder with a combined condition
         */
        public @NotNull ActionBuilder or(boolean other) {
            return new ActionBuilder(this.condition || other);
        }

        /**
         * Combines this condition with another using logical OR (lazy).
         *
         * @param other supplier for the other condition
         * @return a new builder with a combined condition
         */
        public @NotNull ActionBuilder or(@NotNull BooleanSupplier other) {
            return new ActionBuilder(this.condition || other.getAsBoolean());
        }

        /**
         * Negates the condition.
         *
         * @return a new builder with an inverted condition
         */
        public @NotNull ActionBuilder negate() {
            return new ActionBuilder(!this.condition);
        }

        /**
         * Throws an exception if the condition is true.
         * Terminal operation.
         *
         * @param exceptionSupplier supplier for the exception
         * @throws RuntimeException if the condition is true
         */
        @Contract("_ -> fail")
        public void thenThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (condition) {
                throw exceptionSupplier.get();
            }
        }
    }

    /**
     * Builder for else branch of conditional execution.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ElseBuilder {
        private final boolean condition;
        private final Runnable thenAction;

        /**
         * Executes an alternative action if the condition was false.
         * This completes the conditional and executes immediately.
         *
         * @param elseAction the action to execute if condition is false
         */
        public void otherwise(@NotNull Runnable elseAction) {
            Objects.requireNonNull(elseAction, "Else action cannot be null");
            if (condition) {
                thenAction.run();
            } else {
                elseAction.run();
            }
        }

        /**
         * Completes the conditional, executing only the then action if true.
         * This is equivalent to an if without else.
         */
        public void execute() {
            if (condition) {
                thenAction.run();
            }
        }

        /**
         * Throws an exception if the condition was false.
         *
         * @param exceptionSupplier supplier for the exception
         * @throws RuntimeException if condition is false
         */
        public void orThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (condition) {
                thenAction.run();
            } else {
                throw exceptionSupplier.get();
            }
        }

        /**
         * Chains another condition if the first was false.
         *
         * @param nextCondition the next condition to evaluate
         * @return a new action builder for chaining
         */
        public @NotNull ActionBuilder orWhen(boolean nextCondition) {
            if (condition) {
                thenAction.run();
                return new ActionBuilder(false); // Already handled
            }
            return new ActionBuilder(nextCondition);
        }
    }

    // ==================== VALUE-BASED CONDITIONALS ====================

    /**
     * Creates a value-based conditional that returns a result.
     *
     * @param condition the condition to evaluate
     * @param <T> the type of value to return
     * @return a fluent value builder
     */
    @Contract("_ -> new")
    public static <T> @NotNull ValueBuilder<T> whenValue(boolean condition) {
        return new ValueBuilder<>(condition);
    }

    /**
     * Value builder for conditional computation.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ValueBuilder<T> {
        private final boolean condition;

        /**
         * Computes a value if the condition is true.
         *
         * @param supplier the supplier of the value
         * @return chained value resolver
         */
        public @NotNull ValueResolver<T> compute(@NotNull Supplier<T> supplier) {
            Objects.requireNonNull(supplier, "Supplier cannot be null");
            return new ValueResolver<>(condition, supplier);
        }

        /**
         * Returns a constant value if the condition is true.
         *
         * @param value the value to return
         * @return chained value resolver
         */
        public @NotNull ValueResolver<T> value(T value) {
            return new ValueResolver<>(condition, () -> value);
        }
    }

    /**
     * Resolver for extracting values from conditionals.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ValueResolver<T> {
        private final boolean condition;
        private final Supplier<T> supplier;

        /**
         * Returns the computed value or an alternative.
         * Terminal operation.
         *
         * @param elseSupplier supplier for alternative value
         * @return the computed or alternative value
         */
        public T orElse(@NotNull Supplier<T> elseSupplier) {
            return condition ? supplier.get() : elseSupplier.get();
        }

        /**
         * Returns the computed value or a constant alternative.
         * Terminal operation.
         *
         * @param elseValue the alternative value
         * @return the computed or alternative value
         */
        public T orElse(T elseValue) {
            return condition ? supplier.get() : elseValue;
        }

        /**
         * Returns the computed value or null.
         * Terminal operation.
         *
         * @return the computed value or null
         */
        public @Nullable T orNull() {
            return condition ? supplier.get() : null;
        }

        /**
         * Returns the computed value as an Optional.
         * Terminal operation.
         *
         * @return optional containing the value if condition is true
         */
        public @NotNull Optional<T> asOptional() {
            return condition ? Optional.ofNullable(supplier.get()) : Optional.empty();
        }

        /**
         * Returns the computed value or throws an exception.
         * Terminal operation.
         *
         * @param exceptionSupplier supplier for the exception
         * @return the computed value
         * @throws RuntimeException if condition is false
         */
        public T orThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (condition) {
                return supplier.get();
            }
            throw exceptionSupplier.get();
        }

        /**
         * Chains another condition if the first was false.
         *
         * @param nextCondition the next condition to evaluate
         * @return a new value builder for chaining
         */
        public @NotNull ValueBuilder<T> orWhen(boolean nextCondition) {
            if (condition) {
                return new ValueBuilder<>(true) {
                    @Override
                    public @NotNull ValueResolver<T> compute(@NotNull Supplier<T> ignored) {
                        return new ValueResolver<>(true, supplier);
                    }
                };
            }
            return new ValueBuilder<>(nextCondition);
        }

        /**
         * Transforms the value if present.
         *
         * @param mapper the transformation function
         * @param <R> the result type
         * @return a new value resolver with transformed value
         */
        public <R> @NotNull ValueResolver<R> map(@NotNull Function<T, R> mapper) {
            return new ValueResolver<>(condition, () -> mapper.apply(supplier.get()));
        }

        /**
         * Flat maps the value if present.
         *
         * @param mapper the flat mapping function
         * @param <R> the result type
         * @return a new value resolver
         */
        public <R> @NotNull ValueResolver<R> flatMap(@NotNull Function<T, Supplier<R>> mapper) {
            if (!condition) {
                return new ValueResolver<>(false, () -> null);
            }
            return new ValueResolver<>(true, mapper.apply(supplier.get()));
        }

        /**
         * Filters the value based on a predicate.
         *
         * @param predicate the filter predicate
         * @return a new value resolver that may be empty
         */
        public @NotNull ValueResolver<T> filter(@NotNull Predicate<T> predicate) {
            return new ValueResolver<>(condition && predicate.test(supplier.get()), supplier);
        }
    }

    // ==================== PATTERN MATCHING ====================

    /**
     * Creates a pattern matching context for the given value.
     *
     * @param value the value to match against
     * @param <T> the type of value
     * @param <R> the type of result
     * @return a pattern matcher
     */
    @Contract("_ -> new")
    public static <T, R> @NotNull PatternMatcher<T, R> match(@Nullable T value) {
        return new PatternMatcher<>(value);
    }

    /**
     * Pattern matching implementation.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PatternMatcher<T, R> {
        private final T value;
        private R result;
        private boolean matched;

        /**
         * Matches against a specific value.
         *
         * @param pattern the pattern to match
         * @param resultSupplier supplier for the result
         * @return this matcher for chaining
         */
        public @NotNull PatternMatcher<T, R> when(T pattern, @NotNull Supplier<R> resultSupplier) {
            if (!matched && Objects.equals(value, pattern)) {
                result = resultSupplier.get();
                matched = true;
            }
            return this;
        }

        /**
         * Matches against a specific value with a constant result.
         *
         * @param pattern the pattern to match
         * @param result the result to return
         * @return this matcher for chaining
         */
        public @NotNull PatternMatcher<T, R> when(T pattern, R result) {
            return when(pattern, () -> result);
        }

        /**
         * Matches using a predicate.
         *
         * @param predicate the matching predicate
         * @param resultSupplier supplier for the result
         * @return this matcher for chaining
         */
        public @NotNull PatternMatcher<T, R> whenMatch(@NotNull Predicate<T> predicate, @NotNull Supplier<R> resultSupplier) {
            if (!matched && predicate.test(value)) {
                result = resultSupplier.get();
                matched = true;
            }
            return this;
        }

        /**
         * Matches using a predicate with a constant result.
         *
         * @param predicate the matching predicate
         * @param result the result to return
         * @return this matcher for chaining
         */
        public @NotNull PatternMatcher<T, R> whenMatch(@NotNull Predicate<T> predicate, R result) {
            return whenMatch(predicate, () -> result);
        }

        /**
         * Matches if the value is an instance of the given type.
         *
         * @param type the type to match
         * @param resultSupplier supplier for the result
         * @param <S> the specific type
         * @return this matcher for chaining
         */
        public <S extends T> @NotNull PatternMatcher<T, R> whenType(@NotNull Class<S> type, @NotNull Function<S, R> resultSupplier) {
            if (!matched && type.isInstance(value)) {
                result = resultSupplier.apply(type.cast(value));
                matched = true;
            }
            return this;
        }

        /**
         * Default case when no pattern matched.
         * Terminal operation.
         *
         * @param defaultSupplier supplier for default result
         * @return the matched or default result
         */
        public R orElse(@NotNull Supplier<R> defaultSupplier) {
            return matched ? result : defaultSupplier.get();
        }

        /**
         * Default case with constant value.
         * Terminal operation.
         *
         * @param defaultValue the default result
         * @return the matched or default result
         */
        public R orElse(R defaultValue) {
            return matched ? result : defaultValue;
        }

        /**
         * Returns null if no pattern matched.
         * Terminal operation.
         *
         * @return the matched result or null
         */
        public @Nullable R orNull() {
            return result;
        }

        /**
         * Returns Optional of the result.
         * Terminal operation.
         *
         * @return optional containing the result if matched
         */
        public @NotNull Optional<R> asOptional() {
            return Optional.ofNullable(result);
        }

        /**
         * Throws exception if no pattern matched.
         * Terminal operation.
         *
         * @param exceptionSupplier supplier for the exception
         * @return the matched result
         * @throws RuntimeException if no pattern matched
         */
        public R orThrow(@NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
            if (matched) {
                return result;
            }
            throw exceptionSupplier.get();
        }
    }

    // ==================== ASSERTION UTILITIES ====================

    /**
     * Throws an exception if the condition is true.
     *
     * @param condition the condition to check
     * @param exceptionSupplier supplier for the exception
     * @throws RuntimeException if the condition is true
     */
    @Contract("true, _ -> fail")
    public static void throwIf(boolean condition, @NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Throws an exception if the condition is false.
     *
     * @param condition the condition to check
     * @param exceptionSupplier supplier for the exception
     * @throws RuntimeException if condition is false
     */
    @Contract("false, _ -> fail")
    public static void throwUnless(boolean condition, @NotNull Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Throws IllegalArgumentException if the condition is true.
     *
     * @param condition the condition to check
     * @param message the exception message
     * @throws IllegalArgumentException if condition is true
     */
    @Contract("true, _ -> fail")
    public static void requireFalse(boolean condition, @NotNull String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throws IllegalArgumentException if the condition is false.
     *
     * @param condition the condition to check
     * @param message the exception message
     * @throws IllegalArgumentException if condition is false
     */
    @Contract("false, _ -> fail")
    public static void requireTrue(boolean condition, @NotNull String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Requires that the object is not null.
     *
     * @param object the object to check
     * @param message the exception message
     * @param <T> the type of object
     * @return the object if not null
     * @throws NullPointerException if object is null
     */
    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> @NotNull T requireNonNull(@Nullable T object, @NotNull String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    // ==================== CONVENIENCE UTILITIES ====================

    /**
     * Executes one of two actions based on a condition.
     * This is a simple, direct if-else replacement.
     *
     * @param condition the condition to evaluate
     * @param ifTrue action to execute if true
     * @param ifFalse action to execute if false
     */
    public static void ifElse(boolean condition, @NotNull Runnable ifTrue, @NotNull Runnable ifFalse) {
        if (condition) {
            ifTrue.run();
        } else {
            ifFalse.run();
        }
    }

    /**
     * Returns one of two values based on a condition.
     * This is a ternary operator replacement.
     *
     * @param condition the condition to evaluate
     * @param ifTrue supplier for value if true
     * @param ifFalse supplier for value if false
     * @param <T> the type of value
     * @return the selected value
     */
    public static <T> T choose(boolean condition, @NotNull Supplier<T> ifTrue, @NotNull Supplier<T> ifFalse) {
        return condition ? ifTrue.get() : ifFalse.get();
    }

    /**
     * Returns one of two constant values based on a condition.
     *
     * @param condition the condition to evaluate
     * @param ifTrue value if true
     * @param ifFalse value if false
     * @param <T> the type of value
     * @return the selected value
     */
    public static <T> T choose(boolean condition, T ifTrue, T ifFalse) {
        return condition ? ifTrue : ifFalse;
    }

    /**
     * Executes an action only if the condition is true.
     * This is a simple if statement replacement with auto-execution.
     *
     * @param condition the condition to evaluate
     * @param action the action to execute
     */
    public static void onlyIf(boolean condition, @NotNull Runnable action) {
        if (condition) {
            action.run();
        }
    }

    /**
     * Executes an action only if the condition is false.
     *
     * @param condition the condition to evaluate
     * @param action the action to execute
     */
    public static void unless(boolean condition, @NotNull Runnable action) {
        if (!condition) {
            action.run();
        }
    }
}