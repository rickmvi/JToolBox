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
import com.github.rickmvi.jtoolbox.collections.array.Array;
import com.github.rickmvi.jtoolbox.collections.map.Mapping;
import com.github.rickmvi.jtoolbox.control.While;
import com.github.rickmvi.jtoolbox.debug.Logger;

import com.github.rickmvi.jtoolbox.utils.constants.Constants;
import com.github.rickmvi.jtoolbox.utils.Primitives;
import com.github.rickmvi.jtoolbox.utils.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;

import static java.util.Map.entry;
import static com.github.rickmvi.jtoolbox.utils.Primitives.isNegative;
import static com.github.rickmvi.jtoolbox.utils.Primitives.isGreaterThan;

/**
 * Utility class for formatting strings using placeholders and custom tokens.
 * <p>
 * This class provides flexible formatting options for console applications
 * and other scenarios where dynamic string replacement is required.
 * It supports:
 * <ul>
 *     <li>Generic placeholders <code>{}</code> that are replaced sequentially with provided arguments.</li>
 *     <li>Indexed placeholders like <code>{0}</code>, <code>{1}</code> referring to specific arguments.</li>
 *     <li>Original formatting tokens:
 *         <ul>
 *             <li><code>dc</code> - decimal with comma (e.g., 1.234,56)</li>
 *             <li><code>dp</code> - decimal with point (e.g., 1234.56)</li>
 *             <li><code>i</code>  - integer format</li>
 *             <li><code>p</code>  - percent format (e.g., 75%)</li>
 *             <li><code>sc</code> - scientific notation</li>
 *             <li><code>e</code>  - exponentiation format</li>
 *             <li><code>U</code>  - uppercase</li>
 *             <li><code>lc</code> - lowercase</li>
 *         </ul>
 *     </li>
 *     <li>Advanced formatting tokens:
 *         <ul>
 *             <li><code>pad</code> - pad string to fixed length (e.g., <code>{0:pad}</code>)</li>
 *             <li><code>len</code> - length of the value</li>
 *             <li><code>sub</code> - substring with start/end indexes (e.g., <code>{0:sub}</code>)</li>
 *             <li><code>rev</code> - reverse string</li>
 *             <li><code>trim</code> - trim whitespace</li>
 *             <li><code>cap</code> - capitalize first letter, lowercase rest</li>
 *         </ul>
 *     </li>
 *     <li>Custom tokens (e.g., <code>$n</code> for newline, <code>$t</code> for tab, <code>$tmp</code> for current date-time, <code>$rand</code> for random number).</li>
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
 * // Original formatting tokens
 * s = StringFormat.format("Decimal: {0:dc}, Percent: {1:p}", 1234.56, 0.75); // "Decimal: 1.234,56, Percent: 75%"
 * s = StringFormat.format("Upper: {0:U}, Lower: {1:lc}", "hello", "WORLD"); // "Upper: HELLO, Lower: world"
 *
 * // Advanced formatting tokens
 * s = StringFormat.format("Padded: {0:pad:5}", "text"); // "Padded: text      "
 * s = StringFormat.format("Length: {0:len}", "Hello"); // "Length: 5"
 * s = StringFormat.format("Reversed: {0:rev}", "abc"); // "Reversed: cba"
 * s = StringFormat.format("Substring: {0:sub:1,4}", "Hello"); // "Substring: ell"
 * s = StringFormat.format("Capitalized: {0:cap}", "hello WORLD"); // "Capitalized: Hello world"
 * s = StringFormat.format("Trimmed: {0:trim}", "  text  "); // "Trimmed: text"
 * }</pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 *     <li>All string and argument parameters must not be {@code null} unless explicitly allowed.</li>
 *     <li>Indexed placeholders must contain valid integer indices within the bounds of the provided arguments.</li>
 *     <li>Invalid numeric indices or formats are logged via {@link Logger} and replaced with an empty string.</li>
 *     <li>Supports standard Java numeric formatting via {@link NumberFormat} and string transformations via {@link Stringifier}.</li>
 *     <li>Custom tokens are defined in the {@link #TOKENS} map and include common escape sequences, the current timestamp, and random values.</li>
 *     <li>Advanced tokens allow for inline string transformations, numeric manipulations, and conditional formatting.</li>
 * </ul>
 *
 * @apiNote This class is intended as a more flexible alternative to {@link String#format(String, Object...)}
 *          and can handle mixed placeholder types, original and advanced tokens, and custom formatting.
 * @see Mapping
 * @see Stringifier
 * @see NumberParser
 * @see Logger
 * @since 1.2
 */
@UtilityClass
public final class StringFormat {

    private static final Pattern GENERIC_PATTERN     = Pattern.compile(Constants.GENERIC);
    private static final Pattern TOKEN_PATTERN       = Pattern.compile(Constants.TOKENS_COMMUM);
    private static final Pattern ADVANCED_TOKENS     = Pattern.compile(Constants.ADVANCED_TOKEN);
    private static final Pattern NEW_LINE_PATTERN    = Pattern.compile(Constants.NEW_LINE);
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
        templateString = applyTemplateFormatting(templateString, args);

