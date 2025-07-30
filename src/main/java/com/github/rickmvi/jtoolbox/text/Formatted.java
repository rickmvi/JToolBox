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

import com.github.rickmvi.jtoolbox.collections.MapUtils;
import com.github.rickmvi.jtoolbox.template.TemplateFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

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
    private final Pattern GENERIC_PATTERN = Pattern.compile("\\{\\}");
    private final Pattern TOKEN_PATTERN = Pattern.compile("%(s|i|l|#d|ed|dn|nu|lc|c|b)");

    private final Map<String, String> TOKENS = Map.of(
            "%n", System.lineSeparator(), // Line break, jump line "\n"
            "%t", "\t",                   // Horizontal tab "value:\t1" -> "value:  1"
            "%r", "\r",                   // Car return (back cursor) "Halo \r, Ich bin der Rick" -> ", Ich bin..."
            "b", " "                          // Blank "Ich%bkomma aus Braziliane" -> "Ich komma..."
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
    public @NotNull String replace(@NotNull String template, @NotNull Map<String, String> values) {
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
    public @NotNull String format(@NotNull String template, Object @NotNull ... args) {
        template = MapUtils.replace(template, TOKENS);
        for (Object arg : args) {
            template = GENERIC_PATTERN
                    .matcher(template)
                    .replaceFirst(Matcher.quoteReplacement(String.valueOf(arg))
                    );
        }
        return template;
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
    public @NotNull String formatTokens(@NotNull String template, Object... args) {
        return TemplateFormatter.format(template, TOKEN_PATTERN, (matcher, index) -> {
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
    public @NotNull String formatNamed(@NotNull String template,
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
    public @NotNull Formatter builder(@NotNull String template) {
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
}
