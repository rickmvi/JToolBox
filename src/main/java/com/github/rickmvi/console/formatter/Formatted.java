/*
 * Console API - Biblioteca utilitária para entrada, saída e formatação no console.
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
package com.github.rickmvi.formatter;

import com.github.rickmvi.console.convert.ObjectToNumber;
import com.github.rickmvi.template.TemplateFormatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@lombok.experimental.UtilityClass
public final class Formatted {

    private final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");
    private final Pattern GENERIC_PATTERN = Pattern.compile("\\{\\}");
    private final Pattern TOKEN_PATTERN = Pattern.compile("%(s|i|l|#d|ed|n|u|lc|c|b)");
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    public static String formatDecimal(Object o) {
        return DECIMAL_FORMAT.format(ObjectToNumber.toDouble(o));
    }

    public @NotNull String replace(@NotNull String template, @NotNull Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            template = template.replace(placeholder, entry.getValue());
        }
        return template;
    }

    public @NotNull String format(@NotNull String template, Object @NotNull ... args) {
        for (Object arg : args) {
            template = GENERIC_PATTERN
                    .matcher(template)
                    .replaceFirst(Matcher.quoteReplacement(String.valueOf(arg))
                    );
        }
        return template;
    }


    @Contract("_,_ -> new")
    public @NotNull String formatTokens(@NotNull String template, Object... args) {
        return TemplateFormatter.format(template, TOKEN_PATTERN, (matcher, index) -> {
            if (index >= args.length) return "";
            String tokenCode = matcher.group(1);
            FormatToken token = FormatToken.fromCode(tokenCode);
            return token != null ? token.format(args[index]) : "";
        });
    }

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

    @Contract("_ -> new")
    public @NotNull Formatter builder(@NotNull String template) {
        return new Formatter(template);
    }

    public static final class Formatter {
        private final String template;
        private final Map<String, Object> values = new HashMap<>();
        private boolean failIfMissing = false;

        private Formatter(String template) {
            this.template = template;
        }

        @Contract("_,_-> this")
        public Formatter set(@NotNull String key, Object value) {
            values.put(key, value);
            return this;
        }

        @Contract("_-> this")
        public Formatter setAll(@NotNull Map<String, ?> map) {
            values.putAll(map);
            return this;
        }

        @Contract(value = "-> this", mutates = "this")
        public Formatter failIfMissing() {
            this.failIfMissing = true;
            return this;
        }

        @Contract(pure = true)
        public @NotNull String build() {
            return formatNamed(template, values, failIfMissing);
        }
    }
}
