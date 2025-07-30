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
package com.github.rickmvi.template;

import com.github.rickmvi.console.debug.SLogger;

import java.util.Optional;
import java.util.function.Function;

@lombok.experimental.UtilityClass
@SuppressWarnings("unused")
public class TryConvert {

    public <T, R>Optional<R> convert(T value, Function<T, R> converter) {
        if (value == null) return Optional.empty();
        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            SLogger.warn(
                    "Failed to convert value: "
                            + value
                            + " to type: "
                            + converter.getClass().getSimpleName(), e
            );
            return Optional.empty();
        }
    }
}
