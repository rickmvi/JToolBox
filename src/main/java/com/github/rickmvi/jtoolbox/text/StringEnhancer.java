package com.github.rickmvi.jtoolbox.text;

import com.github.rickmvi.jtoolbox.collections.array.Array;
import com.github.rickmvi.jtoolbox.console.utils.convert.NumberParser;
import com.github.rickmvi.jtoolbox.console.utils.convert.Stringifier;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@UtilityClass
public class StringEnhancer {
    
    public static @NotNull String pad(@NotNull Object value) {
        String[] parts = Stringifier.valueOf(value).split(":", 2);
        String text = parts[0];

        int size = Array.length(parts) > 1 ? NumberParser.toInt(parts[1]) : 10;
        return String.format("%-" + size + "s", text);
    }

    public static @NotNull String length(@NotNull Object value) {
        return Stringifier.valueOf(Stringifier.valueOf(value).length());
    }

    public static @NotNull String substring(@NotNull Object value) {
        String[] parts = Stringifier.valueOf(value).split(":");
        String text = parts[0];

        int start = 0;
        int end = text.length();

        if (parts.length > 1) {
            String[] range = parts[1].split(",");
            start = NumberParser.toInt(range[0]);
            end = range.length > 1 ? NumberParser.toInt(range[1]) : text.length();
        }

        if (start < 0) start = 0;
        if (end > text.length()) end = text.length();

        return text.substring(start, end);
    }


    public static @NotNull String reverse(@NotNull Object value) {
        return new StringBuilder(Stringifier.valueOf(value)).reverse().toString();
    }

    public static @NotNull String trim(@NotNull Object value) {
        return Stringifier.valueOf(value).trim();
    }

    public static @NotNull String capitalize(@NotNull Object value) {
        String s = Stringifier.valueOf(value).trim();
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
