package com.github.rickmvi.jtoolbox.util.function;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Throwable;
}
