package com.github.rickmvi.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@lombok.experimental.UtilityClass
@SuppressWarnings("unused")
public class TemplateFormatter {

    @Contract("_,_, _ -> new")
    public @NotNull String format(@NotNull String template,
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
