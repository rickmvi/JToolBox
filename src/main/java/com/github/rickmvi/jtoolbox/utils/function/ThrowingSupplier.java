package com.github.rickmvi.jtoolbox.utils.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
