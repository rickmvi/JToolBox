package com.github.rickmvi.jtoolbox.control;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class Match {
    /**
     * Performs a switch-like operation using a map of cases. Executes the matching case or the default if not found.
     *
     * @param value       the input value to match
     * @param cases       a map of values to actions
     * @param defaultCase the action to run if no match is found
     * @param <T>         the type of the input value
     */
    public <T> void on(T value, @NotNull Map<T, Runnable> cases, Runnable defaultCase) {
        cases.getOrDefault(value, defaultCase).run();
    }

    /**
     * Performs a switch-like operation using a map of cases and returns a result from the matched case.
     *
     * @param value       the input value to match
     * @param cases       a map of values to result suppliers
     * @param defaultCase the supplier to run if no match is found
     * @param <T>         the type of the input value
     * @param <R>         the return type
     * @return the result from the matching case or the default supplier
     */
    public <T, R> R returning(T value, @NotNull Map<T, Supplier<R>> cases, Supplier<R> defaultCase) {
        return cases.getOrDefault(value, defaultCase).get();
    }

    /**
     * Asynchronously performs a switch-like operation and returns the result from the matching case.
     *
     * @param value       the input value to match
     * @param cases       a map of values to result suppliers
     * @param defaultCase the supplier to run if no match is found
     * @param <T>         the type of the input and return values
     * @return a {@link CompletableFuture} with the result of the matched case or default
     */
    public <T> CompletableFuture<T> returnAsync(T value, Map<T, Supplier<T>> cases, Supplier<T> defaultCase) {
        return CompletableFuture.supplyAsync(() -> returning(value, cases, defaultCase));
    }
}
