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
public class ShortValidator {

    @Contract(pure = true)
    public boolean equals(short x, short y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public boolean notEquals(short x, short y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public boolean isZero(short value) {
        return value == 0;
    }

    @Contract(pure = true)
    public boolean isPositive(short value) {
        return value > 0;
    }

    @Contract(pure = true)
    public boolean isNegative(short value) {
        return value < 0;
    }

    @Contract(pure = true)
    public boolean isGreaterThan(short x, short y) {
        return x > y;
    }

    @Contract(pure = true)
    public boolean isLessThan(short x, short y) {
        return x < y;
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqual(short x, short y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public boolean isLessOrEqual(short x, short y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqualOrIsNegative(short x, short y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public boolean isLessOrEqualOrIsNegative(short x, short y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public short ensureNonNegative(short value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public short ensureNonNegative(short value, short fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            if (isNegative(fallback)) return 0;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public short ensureNonNegative(short value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be non-negative"));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public short nonNegative(short value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract("_ -> param1")
    public short ensurePositive(short value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public short ensurePositive(short value, short fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            if (isNonPositive(fallback)) return 1;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public short ensurePositive(short value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be positive"));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public short nonNegativeNonZero(short value) {
        return isNonPositive(value) ? 1 : value;
    }

    @Contract(pure = true)
    public boolean isEven(short value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public boolean isOdd(short value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public boolean isNonPositive(short value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public short min() {
        return Short.MIN_VALUE;
    }

    @Contract(pure = true)
    public short max() {
        return Short.MAX_VALUE;
    }

    @Contract(pure = true)
    public int compare(short x, short y) {
        return Short.compare(x, y);
    }

    @Contract(pure = true)
    public int compareUnsigned(short x, short y) {
        return Short.compareUnsigned(x, y);
    }

}
