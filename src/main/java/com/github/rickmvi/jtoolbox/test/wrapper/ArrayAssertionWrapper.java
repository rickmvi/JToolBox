package com.github.rickmvi.jtoolbox.test.wrapper;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ArrayAssertionWrapper<T> extends AssertionWrapper<T[]> {

    public ArrayAssertionWrapper(T[] actual) {
        super(actual);
    }

    public ArrayAssertionWrapper<T> isEmpty() {
        if (actual == null || actual.length != 0) {
            fail("Expected empty array but was: " + Arrays.toString(actual));
        }
        return this;
    }

    public ArrayAssertionWrapper<T> isNotEmpty() {
        if (actual == null || actual.length == 0) {
            fail("Expected non-empty array but was empty");
        }
        return this;
    }

    public ArrayAssertionWrapper<T> hasSize(int expected) {
        if (actual == null) {
            fail("Expected array with size " + expected + " but was null");
        }
        if (actual.length != expected) {
            fail(String.format("Expected size <%d> but was <%d>", expected, actual.length));
        }
        return this;
    }

    public ArrayAssertionWrapper<T> contains(@NotNull T... elements) {
        if (actual == null) {
            fail("Expected to contain elements but array was null");
        }
        List<T> actualList = Arrays.asList(actual);
        for (T element : elements) {
            if (!actualList.contains(element)) {
                fail("Expected to contain: " + element);
            }
        }
        return this;
    }

    public ArrayAssertionWrapper<T> containsExactly(@NotNull T... elements) {
        if (actual == null) {
            fail("Expected to contain exactly elements but array was null");
        }
        if (!Arrays.equals(actual, elements)) {
            fail(String.format("Expected to contain exactly %s but was %s",
                    Arrays.toString(elements), Arrays.toString(actual)));
        }
        return this;
    }
}