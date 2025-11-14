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
package com.github.rickmvi.jtoolbox.util;

import com.github.rickmvi.jtoolbox.logger.Logger;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

@SuppressWarnings("unused")
public interface TypeConverter<T> {

    /**
     * Converts a given value using the provided converter function.
     * <p>
     * If the input value is null, an empty {@code Optional} is returned. If the conversion
     * fails, an error is logged, and an empty {@code Optional} is returned.
     *
     * @param <T> the type of the input value to be converted
     * @param <R> the type of the resulting value after conversion
     * @param value the input value to be converted; may be null
     * @param converter a {@code Function} that defines how to convert the input value
     * @return an {@code Optional} containing the converted value if successful, or an empty
     *         {@code Optional} if the input is null or the conversion fails
     * @throws NullPointerException if the converter function is null
     */
    @Contract("null, _ -> !null")
    static <T, R> Optional<R> convert(T value, Function<T, R> converter) {
        if (Objects.isNull(value)) return Optional.empty();
        return Try.of(() -> converter.apply(value)).onFailure(e -> Logger.error(
                "Failed to convert value: {} to type: {} ",
                e,
                value,
                converter.getClass().getSimpleName()))
                .toOptional();
    }

}
