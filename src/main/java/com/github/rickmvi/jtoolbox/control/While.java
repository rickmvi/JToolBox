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

/**
 * Utility interface providing while loop abstractions.
 * <p>
 * Supports standard while loops, cancelable loops, and asynchronous execution.
 * Provides functional and fluent alternatives to traditional while-loop syntax.
 * </p>
 *
 * <h2>Core Methods:</h2>
 * <ul>
 *     <li>{@link #isTrue(BooleanSupplier, Runnable)} – Executes the action while the condition evaluates to true.</li>
 *     <li>{@link #trueCancelable(BooleanSupplier, Runnable, BooleanSupplier)} – Executes the action while the condition is true and the cancel supplier is false.</li>
 *     <li>{@link #trueCancelable(BooleanSupplier, Runnable, Supplier)} – Variation of the cancelable loop using a generic {@link Supplier} for cancellation.</li>
 *     <li>{@link #whileTrueAsync(BooleanSupplier, Runnable)} – Executes a while loop asynchronously, returning a {@link CompletableFuture}.</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Standard while loop
 * While.runTrue(() -> counter < 5, () -> counter++);
 *
 * // Cancelable loop
 * While.trueCancelable(
 *     () -> counter < 10,
 *     () -> counter++,
 *     () -> shouldCancel
 * );
 *
 * // Async loop
 * CompletableFuture<Void> future = While.whileTrueAsync(
 *     () -> counter < 5,
 *     () -> counter++
 * );
 * future.join();
 * }</pre>
 *
 * @author Rick
 * @since 1.1
 */
public interface While {

    static void isTrue(@NotNull BooleanSupplier condition, Runnable action) {
        while (condition.getAsBoolean()) action.run();
    }

    static void trueCancelable(
            @NotNull BooleanSupplier condition,
            Runnable action,
            @NotNull BooleanSupplier cancel
    ) {
        while (condition.getAsBoolean() && !cancel.getAsBoolean()) action.run();
    }

    static void trueCancelable(
            @NotNull BooleanSupplier condition,
            Runnable action,
            Supplier<Boolean> cancel
    ) {
        while (condition.getAsBoolean() && !cancel.get()) action.run();
    }

    @Contract("_, _ -> new")
    static @NotNull CompletableFuture<Void> whileTrueAsync(BooleanSupplier condition, Runnable action) {
        return CompletableFuture.runAsync(() -> isTrue(condition, action));
    }
    
}
