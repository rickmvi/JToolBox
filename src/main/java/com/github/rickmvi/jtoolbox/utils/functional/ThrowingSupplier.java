package com.github.rickmvi.jtoolbox.utils.functional;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
