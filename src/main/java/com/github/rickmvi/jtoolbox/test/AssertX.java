package com.github.rickmvi.jtoolbox.test;

import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import com.github.rickmvi.jtoolbox.test.wrapper.*;
import com.github.rickmvi.jtoolbox.text.Stringifier;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Advanced assertion framework providing fluent API for test validations.
 * Inspired by AssertJ and JUnit 5, with comprehensive assertion methods.
 *
 * @author Rick M. Viana
 * @version 2.0
 * @since 2026
 */
@UtilityClass
public class AssertX {

    // ==================== Entry Points ====================

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull AssertionWrapper<T> that(T actual) {
        return new AssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull StringAssertionWrapper that(String actual) {
        return new StringAssertionWrapper(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull NumberAssertionWrapper<Integer> that(Integer actual) {
        return new NumberAssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull NumberAssertionWrapper<Long> that(Long actual) {
        return new NumberAssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull NumberAssertionWrapper<Double> that(Double actual) {
        return new NumberAssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull CollectionAssertionWrapper<T> that(Collection<T> actual) {
        return new CollectionAssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull ArrayAssertionWrapper<T> that(T[] actual) {
        return new ArrayAssertionWrapper<>(actual);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static <K, V> @NotNull MapAssertionWrapper<K, V> that(Map<K, V> actual) {
        return new MapAssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull AsyncAssertionWrapper<T> thatAsync(CompletableFuture<T> future) {
        return new AsyncAssertionWrapper<>(future);
    }

    // ==================== Exception Assertions ====================

    public static <T extends Throwable> T assertThrows(
            Class<T> expectedType,
            @NotNull Runnable operation
    ) {
        return assertThrows(expectedType, operation, null);
    }

    public static <T extends Throwable> T assertThrows(
            Class<T> expectedType,
            @NotNull Runnable operation,
            @Nullable String message
    ) {
        try {
            operation.run();
            throw new AssertionException(formatMessage(message,
                    "Expected exception of type " + expectedType.getName() + " but nothing was thrown"));
        } catch (Throwable e) {
            if (!expectedType.isInstance(e)) {
                throw new AssertionException(formatMessage(message,
                        "Expected exception of type " + expectedType.getName() +
                                " but was " + e.getClass().getName()), e);
            }
            return expectedType.cast(e);
        }
    }

    public static void assertDoesNotThrow(@NotNull Runnable operation) {
        assertDoesNotThrow(operation, null);
    }

    public static void assertDoesNotThrow(@NotNull Runnable operation, @Nullable String message) {
        try {
            operation.run();
        } catch (Throwable e) {
            throw new AssertionException(formatMessage(message,
                    "Expected no exception but caught: " + e.getClass().getName()), e);
        }
    }

    // ==================== Timeout Assertions ====================

    public static void assertTimeout(@NotNull Duration timeout, @NotNull Runnable operation) {
        assertTimeout(timeout, operation, null);
    }

    public static void assertTimeout(
            @NotNull Duration timeout,
            @NotNull Runnable operation,
            @Nullable String message
    ) {
        long startTime = System.nanoTime();
        operation.run();
        long duration = System.nanoTime() - startTime;

        if (duration > timeout.toNanos()) {
            throw new AssertionException(formatMessage(message,
                    String.format("Execution exceeded timeout of %dms (took %dms)",
                            timeout.toMillis(), duration / 1_000_000)));
        }
    }

    // ==================== Direct Assertions ====================

    public static void assertTrue(boolean condition) {
        assertTrue(condition, null);
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionException(formatMessage(message, "Expected true but was false"));
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, null);
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionException(formatMessage(message, "Expected false but was true"));
        }
    }

    public static void assertNull(Object actual) {
        assertNull(actual, null);
    }

    public static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionException(formatMessage(message,
                    "Expected null but was: " + actual));
        }
    }

    public static void assertNotNull(Object actual) {
        assertNotNull(actual, null);
    }

    public static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionException(formatMessage(message, "Expected not null but was null"));
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, null);
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionException(formatMessage(message,
                    String.format("Expected <%s> but was <%s>", expected, actual)));
        }
    }

    public static void assertNotEquals(Object unexpected, Object actual) {
        assertNotEquals(unexpected, actual, null);
    }

    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        if (Objects.equals(unexpected, actual)) {
            throw new AssertionException(formatMessage(message,
                    String.format("Expected not equal to <%s>", unexpected)));
        }
    }

    public static void assertSame(Object expected, Object actual) {
        assertSame(expected, actual, null);
    }

    public static void assertSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new AssertionException(formatMessage(message,
                    "Expected same instance but were different"));
        }
    }

    public static void assertNotSame(Object unexpected, Object actual) {
        assertNotSame(unexpected, actual, null);
    }

    public static void assertNotSame(Object unexpected, Object actual, String message) {
        if (unexpected == actual) {
            throw new AssertionException(formatMessage(message,
                    "Expected different instances but were same"));
        }
    }

    public static void fail() {
        fail("Test failed");
    }

    public static void fail(String message) {
        throw new AssertionException(message);
    }

    public static void fail(String message, Throwable cause) {
        throw new AssertionException(message, cause);
    }

    // ==================== Utility Methods ====================

    private static String formatMessage(@Nullable String userMessage, String defaultMessage) {
        return userMessage != null && !userMessage.isEmpty()
                ? Stringifier.format("{} ==> {}", userMessage, defaultMessage)
                : defaultMessage;
    }

    static void throwAssertionException(String message) {
        throw new AssertionException(message);
    }

    static void throwAssertionException(String message, Throwable cause) {
        throw new AssertionException(message, cause);
    }
}