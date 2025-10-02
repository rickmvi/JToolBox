package com.github.rickmvi.jtoolbox.utils.functional;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Throwable;
}
