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

import com.github.rickmvi.jtoolbox.util.convert.NumberParser;
import com.github.rickmvi.jtoolbox.collections.map.Mapping;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.control.Condition;

import com.github.rickmvi.jtoolbox.datetime.DateTime;
import com.github.rickmvi.jtoolbox.util.Numbers;
import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import static java.util.Map.entry;
import static com.github.rickmvi.jtoolbox.util.Numbers.isNegative;
import static com.github.rickmvi.jtoolbox.util.Numbers.isGreaterThan;
import static com.github.rickmvi.jtoolbox.collections.array.Array.length;
import static com.github.rickmvi.jtoolbox.collections.array.Array.lastIndex;

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
 *             <li><code>cap</code> - capitalize the first letter, lowercase rest</li>
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
 * @author Rick M. Viana
 * @version 1.5
 * @since 2025
 */
@UtilityClass
public final class StringFormatter {

    private static final Pattern     GENERIC_PATTERN;
    private static final Pattern       TOKEN_PATTERN;
    private static final Pattern     ADVANCED_TOKENS;
    private static final Pattern    NEW_LINE_PATTERN;
    private static final Map<String, Object>  TOKENS;

    static {
        GENERIC_PATTERN  = Pattern.compile("\\{}");
        TOKEN_PATTERN    = Pattern.compile("\\{(\\d+)(?::(dc|dp|i|p|sc|e|U|lc))?}");
        ADVANCED_TOKENS  = Pattern.compile("\\{(\\d+):([^}]+)}");
        NEW_LINE_PATTERN = Pattern.compile("\\$N:(\\d+)");
        TOKENS           = Map.of(
                "$n", System.lineSeparator(),
                "$r", "\r",
                "$t", "\t",
                "$sp", " ",
                "$tmp", DateTime.now().format(DateTime.Format.DASH_DATETIME),
                "$rand", Math.random() * 1024L
        );
    }

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
        templateString = Mapping.applyReplacements(templateString, TOKENS);
        templateString = replacePlaceholders(templateString, args, TOKEN_PATTERN, StringFormatter::formatToken);
        templateString = replacePlaceholders(templateString, args, ADVANCED_TOKENS, StringFormatter::formatToken);
        templateString = newLine(templateString);
        return templateString;
    }

    private static @NotNull String newLine(String template) {
        Matcher matcher = NEW_LINE_PATTERN.matcher(template);
        StringBuffer buffer = getBuffer().get();

        while (matcher.find()) {
            int count = Try.of(() -> NumberParser.toInt(matcher.group(1)))
                    .onFailure(e -> Logger.error("Invalid new line count '{}'", e, matcher.group(1)))
                    .orElse(1);

            if (Numbers.isLessThan(count, 1)) count = 1;

            String replacement = System.lineSeparator().repeat(count);

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

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
        StringBuffer buffer = getBuffer().get();

        while (matcher.find()) {
            int index = getIndexOrElse(matcher);
            handleInvalidIndex(args, index, matcher, buffer);

            Object value = args[index];
            String token = matcher.group(2);
            String replacement = token == null ? Stringifier.valueOf(value) : formatter.apply(token, value);

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static void handleInvalidIndex(Object @NotNull [] args, int index, Matcher matcher, StringBuffer buffer) {
        if (isNegative(index) || isGreaterThan(index, length(args))) {
            Logger.error(
                    "Invalid index '{}' for placeholder '{}' (args length: {}). Valid range is [0..{}].",
                    matcher.group(1),
                    matcher.group(2),
                    length(args),
                    lastIndex(args)
            );
            matcher.appendReplacement(buffer, "");
        }
    }

    @Contract(pure = true)
    private static @NotNull Supplier<StringBuffer> getBuffer() {
        return StringBuffer::new;
    }

    @Contract("null, _ -> null; !null, null -> param1")
    public static String interpolate(String template, Object... objects) {
        if (template == null || objects == null) return template;

        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(template);
        StringBuilder sb = new StringBuilder();

        int index = 0;
        applyFormatting(objects, matcher, index, sb);

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static void applyFormatting(
            Object[] objects,
            @NotNull Matcher matcher,
            int index,
            StringBuilder sb
    ) {
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = matcher.group();

            int finalIndex = index;
            Condition.ThrowWhen(index >= length(objects),
                    () -> new ArrayIndexOutOfBoundsException(format(
                            "The index '{}' overlapping the maximum index '{}'",
                            finalIndex,
                            lastIndex(objects)
                    )));

            Object obj = objects[index];

            Condition.ThrowWhen(obj == null, () -> new IllegalArgumentException(format(
                    "Invalid null value for placeholder '{}'", placeholder)
            ));

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
                replacement = Stringifier.toString(obj);
                index++;
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
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

    private static @NotNull String formatPlaceholder(String fieldName) {
        return "{" + fieldName + "}";
    }

    private static boolean isPrimitiveLike(@NotNull Class<?> clazz) {
        return clazz.isPrimitive()
                || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz);
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

    private static @NotNull String formatToken(@NotNull String token, Object value) {
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
                        entry("cap",  () -> StringEnhancer.capitalize(value)),
                        entry("dc",   () -> NumberFormat.DECIMAL_COMMA.format(value)),
                        entry("dp",   () -> NumberFormat.DECIMAL_POINT.format(value)),
                        entry("i",    () -> NumberFormat.INTEGER.format(value)),
                        entry("p",    () -> NumberFormat.PERCENT.format(value)),
                        entry("sc",   () -> NumberFormat.SCIENTIFIC.format(value)),
                        entry("e",    () -> NumberFormat.EXPONENTIATION.format(value)),
                        entry("U",    () -> Stringifier.valueOf(value).toUpperCase()),
                        entry("lc",   () -> Stringifier.valueOf(value).toLowerCase())
                ),
                () -> ""
        );
    }

}
