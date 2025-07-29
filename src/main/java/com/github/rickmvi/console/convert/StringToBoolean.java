/*
 * Console API - Biblioteca utilitária para entrada, saída e formatação no console.
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class StringToBoolean {

    public boolean toBoolean(@Nullable String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean toBoolean(@Nullable String value, boolean fallback) {
        try {
            return value != null ? Boolean.parseBoolean(value) : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public boolean toBoolean(@Nullable String value, @NotNull Supplier<Boolean> fallback) {
        try {
            return value != null ? Boolean.parseBoolean(value) : fallback.get();
        } catch (Exception e) {
            return fallback.get();
        }
    }

    public Optional<Boolean> toBooleanOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Boolean.parseBoolean(value)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
