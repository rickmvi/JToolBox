package com.github.rickmvi.jtoolbox.test.wrapper;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class MapAssertionWrapper<K, V> extends AssertionWrapper<Map<K, V>> {

    public MapAssertionWrapper(Map<K, V> actual) {
        super(actual);
    }

    public MapAssertionWrapper<K, V> isEmpty() {
        if (actual == null || !actual.isEmpty()) {
            fail("Expected empty map but was: " + actual);
        }
        return this;
    }

    public MapAssertionWrapper<K, V> isNotEmpty() {
        if (actual == null || actual.isEmpty()) {
            fail("Expected non-empty map but was empty");
        }
        return this;
    }

    public MapAssertionWrapper<K, V> hasSize(int expected) {
        if (actual == null) {
            fail("Expected map with size " + expected + " but was null");
        }
        if (actual.size() != expected) {
            fail(String.format("Expected size <%d> but was <%d>", expected, actual.size()));
        }
        return this;
    }

    public MapAssertionWrapper<K, V> containsKey(@NotNull K key) {
        if (actual == null || !actual.containsKey(key)) {
            fail("Expected to contain key: " + key);
        }
        return this;
    }

    public MapAssertionWrapper<K, V> containsKeys(@NotNull K... keys) {
        if (actual == null) {
            fail("Expected to contain keys but map was null");
        }
        for (K key : keys) {
            if (!actual.containsKey(key)) {
                fail("Expected to contain key: " + key);
            }
        }
        return this;
    }

    public MapAssertionWrapper<K, V> doesNotContainKey(@NotNull K key) {
        if (actual != null && actual.containsKey(key)) {
            fail("Expected not to contain key: " + key);
        }
        return this;
    }

    public MapAssertionWrapper<K, V> containsValue(@NotNull V value) {
        if (actual == null || !actual.containsValue(value)) {
            fail("Expected to contain value: " + value);
        }
        return this;
    }

    public MapAssertionWrapper<K, V> containsEntry(@NotNull K key, @NotNull V value) {
        if (actual == null) {
            fail("Expected to contain entry but map was null");
        }
        if (!actual.containsKey(key)) {
            fail("Expected to contain key: " + key);
        }
        if (!Objects.equals(actual.get(key), value)) {
            fail(String.format("Expected key <%s> to have value <%s> but was <%s>",
                    key, value, actual.get(key)));
        }
        return this;
    }
}