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

import com.github.rickmvi.jtoolbox.console.utils.convert.Stringifier;
import com.github.rickmvi.jtoolbox.template.TemplateFormatter;
import com.github.rickmvi.jtoolbox.collections.map.MapUtils;
import com.github.rickmvi.jtoolbox.control.ActionMapper;
import com.github.rickmvi.jtoolbox.debug.SLogger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;

import static com.github.rickmvi.jtoolbox.control.Repeater.whileTrue;
import static com.github.rickmvi.jtoolbox.collections.array.Array.length;
import static com.github.rickmvi.jtoolbox.utils.primitives.Primitives.integers;

/**
 * Utility class for advanced string formatting and templating.
 * <p>
 * Provides multiple formatting strategies including:
 * <ul>
 *   <li>Simple placeholder replacement using named keys (e.g., {{key}})</li>
 *   <li>Positional formatting with generic placeholders {}</li>
 *   <li>Token-based formatting with special codes (e.g., %s, %i, %l, %#d)</li>
 *   <li>Named formatting with optional strict checking for missing values</li>
 *   <li>Fluent builder pattern for assembling formatted strings</li>
 * </ul>
 * <p>
 * This class leverages regular expressions and a flexible template resolver
 * to perform customizable replacements, supporting various formatting needs
 * such as localization, dynamic message construction, and templated output.
 */
@lombok.experimental.UtilityClass
public final class Formatted {

    private final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");
    private final Pattern GENERIC_PATTERN = Pattern.compile("\\{}");

    private static final Pattern PRINTF_PATTERN =
            Pattern.compile("%([-#+ 0,(<]*)?(\\d*)?(\\.\\d+)?([a-zA-Z])");

    private static final Map<String, Object> PRINTF_TOKENS = Map.of(
            "%n", System.lineSeparator(),
            "%t", "\t",
            "%sp", " "
    );

