package com.github.rickmvi.jtoolbox.test.wrapper;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CollectionAssertionWrapper<T> extends AssertionWrapper<Collection<T>> {

    public CollectionAssertionWrapper(Collection<T> actual) {
        super(actual);
    }

    public CollectionAssertionWrapper<T> isEmpty() {
        if (actual == null || !actual.isEmpty()) {
            fail("Expected empty collection but was: " + actual);
        }
        return this;
    }

    public CollectionAssertionWrapper<T> isNotEmpty() {
        if (actual == null || actual.isEmpty()) {
            fail("Expected non-empty collection but was empty");
        }
        return this;
    }

    public CollectionAssertionWrapper<T> hasSize(int expected) {
        if (actual == null) {
            fail("Expected collection with size " + expected + " but was null");
        }
        if (actual.size() != expected) {
            fail(String.format("Expected size <%d> but was <%d>", expected, actual.size()));
        }
        return this;
    }

    public CollectionAssertionWrapper<T> contains(@NotNull T... elements) {
        if (actual == null) {
            fail("Expected to contain elements but collection was null");
        }
        for (T element : elements) {
            if (!actual.contains(element)) {
                fail("Expected to contain: " + element);
            }
        }
        return this;
    }

    public CollectionAssertionWrapper<T> containsOnly(@NotNull T... elements) {
        if (actual == null) {
            fail("Expected to contain only elements but collection was null");
        }
        Set<T> expectedSet = new HashSet<>(Arrays.asList(elements));
        Set<T> actualSet = new HashSet<>(actual);

        if (!actualSet.equals(expectedSet)) {
            fail(String.format("Expected to contain only %s but was %s",
                    expectedSet, actualSet));
        }
        return this;
    }

    public CollectionAssertionWrapper<T> containsExactly(@NotNull T... elements) {
        if (actual == null) {
            fail("Expected to contain exactly elements but collection was null");
        }
        List<T> expectedList = Arrays.asList(elements);
        List<T> actualList = new ArrayList<>(actual);

        if (!actualList.equals(expectedList)) {
            fail(String.format("Expected to contain exactly %s but was %s",
                    expectedList, actualList));
        }
        return this;
    }

    public CollectionAssertionWrapper<T> doesNotContain(@NotNull T... elements) {
        if (actual == null) return this;
        for (T element : elements) {
            if (actual.contains(element)) {
                fail("Expected not to contain: " + element);
            }
        }
        return this;
    }

    public CollectionAssertionWrapper<T> containsNull() {
        if (actual == null || !actual.contains(null)) {
            fail("Expected to contain null but did not");
        }
        return this;
    }

    public CollectionAssertionWrapper<T> doesNotContainNull() {
        if (actual != null && actual.contains(null)) {
            fail("Expected not to contain null but did");
        }
        return this;
    }

    public CollectionAssertionWrapper<T> allMatch(@NotNull java.util.function.Predicate<T> predicate) {
        if (actual == null) {
            fail("Expected all elements to match predicate but collection was null");
        }
        for (T element : actual) {
            if (!predicate.test(element)) {
                fail("Not all elements match predicate. Failed on: " + element);
            }
        }
        return this;
    }

    public CollectionAssertionWrapper<T> anyMatch(@NotNull java.util.function.Predicate<T> predicate) {
        if (actual == null) {
            fail("Expected any element to match predicate but collection was null");
        }
        for (T element : actual) {
            if (predicate.test(element)) {
                return this;
            }
        }
        fail("No elements match the predicate");
        return this;
    }

    public CollectionAssertionWrapper<T> noneMatch(@NotNull java.util.function.Predicate<T> predicate) {
        if (actual == null) return this;
        for (T element : actual) {
            if (predicate.test(element)) {
                fail("Expected no elements to match predicate but found: " + element);
            }
        }
        return this;
    }
}
