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
package com.github.rickmvi.console.convert;

import com.github.rickmvi.jtoolbox.template.TryConvert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class StringToBoolean {

    public boolean toBoolean(@Nullable String value) {
        return TryConvert.convert(value, Boolean::parseBoolean).orElse(false);
    }

    public boolean toBoolean(@Nullable String value, boolean fallback) {
        return TryConvert.convert(value, Boolean::parseBoolean).orElse(fallback);
    }

    public boolean toBoolean(@Nullable String value, @NotNull Supplier<Boolean> fallback) {
        return TryConvert.convert(value, Boolean::parseBoolean).orElseGet(fallback);
    }

    public Optional<Boolean> toBooleanOptional(@Nullable String value) {
        return TryConvert.convert(value, Boolean::parseBoolean);
    }
}
