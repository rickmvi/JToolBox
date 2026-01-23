package com.github.rickmvi.jtoolbox.test.wrapper;

import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Specialized assertion wrapper for String values.
 */
public class StringAssertionWrapper extends AssertionWrapper<String> {

    public StringAssertionWrapper(String actual) {
        super(actual);
    }

    public StringAssertionWrapper isEmpty() {
        if (actual == null || !actual.isEmpty()) {
            fail("Expected empty string but was: " + actual);
        }
        return this;
    }

    public StringAssertionWrapper isNotEmpty() {
        if (actual == null || actual.isEmpty()) {
            fail("Expected non-empty string but was empty");
        }
        return this;
    }

    public StringAssertionWrapper isBlank() {
        if (actual == null || !actual.isBlank()) {
            fail("Expected blank string but was: " + actual);
        }
        return this;
    }

    public StringAssertionWrapper isNotBlank() {
        if (actual == null || actual.isBlank()) {
            fail("Expected non-blank string but was blank");
        }
        return this;
    }

    public StringAssertionWrapper hasLength(int expected) {
        if (actual == null) {
            fail("Expected string with length " + expected + " but was null");
        }
        if (actual.length() != expected) {
            fail(String.format("Expected length <%d> but was <%d>", expected, actual.length()));
        }
        return this;
    }

    public StringAssertionWrapper contains(@NotNull CharSequence... sequences) {
        if (actual == null) {
            fail("Expected to contain sequences but string was null");
        }
        for (CharSequence seq : sequences) {
            if (!actual.contains(seq)) {
                fail("Expected to contain: " + seq);
            }
        }
        return this;
    }

    public StringAssertionWrapper doesNotContain(@NotNull CharSequence... sequences) {
        if (actual == null) return this;
        for (CharSequence seq : sequences) {
            if (actual.contains(seq)) {
                fail("Expected not to contain: " + seq);
            }
        }
        return this;
    }

    public StringAssertionWrapper startsWith(@NotNull String prefix) {
        if (actual == null || !actual.startsWith(prefix)) {
            fail("Expected to start with: " + prefix);
        }
        return this;
    }

    public StringAssertionWrapper endsWith(@NotNull String suffix) {
        if (actual == null || !actual.endsWith(suffix)) {
            fail("Expected to end with: " + suffix);
        }
        return this;
    }

    public StringAssertionWrapper matches(@NotNull String regex) {
        if (actual == null || !actual.matches(regex)) {
            fail("Expected to match regex: " + regex);
        }
        return this;
    }

    public StringAssertionWrapper matches(@NotNull Pattern pattern) {
        if (actual == null || !pattern.matcher(actual).matches()) {
            fail("Expected to match pattern: " + pattern);
        }
        return this;
    }

    public StringAssertionWrapper containsIgnoringCase(@NotNull String sequence) {
        if (actual == null || !actual.toLowerCase().contains(sequence.toLowerCase())) {
            fail("Expected to contain (ignoring case): " + sequence);
        }
        return this;
    }

    public StringAssertionWrapper isEqualToIgnoringCase(@NotNull String expected) {
        if (actual == null || !actual.equalsIgnoreCase(expected)) {
            fail(String.format("Expected (ignoring case) <%s> but was <%s>", expected, actual));
        }
        return this;
    }

    public StringAssertionWrapper isEqualToIgnoringWhitespace(@NotNull String expected) {
        String actualTrimmed = actual == null ? null : actual.replaceAll("\\s+", "");
        String expectedTrimmed = expected.replaceAll("\\s+", "");
        if (!Objects.equals(actualTrimmed, expectedTrimmed)) {
            fail(String.format("Expected (ignoring whitespace) <%s> but was <%s>", expected, actual));
        }
        return this;
    }
}
