/*
 * Console API - Utilitarian library for input, output and formatting on the console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.rickmvi.jtoolbox.control;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class Repeater {
    /**
     * Repeatedly executes the action while the condition returns true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     */
    public static void whileTrue(@NotNull BooleanSupplier condition, Runnable action) {
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
    public static void whileTrueCancelable(
            @NotNull BooleanSupplier condition,
            Runnable action,
            @NotNull BooleanSupplier cancel
    ) {
        while (condition.getAsBoolean() && !cancel.getAsBoolean()) action.run();
    }

    /**
     * Repeatedly executes the action while the condition is true and not canceled.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @param cancel    the supplier that determines if execution should be canceled
     */
    public static void whileTrueCancelable(
            @NotNull BooleanSupplier condition,
            Runnable action,
            Supplier<Boolean> cancel
    ) {
        while (condition.getAsBoolean() && !cancel.get()) action.run();
    }

    /**
     * Asynchronously executes the action while the condition is true.
     *
     * @param condition the condition to evaluate before each iteration
     * @param action    the action to execute
     * @return a {@link CompletableFuture} that completes when the loop ends
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<Void> whileTrueAsync(BooleanSupplier condition, Runnable action) {
        return CompletableFuture.runAsync(() -> whileTrue(condition, action));
    }
}
