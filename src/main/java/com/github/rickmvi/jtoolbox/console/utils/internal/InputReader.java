package com.github.rickmvi.jtoolbox.console.utils.internal;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface InputReader {

    String readPrompt(String prompt);

    int readIntPrompt(String prompt);

    int readIntUntil(String prompt, IntPredicate predicate);

    String readUntil(String prompt, Predicate<String> validator);

    <T extends Number> T readNumberUntil(String prompt, Function<String, T> parser, Predicate<T> validator);

}
