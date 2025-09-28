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

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.function.BooleanSupplier;

/**
 * Utility interface providing do-while loop abstractions.
 * <p>
 * Allows executing actions at least once and repeating based on a condition.
 * Provides both void and value-returning variations for functional usage.
 * </p>
 *
 * <h2>Core Methods:</h2>
 * <ul>
 *     <li>{@link #runAction(BooleanSupplier, Runnable)} – Executes the action at least once and repeats while the boolean condition is true.</li>
 *     <li>{@link #supplyAction(Supplier, Predicate)} – Executes a supplier that returns a value and repeats while the predicate applied to the value returns true.</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * // Simple void do-while
 * DoWhile.runAction(() -> counter < 5, () -> {
 *     System.out.println(counter);
 *     counter++;
 * });
 *
 * // Value-returning do-while
 * int finalValue = DoWhile.supplyAction(() -> {
 *     counter++;
 *     return counter;
 * }, result -> result < 5);
 * }</pre>
 *
 * @author Rick
 * @since 1.1
 */
public interface DoWhile {

    static void runAction(@NotNull BooleanSupplier condition, @NotNull Runnable action) {
        do action.run(); while (condition.getAsBoolean());
    }

    static <T> T supplyAction(@NotNull Supplier<T> action, @NotNull Predicate<T> continueCondition) {
        T result;
        do {
            result = action.get();
        } while (continueCondition.test(result));
        return result;
    }
}
