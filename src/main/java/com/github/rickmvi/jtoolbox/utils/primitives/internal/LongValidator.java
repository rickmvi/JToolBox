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
package com.github.rickmvi.jtoolbox.utils.primitives.internal;

import com.github.rickmvi.jtoolbox.utils.primitives.Primitives;
import com.github.rickmvi.jtoolbox.debug.SLogger;

import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

import static com.github.rickmvi.jtoolbox.control.Conditionals.messageOrDefault;
import static com.github.rickmvi.jtoolbox.control.Conditionals.ifTrueThrow;

/**
 * @author Rick M. Viana
 * @since 1.1
 */
public class LongValidator {

    @Contract(pure = true)
    public boolean equals(long x, long y) {
        return (x ^ y) == 0L;
    }

    @Contract(pure = true)
    public boolean notEquals(long x, long y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public boolean isZero(long value) {
        return value == 0L;
    }

    @Contract(pure = true)
    public boolean isPositive(long value) {
        return value > 0L;
    }

    @Contract(pure = true)
    public boolean isNegative(long value) {
        return value < 0L;
    }

    @Contract(pure = true)
    public boolean isGreaterThan(long x, long y) {
        return x > y;
    }

    @Contract(pure = true)
    public boolean isLessThan(long x, long y) {
        return x < y;
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqual(long x, long y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public boolean isLessOrEqual(long x, long y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqualOrIsNegative(long x, long y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public boolean isLessOrEqualOrIsNegative(long x, long y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public long ensureNonNegative(long value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public long ensureNonNegative(long value, long fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            if (isNegative(fallback)) return 0L;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public long ensureNonNegative(long value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be non-negative"));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public long nonNegative(long value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public long ensurePositive(long value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public long ensurePositive(long value, long fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            if (isNonPositive(fallback)) return 1L;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public long ensurePositive(long value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be positive"));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public long nonNegativeNonZero(long value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public boolean isNonPositive(long value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public boolean isEven(long value) {
        return (value & 1L) == 0;
    }

    @Contract(pure = true)
    public boolean isOdd(long value) {
        return (value & 1L) != 0;
    }

    @Contract(pure = true)
    public long min() {
        return Long.MIN_VALUE;
    }

    @Contract(pure = true)
    public long max() {
        return Long.MAX_VALUE;
    }

    @Contract(pure = true)
    public long compare(long x, long y) {
        return Long.compare(x, y);
    }

    @Contract(pure = true)
    public long compareUnsigned(long x, long y) {
        return Long.compareUnsigned(x, y);
    }

}
