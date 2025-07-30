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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.Optional;

@lombok.experimental.UtilityClass
public class StringToNumber {

    public int toInt(@Nullable String value) {
        return toInt(value, 0);
    }

    public long toLong(@Nullable String value) {
        return toLong(value, 0L);
    }

    public double toDouble(@Nullable String value) {
        return toDouble(value, 0.0);
    }

    public float toFloat(@Nullable String value) {
        return toFloat(value, 0.0f);
    }

    public int toInt(@Nullable String value, int fallback) {
        return TryConvert.convert(value, Integer::parseInt).orElse(fallback);
    }

    public long toLong(@Nullable String value, long fallback) {
        return TryConvert.convert(value, Long::parseLong).orElse(fallback);
    }

    public double toDouble(@Nullable String value, double fallback) {
        return TryConvert.convert(value, Double::parseDouble).orElse(fallback);
    }

    public float toFloat(@Nullable String value, float fallback) {
        return TryConvert.convert(value, Float::parseFloat).orElse(fallback);
    }

    public int toInt(@Nullable String value, @NotNull Supplier<Integer> fallback) {
        return TryConvert.convert(value, Integer::parseInt).orElseGet(fallback);
    }

    public long toLong(@Nullable String value, @NotNull Supplier<Long> fallback) {
        return TryConvert.convert(value, Long::parseLong).orElseGet(fallback);
    }

    public double toDouble(@Nullable String value, @NotNull Supplier<Double> fallback) {
        return TryConvert.convert(value, Double::parseDouble).orElseGet(fallback);
    }

    public float toFloat(@Nullable String value, @NotNull Supplier<Float> fallback) {
        return TryConvert.convert(value, Float::parseFloat).orElseGet(fallback);
    }

    public Optional<Integer> toIntOptional(@Nullable String value) {
        return TryConvert.convert(value, Integer::parseInt);
    }

    public Optional<Long> toLongOptional(@Nullable String value) {
        return TryConvert.convert(value, Long::parseLong);
    }

    public Optional<Double> toDoubleOptional(@Nullable String value) {
        return TryConvert.convert(value, Double::parseDouble);
    }

    public Optional<Float> toFloatOptional(@Nullable String value) {
        return TryConvert.convert(value, Float::parseFloat);
    }
}
