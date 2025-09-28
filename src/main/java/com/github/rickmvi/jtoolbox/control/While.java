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

public interface While {

    static void runTrue(@NotNull BooleanSupplier condition, Runnable action) {
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
        return CompletableFuture.runAsync(() -> runTrue(condition, action));
    }
    
}
