package com.github.rickmvi.jtoolbox.control;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class While {
    /**
     * Repeatedly executes the action while the condition returns true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     */
    public void whileTrue(@NotNull BooleanSupplier condition, Runnable action) {
        while (condition.getAsBoolean()) action.run();
    }

    /**
     * Repeatedly executes the specified action while the given condition is true,
     * unless the cancel condition is met.
     *
     * @param condition the condition to evaluate before each iteration; the loop continues
     *                  executing as long as this condition returns {@code true}.
     * @param action    the action to execute on each iteration.
     * @param cancel    the cancel condition; if this condition returns {@code true},
     *                  the loop is terminated immediately.
     * @throws NullPointerException if {@code condition} or {@code cancel} is {@code null}.
     */
    public void whileTrueCancelable(@NotNull BooleanSupplier condition, Runnable action, @NotNull BooleanSupplier cancel) {
        while (condition.getAsBoolean() && !cancel.getAsBoolean()) action.run();
    }

    /**
     * Repeatedly executes the action while the condition is true and not canceled.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @param cancel    the supplier that determines if execution should be canceled
     */
    public void whileTrueCancelable(@NotNull BooleanSupplier condition, Runnable action, Supplier<Boolean> cancel) {
        while (condition.getAsBoolean() && !cancel.get()) action.run();
    }

    /**
     * Asynchronously executes the action while the condition is true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @return a {@link CompletableFuture} that completes when the loop ends
     */
    public CompletableFuture<Void> whileTrueAsync(BooleanSupplier condition, Runnable action) {
        return CompletableFuture.runAsync(() -> whileTrue(condition, action));
    }
}
