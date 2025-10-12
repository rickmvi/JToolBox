package com.github.rickmvi.jtoolbox.console.util.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface InputReader {

    String readPrompt(String prompt);

    String readUntil(String prompt, Predicate<String> validator);

    byte readBytePrompt(String prompt);

    byte readByteUntil(String prompt, Predicate<Byte> predicate);

    short readShortPrompt(String prompt);

    short readShortUntil(String prompt, Predicate<Short> predicate);

    int readIntPrompt(String prompt);

    int readIntUntil(String prompt, IntPredicate predicate);

    long readLongPrompt(String prompt);

    long readLongUntil(String prompt, Predicate<Long> predicate);

    float readFloatPrompt(String prompt);

    float readFloatUntil(String prompt, Predicate<Float> predicate);

    double readDoublePrompt(String prompt);

    double readDoubleUntil(String prompt, Predicate<Double> predicate);

    boolean readBooleanPrompt(String prompt);

    boolean readBooleanUntil(String prompt, Predicate<Boolean> predicate);

    char readCharPrompt(String prompt);

    char readCharUntil(String prompt, Predicate<Character> predicate);

    BigDecimal readBigDecimalPrompt(String prompt);

    BigDecimal readBigDecimalUntil(String prompt, Predicate<BigDecimal> predicate);

    BigInteger readBigIntegerPrompt(String prompt);

    BigInteger readBigIntegerUntil(String prompt, Predicate<BigInteger> predicate);

    <T extends Number> T readNumberPrompt(String prompt, Function<String, T> parser);

    <T extends Number> T readNumberUntil(String prompt, Function<String, T> parser, Predicate<T> validator);
}
