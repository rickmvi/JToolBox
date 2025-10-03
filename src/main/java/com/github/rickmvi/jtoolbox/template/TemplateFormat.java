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
package com.github.rickmvi.jtoolbox.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing a flexible and reusable template formatting mechanism.
 * <p>
 * This class allows processing a given string template by searching for patterns
 * specified via regular expressions and replacing each matched occurrence dynamically
 * using a resolver function.
 * <p>
 * The resolver function receives the regex {@link Matcher} and the occurrence index,
 * enabling custom logic for generating replacement strings based on match details
 * and position.
 * <p>
 * This approach offers a powerful and generic way to implement custom templating,
 * token replacement, or string interpolation behaviors.
 */
@lombok.experimental.UtilityClass
@SuppressWarnings("unused")
public class TemplateFormat {

    /**
     * Processes the provided template string by finding all matches of the given regex pattern
     * and replacing each match with the string returned by the resolver function.
     * <p>
     * The resolver function is called for each match, with the matcher's state and
     * the zero-based index of the match occurrence. The returned replacement string
     * is safely escaped and substituted into the resulting string.
     *
     * @param template the input string template to format, not null
     * @param pattern the regular expression pattern to find tokens/placeholders in the template, not null
     * @param resolver a function that computes the replacement string for each match,
     *                 receiving the {@link Matcher} and the current match index
     * @return a new string with all matches replaced by the resolver's results
     */
    @Contract("_,_, _ -> new")
    public static @NotNull String format(@NotNull String template,
                                  @NotNull Pattern pattern,
                                  BiFunction<Matcher, Integer, String> resolver) {
        Matcher matcher = pattern.matcher(template);
        StringBuilder result = new StringBuilder();
        int index = 0;

        while (matcher.find()) {
            String replacement = resolver.apply(matcher, index++);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
