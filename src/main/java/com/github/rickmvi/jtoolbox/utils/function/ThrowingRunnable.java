package com.github.rickmvi.jtoolbox.utils.function;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Throwable;
}
