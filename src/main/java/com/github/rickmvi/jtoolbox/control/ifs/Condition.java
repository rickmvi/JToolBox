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
package com.github.rickmvi.jtoolbox.control.ifs;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class Condition {

    /* ========================== Executors ========================== */

    public static void ifTrue(boolean condition, Runnable action) {
        if (condition) action.run();
    }

    public static void ifFalse(boolean condition, Runnable action) {
        if (!condition) action.run();
    }

    /* ========================== Return Values ========================== */

    @Contract("false, _, _ -> param3")
    public static <T> T ifTrueReturn(boolean condition, Supplier<T> supplier, T defaultValue) {
        return condition ? supplier.get() : defaultValue;
    }

    @Contract("true, _, _ -> param3")
    public static <T> T ifFalseReturn(boolean condition, Supplier<T> supplier, T defaultValue) {
        return !condition ? supplier.get() : defaultValue;
    }

    /* ========================== Runnable-based ========================== */

    public static void runIfTrueElse(
            boolean condition,
            @NotNull Runnable whenTrue,
            @NotNull Runnable orElse
    ) {
        if (!condition) {
            orElse.run();
            return;
        }
        whenTrue.run();
    }

    public static void runIfFalseElse(
            boolean condition,
            @NotNull Runnable whenFalse,
            @NotNull Runnable orElse
    ) {
        if (condition) {
            orElse.run();
            return;
        }
        whenFalse.run();
    }


    /* ========================== Supplier-based ========================== */

    public static <R> R supplyIfTrueElse(
            boolean condition,
            @NotNull Supplier<R> whenTrue,
            @NotNull Supplier<R> orElse
    ) {
        return condition ? whenTrue.get() : orElse.get();
    }

    public static <R> R supplyIfFalseElse(
            boolean condition,
            @NotNull Supplier<R> whenFalse,
            @NotNull Supplier<R> orElse
    ) {
        return !condition ? whenFalse.get() : orElse.get();
    }

    public static <T> T supplyByCondition(
            boolean condition,
            Supplier<T> trueSupplier,
            Supplier<T> falseSupplier
    ) {
        return condition ? trueSupplier.get() : falseSupplier.get();
    }

    /* ========================== Optional-based ========================== */

    @Contract("_, _ -> !null")
    public static <R> Optional<R> optionalIfTrue(
            boolean condition,
            @NotNull Supplier<R> whenTrue
    ) {
        return condition ? Optional.ofNullable(whenTrue.get()) : Optional.empty();
    }

    @Contract("_, _ -> !null")
    public static <R> Optional<R> optionalIfFalse(
            boolean condition,
            @NotNull Supplier<R> whenFalse
    ) {
        return !condition ? Optional.ofNullable(whenFalse.get()) : Optional.empty();
    }

    /* ========================== Message Helpers ========================== */

    @Deprecated
    @Contract("null, _ -> param2")
    public static String messageOrDefault(
            Supplier<String> messageSupplier,
            String defaultMessage
    ) {
        return messageSupplier == null ? defaultMessage : messageSupplier.get();
    }

    /* ========================== Throw Helpers ========================== */

    @Deprecated
    @Contract("true, _ -> fail")
    public static void ifTrueThrow(
            boolean condition,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        if (condition) throw exceptionSupplier.get();
    }
}
