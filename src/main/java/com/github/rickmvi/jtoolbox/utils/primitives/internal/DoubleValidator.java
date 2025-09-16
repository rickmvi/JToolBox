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
 * @author Rick M. Viana
 * @since 1.1
 */
public class DoubleValidator {

    @Contract(pure = true)
    public boolean equals(double x, double y) {
        return Double.compare(x, y) == 0;
    }

    @Contract(pure = true)
    public boolean notEquals(double x, double y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public boolean isZero(double value) {
        return Double.compare(value, 0.0d) == 0;
    }

    @Contract(pure = true)
    public boolean isPositive(double value) {
        return Double.compare(value, 0.0d) > 0;
    }

    @Contract(pure = true)
    public boolean isNegative(double value) {
        return Double.compare(value, -0.0d) < 0;
    }

    @Contract(pure = true)
    public boolean isGreaterThan(double x, double y) {
        return x > y;
    }

    @Contract(pure = true)
    public boolean isLessThan(double x, double y) {
        return x < y;
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqual(double x, double y) {
        return equals(x, y) || isGreaterThan(x, y);
    }

    @Contract(pure = true)
    public boolean isLessOrEqual(double x, double y) {
        return equals(x, y) || isLessThan(x, y);
    }

    @Contract(pure = true)
    public boolean isGreaterOrEqualOrIsNegative(double x, double y) {
        return isGreaterOrEqual(x, y) || isNegative(x);
    }

    @Contract(pure = true)
    public boolean isLessOrEqualOrIsNegative(double x, double y) {
        return isLessOrEqual(x, y) || isNegative(x);
    }

    @Contract("_ -> param1")
    public double ensureNonNegative(double value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public double ensureNonNegative(double value, double fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            if (isNegative(fallback)) return 0.0d;
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public double ensureNonNegative(double value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be non-negative"));
        });
        return nonNegative(value);
    }

    @Contract(pure = true)
    public double nonNegative(double value) {
        return isNegative(value) ? 0.0 : value;
    }

    @Contract("_ -> param1")
    public double ensurePositive(double value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public double ensurePositive(double value, double fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            if (isNonPositive(fallback)) return 1.0d;
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public double ensurePositive(double value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> {
            throw new IllegalArgumentException(
                    messageOrDefault(supplierMessage, "Value must be positive"));
        });
        return nonNegativeNonZero(value);
    }

    @Contract(pure = true)
    public double nonNegativeNonZero(double value) {
        return isNonPositive(value) ? 1.0d : value;
    }

    @Contract(pure = true)
    public boolean isEven(double value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public boolean isOdd(double value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    @Contract(pure = true)
    public boolean isNonPositive(double value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public double min() {
        return Double.MIN_VALUE;
    }

    @Contract(pure = true)
    public double max() {
        return Double.MAX_VALUE;
    }

    @Contract(pure = true)
    public double compare(double x, double y) {
        return Double.compare(x, y);
    }

    @Contract(pure = true)
    public boolean isNaN(double value) {
        return Double.isNaN(value);
    }

    @Contract(pure = true)
    public boolean isInfinite(double value) {
        return Double.isInfinite(value);
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private boolean isIntegral(double value) {
        return !isNaN(value) && !isInfinite(value) && value % 1 == 0;
    }

}
