package com.github.rickmvi.jtoolbox.util.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
