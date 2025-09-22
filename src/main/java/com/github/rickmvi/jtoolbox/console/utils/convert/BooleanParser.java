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
package com.github.rickmvi.jtoolbox.console.utils.convert;

import com.github.rickmvi.jtoolbox.utils.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility class for safely converting {@link String} values to {@code boolean} primitives or {@link Boolean} objects.
 * <p>
 * This class provides convenient methods to parse strings into boolean values with support for:
 * <ul>
 *     <li>Default fallback values when the input is {@code null} or invalid.</li>
 *     <li>Lazy evaluation of fallback values via {@link Supplier}.</li>
 *     <li>Returning the conversion result wrapped in {@link Optional} for functional-style handling.</li>
 * </ul>
 * <p>
 * All conversions internally use {@link Try} to handle conversion exceptions gracefully,
 * preventing runtime errors and facilitating robust input parsing from unreliable sources.
 * <p>
 * Typical use cases include parsing user input, configuration values, or environment variables where boolean semantics are needed.
 */
@lombok.experimental.UtilityClass
public class BooleanParser {

    /**
     * Converts the given {@link String} to a primitive {@code boolean}.
     * <p>
     * If the input is {@code null} or cannot be parsed, this method returns {@code false} as the default fallback.
     *
     * @param value the string value to convert, may be {@code null}
     * @return the boolean value parsed from the string, or {@code false} if input is {@code null} or invalid
     */
    public static boolean toBoolean(@Nullable String value) {
        return Try.convert(value, Boolean::parseBoolean).orElse(false);
    }

    /**
     * Converts the given {@link String} to a primitive {@code boolean}.
     * <p>
     * If the input is {@code null} or cannot be parsed, this method returns the specified {@code fallback} value.
     *
     * @param value    the string value to convert, may be {@code null}
     * @param fallback the fallback boolean value to return if input is {@code null} or invalid
     * @return the boolean value parsed from the string, or the provided fallback value on failure
     */
    public static boolean toBoolean(@Nullable String value, boolean fallback) {
        return Try.convert(value, Boolean::parseBoolean).orElse(fallback);
    }

    /**
     * Converts the given {@link String} to a primitive {@code boolean}.
     * <p>
     * If the input is {@code null} or cannot be parsed, this method returns the value
     * supplied by the given {@link Supplier} {@code fallback}.
     * <p>
     * This allows lazy evaluation of the fallback value.
     *
     * @param value    the string value to convert, may be {@code null}
     * @param fallback a supplier that provides the fallback boolean value if input is {@code null} or invalid, must not be {@code null}
     * @return the boolean value parsed from the string, or the supplied fallback value on failure
     */
    public static boolean toBoolean(@Nullable String value, @NotNull Supplier<Boolean> fallback) {
        return Try.convert(value, Boolean::parseBoolean).orElseGet(fallback);
    }

    /**
     * Converts the given {@link String} to an {@link Optional} {@link Boolean}.
     * <p>
     * Returns an empty {@code Optional} if the input is {@code null} or conversion fails.
     *
     * @param value the string value to convert, may be {@code null}
     * @return an {@code Optional} containing the boolean value if conversion succeeds; otherwise, an empty {@code Optional}
     */
    @Contract("null -> !null")
    public static Optional<Boolean> toBooleanOptional(@Nullable String value) {
        return Try.convert(value, Boolean::parseBoolean);
    }
}