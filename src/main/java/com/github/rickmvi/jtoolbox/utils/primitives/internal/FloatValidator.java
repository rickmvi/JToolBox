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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

import static com.github.rickmvi.jtoolbox.control.Conditionals.messageOrDefault;
import static com.github.rickmvi.jtoolbox.control.Conditionals.ifTrueThrow;

/**
 * @author Rick M.Viana
 * @since 1.1
 */
public class FloatValidator {

    @Contract(pure = true)
    public boolean equals(float x, float y) {
        return Float.compare(x, y) == 0f;
    }

    @Contract(pure = true)
    public boolean notEquals(float x, float y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public boolean isZero(float value) {
        return value == 0f;
    }

    @Contract(pure = true)
    public boolean isPositive(float value) {
        return value > 0f;
    }

    @Contract(pure = true)
    public boolean isNegative(float value) {
        return value < 0f;
    }

    @Contract(pure = true)
    public boolean isGreaterThan(float x, float y) {
        return x > y;
    }

    @Contract(pure = true)
    public boolean isLessThan(float x, float y) {
        return x < y;
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqual(float x, float y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public boolean isLessOrEqual(float x, float y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqualOrIsNegative(float x, float y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public boolean isLessOrEqualOrIsNegative(float x, float y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract(value = "_ -> param1", pure = true)
    public float ensureNonNegative(float value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public float ensureNonNegative(float value, float fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            if (isNegative(fallback)) return 0.0f;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public float ensureNonNegative(float value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> {
            throw new
                    IllegalArgumentException(messageOrDefault(supplierMessage, "Value must be non-negative"));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public float nonNegative(float value) {
        return isNegative(value) ? 0.0f : value;
    }

    @Contract("_ -> param1")
    public float ensurePositive(float value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public float ensurePositive(float value, float fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            if (isNonPositive(fallback)) return 1.0f;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public float ensurePositive(float value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be positive"));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public float nonNegativeNonZero(float value) {
        return isNonPositive(value) ? 1.0f : value;
    }

    @Contract(pure = true)
    public boolean isNonPositive(float value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public boolean isEven(float value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public boolean isOdd(float value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    public float min() {
        return Float.MIN_VALUE;
    }

    public float max() {
        return Float.MAX_VALUE;
    }

    @Contract(pure = true)
    public float compare(float x, float y) {
        return Float.compare(x, y);
    }

    @Contract(pure = true)
    public boolean isNaN(float value) {
        return Float.isNaN(value);
    }

    @Contract(pure = true)
    public boolean isInfinite(float value) {
        return Float.isInfinite(value);
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private boolean isIntegral(float value) {
        return !isNaN(value) && !isInfinite(value) && value % 1 == 0;
    }

}