        for (Object arg : args) {
            templateString = GENERIC_PATTERN
                    .matcher(templateString)
                    .replaceFirst(Matcher.quoteReplacement(Stringifier.valueOf(arg)));
        }

        return templateString;
    }

    private static @NotNull String applyTemplateFormatting(@NotNull String templateString, Object @NotNull ... args) {
        templateString = newLine(templateString);
        templateString = Mapping.applyReplacements(templateString, TOKENS);
        templateString = replacePlaceholders(templateString, args, TOKEN_PATTERN, StringFormat::formatOriginalToken);
        templateString = replacePlaceholders(templateString, args, ADVANCED_TOKENS, StringFormat::formatAdvancedToken);
        return templateString;
    }

    private static @NotNull String newLine(String template) {
        Matcher matcher = NEW_LINE_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        While.runTrue(matcher::find, () -> {
            int count = Try.of(() -> NumberParser.toInt(matcher.group(1)))
                    .onFailure(e -> Logger.error("Invalid new line count '{}'", e, matcher.group(1)))
                    .orElse(1);

            if (Primitives.isLessThan(count, 1)) count = 1;

            String replacement = System.lineSeparator().repeat(count);

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static @NotNull String replacePlaceholders(
            @NotNull String template,
            Object @NotNull [] args,
            @NotNull Pattern pattern,
            java.util.function.BiFunction<String,Object,String> formatter
    ) {
        Matcher matcher = pattern.matcher(template);
        StringBuffer buffer = new StringBuffer();

        While.runTrue(matcher::find, () -> {
            int index = getIndexOrElse(matcher);
            if (isPlaceholderIndexInvalid(args, index, matcher, buffer)) return;

            Object value = args[index];
            String token = matcher.group(2);
            String replacement = token == null ? Stringifier.valueOf(value) : formatter.apply(token, value);

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static boolean isPlaceholderIndexInvalid(
            Object @NotNull [] args,
            int index,
            Matcher matcher,
            StringBuffer buffer
    ) {
        if (isNegative(index) || isGreaterThan(index, Array.length(args))) {
            Logger.error(
                    "Invalid index '{}' for placeholder '{}' (args length: {}). Valid range is [0..{}].",
                    matcher.group(1),
                    matcher.group(2),
                    Array.length(args),
                    Array.lastIndex(args)
            );
            matcher.appendReplacement(buffer, "");
            return true;
        }
        return false;
    }

    @Contract("null, _ -> null; !null, null -> param1")
    public static String interpolate(String template, Object... objects) {
        if (template == null || objects == null) return template;

        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(template);
        StringBuilder sb = new StringBuilder();

        int index = 0;
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = matcher.group();

            if (index < objects.length) {
                Object obj = objects[index];

                if (obj != null) {
                    Class<?> clazz = obj.getClass();

                    if (!isPrimitiveLike(clazz)) {
                        String resolved = resolveField(obj, placeholder);

                        if (resolved.equals("{" + placeholder + "}")) {
                            index++;
                            continue;
                        }

                        replacement = resolved;
                    }

                    if (placeholder.equalsIgnoreCase(clazz.getSimpleName())) {
                        replacement = obj.toString();
                        index++;
                    }
                }
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    @Contract("null, _ -> !null")
    private static String resolveField(Object obj, String fieldName) {
        if (obj == null) return formatPlaceholder(fieldName);

        return Try.ofThrowing(() -> {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            Object value = Modifier.isStatic(field.getModifiers())
                    ? field.get(null)
                    : field.get(obj);
            return Stringifier.toString(value);
        }).recover(e -> formatPlaceholder(fieldName)).orElseGet(() -> formatPlaceholder(fieldName));

    }

    @Contract(pure = true)
    private static @NotNull String formatPlaceholder(String fieldName) {
        return "{" + fieldName + "}";
    }

    private static boolean isPrimitiveLike(@NotNull Class<?> clazz) {
        return clazz.isPrimitive() ||
                Number.class.isAssignableFrom(clazz) ||
                Boolean.class.isAssignableFrom(clazz) ||
                CharSequence.class.isAssignableFrom(clazz);
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

    private static @NotNull String formatOriginalToken(String token, Object value) {
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
                ),
                () -> ""
        );
    }

    private static @NotNull String formatAdvancedToken(@NotNull String token, Object value) {
        String[] split = token.split(":", 2);
        String command = split[0];
        String option  = split.length > 1 ? split[1] : "";

        return Mapping.returning(
                command,
                Map.ofEntries(
                        entry("pad",  () -> StringEnhancer.pad(value)),
                        entry("len",  () -> StringEnhancer.length(value)),
                        entry("sub",  () -> StringEnhancer.substring(value + (option.isEmpty() ? "" : ":" + option))),
                        entry("rev",  () -> StringEnhancer.reverse(value)),
                        entry("trim", () -> StringEnhancer.trim(value)),
                        entry("cap",  () -> StringEnhancer.capitalize(value))
                ),
                () -> ""
        );
    }

}
