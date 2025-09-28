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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static com.github.rickmvi.jtoolbox.utils.Primitives.isNegative;
import static com.github.rickmvi.jtoolbox.collections.array.Array.length;
import static com.github.rickmvi.jtoolbox.utils.Primitives.isGreaterThan;

@lombok.experimental.UtilityClass
public final class StringFormatter {

    private final Pattern GENERIC_PATTERN = Pattern.compile(Constants.GENERIC);

    private static final Map<String, Object> TOKENS = Map.of(
            "%n", System.lineSeparator(),
            "%r", "\r",
            "%t", "\t",
            "%sp", " "
    );

    /**
     * Formats the given template by sequentially replacing each generic placeholder "{}"
     * with the string representation of the provided arguments in order.
     * <p>
     * Each placeholder is replaced once, in left-to-right order, matching the argument index.
     *
     * @param templateString the string template containing "{}" placeholders, not null
     * @param args     the arguments to replace placeholders, not null
     * @return the formatted string with placeholders replaced by argument values
     */
    public static @NotNull String format(@NotNull String templateString, @NotNull Object... args) {
        templateString = Mapping.getReplacement(templateString, TOKENS);
        templateString = replaceIndexTokens(templateString, args);
        for (Object arg : args) {
            templateString = GENERIC_PATTERN
                    .matcher(templateString)
                    .replaceFirst(
                            Matcher.quoteReplacement(Stringifier.valueOf(arg)));
        }

        return templateString;
    }

    private static @NotNull String replaceIndexTokens(@NotNull String template, Object @NotNull ... args) {
        Pattern tokenIndex = Pattern.compile(Constants.PLACEHOLDERS_REGEX);
        Matcher matcher = tokenIndex.matcher(template);
        StringBuffer buffer = new StringBuffer();

        While.runTrue(matcher::find, () -> {
            String token = matcher.group(1);

            int index = -1;
            index = getIndex(index, matcher);

            if (isNegative(index) || isGreaterThan(index, length(args))) {
                matcher.appendReplacement(buffer, "");
                return;
            }

            Object value = args[index];
            String replacement = returnToken(token, value);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        });

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @ApiStatus.Internal
    private static int getIndex(int index, @NotNull Matcher matcher) {
        try {
            index = NumberParser.toInt(matcher.group(2));
        } catch (NumberFormatException e) {
            Logger.error("Invalid index format in placeholder '{}'", e, matcher.group(2));
        }
        return index;
    }

    @ApiStatus.Internal
    private static @NotNull String returnToken(String token, Object value) {
        return Mapping.returning(token,
                Map.of(
                        "dc", () -> NumberFormat.DECIMAL_COMMA.format(value),
                        "dp", () -> NumberFormat.DECIMAL_POINT.format(value),
                        "i",  () -> NumberFormat.INTEGER.format(value),
                        "p",  () -> NumberFormat.PERCENT.format(value),
                        "sc", () -> NumberFormat.SCIENTIFIC.format(value),
                        "S",  () -> String.valueOf(value).toUpperCase(),
                        "lc", () -> String.valueOf(value).toLowerCase()
                ), () -> "");
    }
}
