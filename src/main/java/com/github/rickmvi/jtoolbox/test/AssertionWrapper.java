package com.github.rickmvi.jtoolbox.test;

import com.github.rickmvi.jtoolbox.control.Condition;
import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import org.jetbrains.annotations.NotNull;

public class AssertionWrapper<T> {

    private final T actual;

    public AssertionWrapper(T actual) {
        this.actual = actual;
    }

    public AssertionWrapper<T> isNotNull() {
        Condition.ThrowWhen(actual == null, () -> new AssertionException("Expected not null, but was null"));
        return this;
    }

    public AssertionWrapper<T> isNull() {
        Condition.ThrowWhen(actual != null, () -> new AssertionException("Expected null, but was not null"));
        return this;
    }

    public AssertionWrapper<T> isEqualTo(T expected) {
        Condition.ThrowWhen(actual == null || !actual.equals(expected), () ->
                new AssertionException("Expected " + expected + ", but was " + actual));
        return this;
    }

    public AssertionWrapper<T> isNotEqualTo(T expected) {
        Condition.ThrowWhen(actual != null && actual.equals(expected), () ->
                new AssertionException("Expected not " + expected + ", but was " + actual));
        return this;
    }

    public AssertionWrapper<T> isInstanceOf(@NotNull Class<?> expected) {
        Condition.ThrowWhen(!expected.isInstance(actual), () ->
                new AssertionException("Expected " + expected.getName() + ", but was " + actual.getClass().getName()));
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isGreaterThan(T limit) {
        Condition.ThrowWhen(!(actual instanceof Comparable), () -> new AssertionException("The real value is not comparable"));
        Condition.ThrowWhen(((Comparable<T>) actual).compareTo(limit) <= 0, () -> new AssertionException("Expected " + limit + ", but was " + actual));
        return this;
    }

    @SuppressWarnings("unchecked")
    public AssertionWrapper<T> isLessThan(T limit) {
        Condition.ThrowWhen(!(actual instanceof Comparable), () -> new AssertionException("The real value is not comparable"));
        Condition.ThrowWhen(((Comparable<T>) actual).compareTo(limit) >= 0, () -> new AssertionException("Expected " + limit + ", but was " + actual));
        return this;
    }

    public AssertionWrapper<T> isTrue() {
        Condition.ThrowWhen(!(actual instanceof Boolean) || !((Boolean) actual), () -> new AssertionException("Expected true, but was false"));
        return this;
    }

    public AssertionWrapper<T> isFalse() {
        Condition.ThrowWhen(!(actual instanceof Boolean) || ((Boolean) actual), () -> new AssertionException("Expected false, but was true"));
        return this;
    }

    public AssertionWrapper<T> isSameAs(T expected) {
        Condition.ThrowWhen(actual != expected, () -> new AssertionException("Expected " + expected + ", but was " + actual));
        return this;
    }

    public AssertionWrapper<T> isNotSameAs(T expected) {
        Condition.ThrowWhen(actual == expected, () -> new AssertionException("Expected not " + expected + ", but was " + actual));
        return this;
    }
}
