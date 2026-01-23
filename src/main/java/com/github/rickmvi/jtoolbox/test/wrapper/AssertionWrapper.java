package com.github.rickmvi.jtoolbox.test.wrapper;

import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Enhanced assertion wrapper with fluent API and custom error messages.
 */
public class AssertionWrapper<T> {

    @Getter
    protected final T actual;
    protected String customMessage;

    public AssertionWrapper(T actual) {
        this.actual = actual;
    }

    // ==================== Custom Messages ====================

    public AssertionWrapper<T> withMessage(String message) {
        this.customMessage = message;
        return this;
    }

    public AssertionWrapper<T> as(String description) {
        return withMessage(description);
    }

    // ==================== Null Checks ====================

    public AssertionWrapper<T> isNotNull() {
        if (actual == null) {
            fail("Expected not null but was null");
        }
        return this;
    }

    public AssertionWrapper<T> isNull() {
        if (actual != null) {
            fail("Expected null but was: " + actual);
        }
        return this;
    }

    // ==================== Equality ====================

    public AssertionWrapper<T> isEqualTo(T expected) {
        if (!Objects.equals(actual, expected)) {
            fail(String.format("Expected <%s> but was <%s>", expected, actual));
        }
        return this;
    }

    public AssertionWrapper<T> isNotEqualTo(T expected) {
        if (Objects.equals(actual, expected)) {
            fail(String.format("Expected not equal to <%s>", expected));
        }
        return this;
    }

    // ==================== Identity ====================

    public AssertionWrapper<T> isSameAs(T expected) {
        if (actual != expected) {
            fail("Expected same instance but were different");
        }
        return this;
    }

    public AssertionWrapper<T> isNotSameAs(T expected) {
        if (actual == expected) {
            fail("Expected different instances but were same");
        }
        return this;
    }

    // ==================== Type Checks ====================

    public AssertionWrapper<T> isInstanceOf(@NotNull Class<?> expected) {
        if (actual == null) {
            fail("Expected instance of " + expected.getName() + " but was null");
        }
        if (!expected.isInstance(actual)) {
            fail(String.format("Expected instance of <%s> but was <%s>",
                    expected.getName(), actual.getClass().getName()));
        }
        return this;
    }

    public AssertionWrapper<T> isNotInstanceOf(@NotNull Class<?> type) {
        if (actual != null && type.isInstance(actual)) {
            fail(String.format("Expected not instance of <%s>", type.getName()));
        }
        return this;
    }

    public AssertionWrapper<T> isExactlyInstanceOf(@NotNull Class<?> expected) {
        if (actual == null) {
            fail("Expected exact instance of " + expected.getName() + " but was null");
        }
        if (actual.getClass() != expected) {
            fail(String.format("Expected exact instance of <%s> but was <%s>",
                    expected.getName(), actual.getClass().getName()));
        }
        return this;
    }

    // ==================== Comparable ====================

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isGreaterThan(T limit) {
        ensureComparable();
        if (((Comparable<T>) actual).compareTo(limit) <= 0) {
            fail(String.format("Expected greater than <%s> but was <%s>", limit, actual));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isGreaterThanOrEqualTo(T limit) {
        ensureComparable();
        if (((Comparable<T>) actual).compareTo(limit) < 0) {
            fail(String.format("Expected greater than or equal to <%s> but was <%s>", limit, actual));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isLessThan(T limit) {
        ensureComparable();
        if (((Comparable<T>) actual).compareTo(limit) >= 0) {
            fail(String.format("Expected less than <%s> but was <%s>", limit, actual));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isLessThanOrEqualTo(T limit) {
        ensureComparable();
        if (((Comparable<T>) actual).compareTo(limit) > 0) {
            fail(String.format("Expected less than or equal to <%s> but was <%s>", limit, actual));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isBetween(T start, T end) {
        ensureComparable();
        Comparable<T> comp = (Comparable<T>) actual;
        if (comp.compareTo(start) < 0 || comp.compareTo(end) > 0) {
            fail(String.format("Expected between <%s> and <%s> but was <%s>", start, end, actual));
        }
        return this;
    }

    // ==================== Boolean ====================

    public AssertionWrapper<T> isTrue() {
        if (!(actual instanceof Boolean) || !((Boolean) actual)) {
            fail("Expected true but was " + actual);
        }
        return this;
    }

    public AssertionWrapper<T> isFalse() {
        if (!(actual instanceof Boolean) || ((Boolean) actual)) {
            fail("Expected false but was " + actual);
        }
        return this;
    }

    // ==================== Custom Conditions ====================

    public AssertionWrapper<T> matches(@NotNull Predicate<T> predicate) {
        return matches(predicate, "Expected to match predicate");
    }

    public AssertionWrapper<T> matches(@NotNull Predicate<T> predicate, String description) {
        if (!predicate.test(actual)) {
            fail(description + " but was: " + actual);
        }
        return this;
    }

    public AssertionWrapper<T> doesNotMatch(@NotNull Predicate<T> predicate) {
        return doesNotMatch(predicate, "Expected not to match predicate");
    }

    public AssertionWrapper<T> doesNotMatch(@NotNull Predicate<T> predicate, String description) {
        if (predicate.test(actual)) {
            fail(description + " but did: " + actual);
        }
        return this;
    }

    // ==================== Special Values ====================

    public AssertionWrapper<T> isIn(@NotNull T... values) {
        for (T value : values) {
            if (Objects.equals(actual, value)) {
                return this;
            }
        }
        fail(String.format("Expected one of %s but was <%s>",
                java.util.Arrays.toString(values), actual));
        return this;
    }

    public AssertionWrapper<T> isNotIn(@NotNull T... values) {
        for (T value : values) {
            if (Objects.equals(actual, value)) {
                fail(String.format("Expected not in %s but was <%s>",
                        java.util.Arrays.toString(values), actual));
            }
        }
        return this;
    }

    // ==================== Utility Methods ====================

    private void ensureComparable() {
        if (!(actual instanceof Comparable)) {
            fail("Value is not comparable: " + actual.getClass().getName());
        }
    }

    protected void fail(String message) {
        String fullMessage = customMessage != null
                ? customMessage + " ==> " + message
                : message;
        throw new AssertionException(fullMessage);
    }

    @Override
    public String toString() {
        return "AssertionWrapper[" + actual + "]";
    }
}