    /**
     * Replaces all occurrences of placeholders in the form {{key}} within the given template
     * with the corresponding values from the provided map.
     * <p>
     * If a placeholder key is not found in the map, it remains unchanged.
     *
     * @param template the string template containing placeholders, not null
     * @param values   the map of placeholder keys to replacement strings, not null
     * @return the resulting string after replacement
     */
    public static @NotNull String replace(@NotNull String template, @NotNull Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            template = template.replace(placeholder, entry.getValue());
        }
        return template;
    }

    /**
     * Formats the given template by sequentially replacing each generic placeholder "{}"
     * with the string representation of the provided arguments in order.
     * <p>
     * Each placeholder is replaced once, in left-to-right order, matching the argument index.
     *
     * @param template the string template containing "{}" placeholders, not null
     * @param args     the arguments to replace placeholders, not null
     * @return the formatted string with placeholders replaced by argument values
     */
    public static @NotNull String format(@NotNull String template, Object @NotNull ... args) {
        return replaceAllTokens(template, args);
    }

    @Deprecated
    public static @NotNull String replaceAllTokens(@NotNull String template, Object @NotNull ... args) {
        template = MapUtils.getReplacement(template, PRINTF_TOKENS);
        template = replaceIndexedFormatTokens(template, args);

        for (Object arg : args) {
            template = GENERIC_PATTERN
                    .matcher(template)
                    .replaceFirst(Matcher.quoteReplacement(Stringifier.valueOf(arg)));
        }

        return printfTokens(template, args);
    }

    /**
     * Formats the template by replacing token placeholders matching special codes
     * like %s, %i, %l, %#d, etc., with formatted string representations of the
     * corresponding arguments.
     * <p>
     * Tokens are replaced in sequence with arguments based on their order.
     *
     * @param template the string template containing token placeholders, not null
     * @param args     the arguments corresponding to tokens, not null
     * @return a new string with all tokens replaced by formatted argument values
     */
    @Contract("_,_ -> new")
    public static @NotNull String formatTokens(@NotNull String template, Object... args) {
        return TemplateFormatter.format(template, FormatToken.FORMAT_TOKEN_PATTERN, (matcher, index) -> {
            if (index >= args.length) return "";
            String tokenCode = matcher.group(1);
            FormatToken token = FormatToken.fromCode(tokenCode);
            return token != null ? token.format(args[index]) : "";
        });
    }

    /**
     * Formats the template by replacing named placeholders ({{key}}) with corresponding
     * values from the provided map.
     * <p>
     * If a placeholder is missing in the map and {@code failIfMissing} is true,
     * this method throws an {@link IllegalArgumentException}.
     * Otherwise, missing placeholders are replaced with an empty string.
     *
     * @param template      the string template containing named placeholders, not null
     * @param values        the map of placeholder keys to replacement values, not null
     * @param failIfMissing whether to throw an exception if a placeholder key is missing
     * @return the formatted string with placeholders replaced by values
     * @throws IllegalArgumentException if a key is missing and {@code failIfMissing} is true
     */
    @Contract("_,_, _ -> new")
    public static @NotNull String formatNamed(@NotNull String template,
                                       @NotNull Map<String, ?> values,
                                       boolean failIfMissing) {
        return TemplateFormatter.format(template, PLACEHOLDER_PATTERN, (matcher, index) -> {
            String key = matcher.group(1).trim();
            Object value = values.get(key);
            if (value == null) {
                if (failIfMissing) {
                    throw new IllegalArgumentException("Missing value for placeholder '" + key + "'");
                }
                return "";
            }
            return String.valueOf(value);
        });
    }

    /**
     * Creates a new {@link Formatter} instance to build formatted strings
     * with named placeholders using a fluent API.
     *
     * @param template the template string containing named placeholders, not null
     * @return a new {@link Formatter} instance
     */
    @Contract("_ -> new")
    public static @NotNull Formatter builder(@NotNull String template) {
        return new Formatter(template);
    }

    /**
     * Fluent builder class to assemble formatted strings with named placeholders.
     * <p>
     * Allows chaining of multiple key-value pairs to be applied on the template,
     * and optionally enforce strict missing key checks.
     */
    public static final class Formatter {
        private final String template;
        private final Map<String, Object> values = new HashMap<>();
        private boolean failIfMissing = false;

        private Formatter(String template) {
            this.template = template;
        }

        /**
         * Adds or updates a placeholder key with a corresponding value to be replaced
         * in the template.
         *
         * @param key   the placeholder key, not null
         * @param value the value to replace the placeholder with
         * @return this formatter instance for chaining
         */
        @Contract("_,_-> this")
        public Formatter set(@NotNull String key, Object value) {
            values.put(key, value);
            return this;
        }

        /**
         * Adds all key-value pairs from the provided map to the formatter's values.
         *
         * @param map a map containing placeholder keys and their replacement values, not null
         * @return this formatter instance for chaining
         */
        @Contract("_-> this")
        public Formatter setAll(@NotNull Map<String, ?> map) {
            values.putAll(map);
            return this;
        }

        /**
         * Enables strict mode where the formatter throws an exception if
         * any placeholder in the template does not have a corresponding value set.
         *
         * @return this formatter instance for chaining
         */
        @Contract(value = "-> this", mutates = "this")
        public Formatter failIfMissing() {
            this.failIfMissing = true;
            return this;
        }

        /**
         * Builds the formatted string by applying all set key-value pairs to the template.
         * <p>
         * If strict mode is enabled, throws an exception if placeholders are missing values.
         *
         * @return the fully formatted string
         * @throws IllegalArgumentException if a placeholder value is missing and strict mode is enabled
         */
        @Contract(pure = true)
        public @NotNull String build() {
            return formatNamed(template, values, failIfMissing);
        }
    }

    /**
     * Replaces tokens in the given template string with indexed values from the provided arguments.
     * <p>
     * This method identifies placeholders in the template with a specific format `%{type}{index}`
     * and replaces them based on the type and index using the respective formatting logic.
     * Supported types include:
     * <ul>
     * - dc: Decimal with comma separator
     * - dp: Decimal with point separator
     * - in: Integer
     * - p: Percentage
     * - sc: Scientific notation
     * - S: String in uppercase
     * </ul>
     * If the index in the placeholder refers to an element that does not exist
     * in the argument array, it uses {@code null}.
     *
     * @param template the string template containing indexed format tokens, not null
     * @param args     the argument array used to replace the tokens, not null
     * @return a new string with all indexed format tokens replaced by the formatted argument values
     * @throws NullPointerException if the {@code template} or {@code args} parameter is null
     * @throws NumberFormatException if an invalid index is provided in the token
     */
    @ApiStatus.Internal
    private static @NotNull String replaceIndexedFormatTokens(
            @NotNull String template,
            Object @NotNull [] args) {
        Pattern tokenWithIndex = Pattern.compile("%(dc|dp|in|p|sc|S)\\{(\\d+)}");
        Matcher matcher = tokenWithIndex.matcher(template);
        StringBuffer buffer = new StringBuffer();

        whileTrue(matcher::find, () -> {
            String token = matcher.group(1);

            int index = -1;
            try {
                index = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                SLogger.error("Invalid index format in placeholder '{}'", e, matcher.group(2));
            }

            if (integers.isNegative(index) || integers.isGreaterOrEqual(index, length(args))) {
                matcher.appendReplacement(buffer, "");
                return;
            }

            Object value = args[index];
            String replacement = ActionMapper.returning(token,
                    Map.of(
                    "dc", () -> NumberFormat.DECIMAL_COMMA.format(value),
                    "dp", () -> NumberFormat.DECIMAL_POINT.format(value),
                    "in", () -> NumberFormat.INTEGER.format(value),
                    "p",  () -> NumberFormat.PERCENT.format(value),
                    "sc", () -> NumberFormat.SCIENTIFIC.format(value),
                    "S",  () -> String.valueOf(value).toUpperCase(),
                    "lc", () -> String.valueOf(value).toLowerCase()
            ), () -> "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private static @NotNull String printfTokens(@NotNull String template, Object @NotNull ... args) {
        Matcher matcher =     PRINTF_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        int[] argIndex = {0};

        whileTrue(() -> matcher.find() && argIndex[0] < args.length, () -> {
            String flags =      matcher.group(1) != null ? matcher.group(1) : "";
            String width =      matcher.group(2) != null ? matcher.group(2) : "";
            String precision =  matcher.group(3) != null ? matcher.group(3) : "";
            String conversion = matcher.group(4);

            String formatSpecifier = "%" + flags + width + precision + conversion;
            String replacement;
            try {
                replacement = String.format(formatSpecifier, args[argIndex[0]++]);
            } catch (Exception e) {
                replacement = Stringifier.valueOf(args[argIndex[0] - 1]);
            }

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
