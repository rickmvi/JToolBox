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

import com.github.rickmvi.jtoolbox.debug.SLogger;
import com.github.rickmvi.jtoolbox.utils.primitives.Primitives;
import org.jetbrains.annotations.Contract;

import static com.github.rickmvi.jtoolbox.control.Conditionals.ifTrueThrow;

/**
 * @author Rick M. Viana
 * @since 1.1
 */
public class BooleanValidator {

    @Contract(pure = true)
    public boolean equals(boolean x, boolean y) {
        return x == y;
    }

    @Contract(pure = true)
    public boolean notEquals(boolean x, boolean y) {
        return !equals(x, y);
    }
    
    @Contract(pure = true)
    public boolean isTrue(boolean value) {
        return value;
    }
    
    @Contract(pure = true)
    public boolean isFalse(boolean value) {
        return !value;
    }
    
    @Contract("_ -> param1")
    public boolean ensureTrue(boolean value) {
        ifTrueThrow(!isTrue(value), Primitives::getValueMustBeTrue);
        return value;
    }

    public boolean ensureTrue(boolean value, boolean fallback) {
        if (isFalse(value)) {
            SLogger.warn("Value must be true");
            if (isFalse(fallback)) return true;
            return ensureTrue(fallback);
        }
        return ensureTrue(value);
    }

    public boolean ensureTrue(boolean value, Runnable runnable) {
        if (isFalse(value)) runnable.run();
        return ensureTrue(value);
    }
}
