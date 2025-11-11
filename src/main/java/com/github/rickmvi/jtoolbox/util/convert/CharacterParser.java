package com.github.rickmvi.jtoolbox.console.util.convert;

import com.github.rickmvi.jtoolbox.util.SafeRun;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Supplier;

public class CharacterParser {

    public static char toChar(String character) {
        return SafeRun.convert(character, s -> s.charAt(0)).orElse('\0');
    }

    public static char toChar(String character, char fallback) {
        return SafeRun.convert(character, s -> s.charAt(0)).orElse(fallback);
    }

    public static char toChar(String character, Supplier<Character> fallback) {
        return SafeRun.convert(character, s -> s.charAt(0)).orElseGet(fallback);
    }

    @Contract("null -> !null")
    public static Optional<Character> toCharOptional(String character) {
        return SafeRun.convert(character, s -> s.charAt(0));
    }

}
