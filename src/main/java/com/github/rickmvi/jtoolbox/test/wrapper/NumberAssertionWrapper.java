package com.github.rickmvi.jtoolbox.test.wrapper;

public class NumberAssertionWrapper<T extends Number & Comparable<T>> extends AssertionWrapper<T> {

    public NumberAssertionWrapper(T actual) {
        super(actual);
    }

    public NumberAssertionWrapper<T> isZero() {
        if (actual == null || actual.doubleValue() != 0.0) {
            fail("Expected zero but was: " + actual);
        }
        return this;
    }

    public NumberAssertionWrapper<T> isNotZero() {
        if (actual != null && actual.doubleValue() == 0.0) {
            fail("Expected non-zero but was zero");
        }
        return this;
    }

    public NumberAssertionWrapper<T> isPositive() {
        if (actual == null || actual.doubleValue() <= 0) {
            fail("Expected positive number but was: " + actual);
        }
        return this;
    }

    public NumberAssertionWrapper<T> isNegative() {
        if (actual == null || actual.doubleValue() >= 0) {
            fail("Expected negative number but was: " + actual);
        }
        return this;
    }

    public NumberAssertionWrapper<T> isCloseTo(T expected, T offset) {
        if (actual == null) {
            fail("Expected close to " + expected + " but was null");
        }
        double diff = Math.abs(actual.doubleValue() - expected.doubleValue());
        if (diff > offset.doubleValue()) {
            fail(String.format("Expected close to <%s> (within <%s>) but was <%s>",
                    expected, offset, actual));
        }
        return this;
    }

    public NumberAssertionWrapper<T> isStrictlyBetween(T start, T end) {
        if (actual == null) {
            fail("Expected between " + start + " and " + end + " but was null");
        }
        if (actual.compareTo(start) <= 0 || actual.compareTo(end) >= 0) {
            fail(String.format("Expected strictly between <%s> and <%s> but was <%s>",
                    start, end, actual));
        }
        return this;
    }
}