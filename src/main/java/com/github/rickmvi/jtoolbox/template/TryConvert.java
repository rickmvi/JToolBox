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
package com.github.rickmvi.jtoolbox.template;

import com.github.rickmvi.jtoolbox.debug.SLogger;

import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class providing a safe method for attempting type conversions
 * while gracefully handling potential failures.
 * <p>
 * This class offers a robust approach to converting values from one type to another
 * by encapsulating conversion logic and handling errors internally.
 * It protects client code from unexpected runtime exceptions such as {@code NumberFormatException} or
 * {@code NullPointerException} that might occur during parsing or transformation.
 * <p>
 * Conversion results are wrapped within {@link Optional} to explicitly represent the presence
 * or absence of a valid value, encouraging functional and null-safe handling patterns.
 * <p>
 * In case of failure, the class logs detailed warnings using the integrated logging system,
 * facilitating easier debugging without interrupting program flow.
 * <p>
 * Typical use cases include safely parsing strings to numeric types, booleans,
 * or other complex transformations where invalid input or null values are expected
 * and should be handled gracefully.
 */
@lombok.experimental.UtilityClass
@SuppressWarnings("unused")
public class TryConvert {

    /**
     * Attempts to convert a given value using the provided converter function.
     * <p>
     * This method safely applies the conversion function to the input value,
     * returning an {@link Optional} containing the result if successful.
     * If the input value is {@code null} or the converter throws an exception,
     * it returns an empty {@link Optional} and logs a warning.
     * <p>
     * This utility method helps to avoid runtime exceptions during conversions
     * by encapsulating the conversion process and its possible failures.
     *
     * @param <T>       the type of the input value
     * @param <R>       the type of the conversion result
     * @param value     the input value to convert, may be {@code null}
     * @param converter a function that converts the input value to the target type
     * @return an {@link Optional} containing the converted result, or empty if input is {@code null} or conversion fails
     */
    public <T, R> Optional<R> convert(T value, Function<T, R> converter) {
        if (value == null) return Optional.empty();
        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            SLogger.warn(
                    "Failed to convert value: {} to type: {} ",
                    e,
                    value,
                    converter.getClass().getSimpleName()
            );
            return Optional.empty();
        }
    }
}
