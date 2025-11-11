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
package com.github.rickmvi.jtoolbox.util.convert;

import com.github.rickmvi.jtoolbox.util.SafeRun;
import lombok.experimental.UtilityClass;
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
 * All conversions internally use {@link SafeRun} to handle conversion exceptions gracefully,
 * preventing runtime errors and facilitating robust input parsing from unreliable sources.
 * <p>
 * Typical use cases include parsing user input, configuration values, or environment variables where boolean semantics are needed.
 */
@UtilityClass
public class BooleanParser {

    /**
     * Converts the given {@link String} to a primitive {@code boolean}.
     * <p>
     * If the input is {@code null} or cannot be parsed, this method returns {@code false}.
     *
     * @param value the string value to convert, may be {@code null}
     * @return the boolean value parsed from the string, or {@code false} if the input is {@code null}
     *         or cannot be successfully parsed
     * @throws IllegalArgumentException if the underlying parsing logic encounters an unexpected issue
     */
    public static boolean toBoolean(@Nullable String value) {
        return SafeRun.convert(value, Boolean::parseBoolean).orElse(false);
    }

    /**
     * Converts the provided {@link String} value to a primitive {@code boolean},
     * or returns the specified fallback value if conversion fails or the input is {@code null}.
     * <p>
     * This method attempts to parse the input string into a boolean value. If the
     * input is {@code null} or cannot be successfully parsed, the given fallback
     * value is returned instead.
     *
     * @param value the string value to be converted, may be {@code null}
     * @param fallback the value to return if the input is {@code null} or cannot
     *                 be parsed into a boolean
     * @return the boolean value parsed from the string, or the fallback value if
     *         the input is {@code null} or invalid
     * @throws NullPointerException if the fallback value is {@code null} and the fallback
     *                              logic requires it during execution
     */
    public static boolean toBoolean(@Nullable String value, boolean fallback) {
        return SafeRun.convert(value, Boolean::parseBoolean).orElse(fallback);
    }

    /**
     * Converts the given {@link String} value to a primitive {@code boolean},
     * or returns the result of the provided {@link Supplier} fallback if parsing fails or the input is {@code null}.
     * <p>
     * This method attempts to parse the input string into a boolean value. If the
     * input is {@code null} or cannot be successfully parsed, the fallback supplier
     * is executed to determine the value to return instead.
     *
     * @param value the string value to be converted, may be {@code null}
     * @param fallback a {@link Supplier} that provides a fallback boolean value
     *                 if the input string is {@code null} or invalid
     * @return the boolean value parsed from the string, or the result of the fallback supplier
     *         if the input is {@code null} or invalid
     * @throws NullPointerException if the provided {@link Supplier} fallback is {@code null}
     *                              and is invoked
     */
    public static boolean toBoolean(@Nullable String value, @NotNull Supplier<Boolean> fallback) {
        return SafeRun.convert(value, Boolean::parseBoolean).orElseGet(fallback);
    }

    /**
     * Converts the given {@link String} to an {@link Optional} containing a {@code Boolean}.
     * If the input is {@code null}, an empty {@link Optional} is returned.
     * If the input is a non-null {@link String} that can be successfully parsed
     * as a boolean, the {@link Optional} contains the parsed value.
     *
     * @param value the string value to be converted, may be {@code null}.
     * @return an {@link Optional} containing the parsed {@code Boolean} if the input
     * is non-null and valid, or an empty {@link Optional} if the input is {@code null}.
     * @throws IllegalArgumentException if the underlying parsing logic encounters
     * an unexpected issue.
     */
    @Contract("null -> !null")
    public static Optional<Boolean> toBooleanOptional(@Nullable String value) {
        return SafeRun.convert(value, Boolean::parseBoolean);
    }
}