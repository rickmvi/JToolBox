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
package com.github.rickmvi.jtoolbox.text;

import com.github.rickmvi.jtoolbox.console.utils.convert.NumberParser;
import com.github.rickmvi.jtoolbox.console.utils.convert.Stringifier;
import com.github.rickmvi.jtoolbox.collections.map.Mapping;
import com.github.rickmvi.jtoolbox.control.While;
import com.github.rickmvi.jtoolbox.debug.Logger;

import com.github.rickmvi.jtoolbox.utils.constants.Constants;
import com.github.rickmvi.jtoolbox.utils.Try;
import org.jetbrains.annotations.ApiStatus;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.format.DateTimeFormatter;

import static com.github.rickmvi.jtoolbox.utils.Primitives.isNegative;
import static com.github.rickmvi.jtoolbox.utils.Primitives.isGreaterThan;
import static com.github.rickmvi.jtoolbox.collections.array.Array.length;

/**
 * Utility class for formatting strings using placeholders and custom tokens.
 * <p>
 * This class provides flexible formatting options for console applications
 * and other scenarios where dynamic string replacement is required.
 * It supports:
 * <ul>
 *     <li>Generic placeholders <code>{}</code> that are replaced sequentially with provided arguments</li>
 *     <li>Indexed placeholders like <code>{0}</code>, <code>{1}</code> referring to specific arguments</li>
 *     <li>Custom tokens (e.g., <code>$n</code> for newline, <code>$t</code> for tab, <code>$tmp</code> for current date-time)</li>
 *     <li>Special formatting tokens such as decimal, percent, scientific, uppercase, lowercase, etc.</li>
 * </ul>
 * </p>
 *
 * <h2>Examples:</h2>
 * <pre>{@code
 * // Generic placeholder
 * String s = StringFormat.format("Hello, {}!", "World"); // "Hello, World!"
 *
 * // Indexed placeholder
 * s = StringFormat.format("Values: {1}, {0}", "first", "second"); // "Values: second, first"
 *
 * // Token replacement
 * s = StringFormat.format("Line break here:$nNext line"); // includes system line separator
 *
 * // Numeric formatting
 * s = StringFormat.format("Decimal: {0:dc}, Percent: {1:p}", 1234.56, 0.75); // "Decimal: 1.234,56, Percent: 75%"
 *
 * // Uppercase/lowercase formatting
 * s = StringFormat.format("Upper: {0:u}, Lower: {1:lc}", "hello", "WORLD"); // "Upper: HELLO, Lower: world"
 * }</pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>All string and argument parameters must not be {@code null} unless explicitly allowed.</li>
 *     <li>Indexed placeholders must contain valid integer indices within the bounds of the provided arguments.</li>
 *     <li>Invalid numeric indices or formats are logged via {@link Logger} and replaced with an empty string.</li>
 *     <li>Supports standard Java numeric formatting via {@link NumberFormat} and string transformations via {@link Stringifier}.</li>
 *     <li>Custom tokens are defined in the {@link #TOKENS} map and include common escape sequences and the current timestamp.</li>
 * </ul>
 *
 * @apiNote This class is intended as a more flexible alternative to {@link String#format(String, Object...)}
 *          and can handle mixed placeholder types, tokens, and custom formatting.
 * @see Mapping
 * @see Stringifier
 * @see NumberParser
 * @see Logger
 * @since 1.1
 */
@UtilityClass
public final class StringFormat {

    private static final Pattern GENERIC_PATTERN     = Pattern.compile(Constants.GENERIC);
    private static final Pattern INDEXED_PATTERN     = Pattern.compile(Constants.GENERIC_OPTIONAL);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
    private static final Map<String, Object> TOKENS  = Map.of(
            "$n", System.lineSeparator(),
            "$r", "\r",
            "$t", "\t",
            "$sp", " ",
            "$tmp", DATE_TIME.format(LocalDateTime.now()),
            "$rand", Math.random() * 1000000000000000000L
    );

    /**
     * Formats a given template string by replacing placeholders with provided arguments.
     * The method processes and replaces token patterns within the `templateString`
     * to generate a formatted output string.
     *
     * @param templateString the template string containing placeholders to be replaced.
     *                       Must not be null.
     * @param args an array of arguments to replace placeholders in the template string.
     *             Must not be null.
     * @return the formatted string with placeholders replaced by the respective arguments.
     * @throws IllegalArgumentException if the input template string or arguments array is invalid.
     * @throws NullPointerException if the `templateString` or `args` is null.
     */
    public static @NotNull String format(@NotNull String templateString, Object @NotNull ... args) {
        templateString = Mapping.applyReplacements(templateString, TOKENS);
        templateString = replacePlaceholders(templateString, args);

        for (Object arg : args) {
            templateString = GENERIC_PATTERN
                    .matcher(templateString)
                    .replaceFirst(Matcher.quoteReplacement(Stringifier.valueOf(arg)));
        }

        return templateString;
    }

    private static @NotNull String replacePlaceholders(@NotNull String template, Object @NotNull ... args) {
        Matcher matcher = INDEXED_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        While.runTrue(matcher::find, () -> {
            int index = getIndexOrElse(matcher);

            if (isNegative(index) || isGreaterThan(index, length(args))) {
                Logger.error(
                        "Invalid index '{}' for placeholder '{}' (args length: {}). Valid range is [0..{}].",
                        matcher.group(1),
                        matcher.group(2),
                        length(args),
                        length(args) - 1
                );

                matcher.appendReplacement(buffer, "");
                return;
            }

            Object value = args[index];
            String token = matcher.group(2);
            String replacement = token == null ? Stringifier.valueOf(value) : formatTokenValue(token, value);

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Parses and retrieves the integer index from the provided {@link Matcher}.
     * If the index cannot be parsed (due to invalid format or other errors),
     * a default value of -1 is returned.
     *
     * @param matcher the {@code Matcher} containing the group from which the index will be extracted.
     *                Must not be {@code null}.
     * @return the parsed integer index if successful, or -1 as a default value if parsing fails.
     * @throws NullPointerException if the {@code matcher} parameter is {@code null}.
     */
    private static Integer getIndexOrElse(Matcher matcher) {
        return Try.of(() -> NumberParser.toInt(matcher.group(1)))
                .onFailure(e -> Logger.error("Invalid index format '{}'", e, matcher.group(1)))
                .orElse(-1);
    }

    @ApiStatus.Internal
    private static @NotNull String formatTokenValue(String token, Object value) {
        return Mapping.returning(token,
                Map.of(
                        "dc", () -> NumberFormat.DECIMAL_COMMA.format(value),
                        "dp", () -> NumberFormat.DECIMAL_POINT.format(value),
                        "i",  () -> NumberFormat.INTEGER.format(value),
                        "p",  () -> NumberFormat.PERCENT.format(value),
                        "sc", () -> NumberFormat.SCIENTIFIC.format(value),
                        "e",  () -> NumberFormat.EXPONENTIATION.format(value),
                        "U",  () -> Stringifier.valueOf(value).toUpperCase(),
                        "lc", () -> Stringifier.valueOf(value).toLowerCase()
                ), () -> "");
    }
}
