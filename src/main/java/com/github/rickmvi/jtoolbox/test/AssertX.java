package com.github.rickmvi.jtoolbox.test;

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@UtilityClass
public class AssertX {

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull AssertionWrapper<T> that(T actual) {
        return new AssertionWrapper<>(actual);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull AsyncAssertionWrapper<T> assertAsync(CompletableFuture<T> future) {
        return new AsyncAssertionWrapper<>(future);
    }

    public static <T extends Throwable> T throwsException(BooleanSupplier operation, Class<T> expectedType) {
        try {
            operation.getAsBoolean();
        } catch (Throwable e) {
            If.ThrowWhen(!expectedType.isInstance(e), () -> new AssertionException("Expected exception of type " + expectedType.getName() + ", but was " + e.getClass().getName()));
            return expectedType.cast(e);
        }
        throw new AssertionException("Expected exception of type " + expectedType.getName() + ", but no exception was thrown");
    }
}
