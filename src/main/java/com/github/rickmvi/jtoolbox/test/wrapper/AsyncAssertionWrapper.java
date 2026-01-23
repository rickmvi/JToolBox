package com.github.rickmvi.jtoolbox.test.wrapper;

import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncAssertionWrapper<T> {

    @Getter(value = AccessLevel.PRIVATE)
    private final CompletableFuture<T> future;

    public AsyncAssertionWrapper(CompletableFuture<T> future) {
        this.future = future;
    }

    public AssertionWrapper<T> resolvesTo() {
        return new AssertionWrapper<>(future.join());
    }

    public <E extends Throwable> AsyncAssertionWrapper<T> resolvesWithException(Class<E> expectedType) {
        try {
            future.get();
            throw new AssertionException("Expected failure with exception, but resolved successfully.");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (!expectedType.isInstance(cause)) {
                throw new AssertionException("Expected exception of type " + expectedType.getName() +
                        ", but failure with " + cause.getClass().getName(), cause);
            }
            return this;
        } catch (InterruptedException e) {
            throw new AssertionException("Future was stopped before it failed.", e);
        }
    }
}
