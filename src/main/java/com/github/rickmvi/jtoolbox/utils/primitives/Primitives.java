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
package com.github.rickmvi.jtoolbox.utils.primitives;

import com.github.rickmvi.jtoolbox.utils.primitives.internal.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.github.rickmvi.jtoolbox.text.Formatted.format;

/**
 * @author Rick M. Viana
 * @since 1.1
 */
public final class Primitives {

    public static final ByteValidator    bytes    = new ByteValidator();
    public static final ShortValidator   shorts   = new ShortValidator();
    public static final IntegerValidator integers = new IntegerValidator();
    public static final LongValidator    longs    = new LongValidator();
    public static final FloatValidator   floats   = new FloatValidator();
    public static final DoubleValidator  doubles  = new DoubleValidator();
    public static final BooleanValidator booleans = new BooleanValidator();
    public static final CharValidator    chars    = new CharValidator();

    @Contract(value = " -> fail", pure = true)
    private Primitives() {
        throw new AssertionError(
                format("No {}.Primitives instances for you!", this.getClass().getPackage().getName())
        );
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    public static @NotNull IllegalArgumentException getValueCannotBeNegative() {
        return new IllegalArgumentException("Value must be non-negative");
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    public static @NotNull IllegalArgumentException getValueMustBePositive() {
        return new IllegalArgumentException("Value must be positive");
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    public static @NotNull IllegalArgumentException getValueMustBeTrue() {
        return new IllegalArgumentException("Value must be true");
    }
}
