package com.github.rickmvi.jtoolbox.util.convert;

import com.github.rickmvi.jtoolbox.util.TypeConverter;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A utility class for parsing strings into {@code char} values.
 * The class provides several methods to convert a given {@code String} input
 * into a {@code char}, with optional fallback mechanisms.
 */
public class CharacterParser {

    public static char toChar(String character) {
        return TypeConverter.convert(character, s -> s.charAt(0)).orElse('\0');
    }

    public static char toChar(String character, char fallback) {
        return TypeConverter.convert(character, s -> s.charAt(0)).orElse(fallback);
    }

    public static char toChar(String character, Supplier<Character> fallback) {
        return TypeConverter.convert(character, s -> s.charAt(0)).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Character> toCharOptional(String character) {
        return TypeConverter.convert(character, s -> s.charAt(0));
    }

}
