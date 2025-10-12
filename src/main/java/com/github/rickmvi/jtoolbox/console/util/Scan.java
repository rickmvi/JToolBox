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
package com.github.rickmvi.jtoolbox.console.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Scanner;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.IntPredicate;

/**
 * Public-facing utility interface for enhanced and safe input operations via {@link Scanner}.
 * <p>
 * Unlike a plain {@link Scanner}, this utility auto-initializes with {@code System.in}
 * on first usage. That means you can directly call {@link #readLine()} or {@link #readInt()}
 * without worrying about manual setup.
 * <p>
 * For advanced scenarios (e.g., testing or reading from files), you may override the input
 * source by calling {@link #init(Scanner)} with a custom scanner.
 * <p>
 * Example:
 * <pre>{@code
 * String name = Scan.readLine();
 * int age = Scan.readInt();
 *
 * // Using a custom source
 * Scan.init(new Scanner("42\n"));
 * int value = Scan.readInt(); // -> 42
 * }</pre>
 *
 * @author Rick M. Viana
 * @since 1.2
 */
@SuppressWarnings("unused")
public interface Scan {

    @Getter(value = AccessLevel.PRIVATE)
    Scanf scannerInstance = new Scanf();

    static void init(@NotNull Scanner scanner) {
        scannerInstance.init(scanner);
    }

    static void locale(@NotNull Location location) {
        scannerInstance.locale(location);
    }

    static boolean hasNext() {
        return scannerInstance.hasNext();
    }

    static boolean hasNextLine() {
        return scannerInstance.hasNextLine();
    }

    static void close() {
        scannerInstance.close();
    }

    @Contract(pure = true)
    static String read() {
        return scannerInstance.read();
    }

    @Contract(pure = true)
    static String read(@NotNull String pattern) {
        return scannerInstance.read(pattern);
    }

    @Contract(pure = true)
    static String readLine() {
        return scannerInstance.readLine();
    }

    static String readSafe() {
        return scannerInstance.readSafe();
    }

    static byte readByte() {
        return scannerInstance.readByte();
    }

    static byte readBytePrompt(String prompt) {
        return scannerInstance.readBytePrompt(prompt);
    }

    static byte readByteUntil(String prompt, Predicate<Byte> validator) {
        return scannerInstance.readByteUntil(prompt, validator);
    }

    static short readShort() {
        return scannerInstance.readShort();
    }

    static short readShortPrompt(String prompt) {
        return scannerInstance.readShortPrompt(prompt);
    }

    static short readShortUntil(String prompt, Predicate<Short> validator) {
        return scannerInstance.readShortUntil(prompt, validator);
    }

    @Contract(pure = true)
    static int readInt() {
        return scannerInstance.readInt();
    }

    static int readIntPrompt(String prompt) {
        return scannerInstance.readIntPrompt(prompt);
    }

    static int readIntUntil(String prompt, IntPredicate validator) {
        return scannerInstance.readIntUntil(prompt, validator);
    }

    @Contract(pure = true)
    static long readLong() {
        return scannerInstance.readLong();
    }

    static long readLongPrompt(String prompt) {
        return scannerInstance.readLongPrompt(prompt);
    }

    static long readLongUntil(String prompt, Predicate<Long> validator) {
        return scannerInstance.readLongUntil(prompt, validator);
    }

    @Contract(pure = true)
    static float readFloat() {
        return scannerInstance.readFloat();
    }

    static float readFloatPrompt(String prompt) {
        return scannerInstance.readFloatPrompt(prompt);
    }

    static float readFloatUntil(String prompt, Predicate<Float> validator) {
        return scannerInstance.readFloatUntil(prompt, validator);
    }

    @Contract(pure = true)
    static double readDouble() {
        return scannerInstance.readDouble();
    }

    static double readDoublePrompt(String prompt) {
        return scannerInstance.readDoublePrompt(prompt);
    }

    static double readDoubleUntil(String prompt, Predicate<Double> validator) {
        return scannerInstance.readDoubleUntil(prompt, validator);
    }

    @Contract(pure = true)
    static boolean readBoolean() {
        return scannerInstance.readBoolean();
    }

    static boolean readBooleanPrompt(String prompt) {
        return scannerInstance.readBooleanPrompt(prompt);
    }

    static boolean readBooleanUntil(String prompt, Predicate<Boolean> validator) {
        return scannerInstance.readBooleanUntil(prompt, validator);
    }

    static char readChar() {
        return scannerInstance.readChar();
    }

    static char readCharPrompt(String prompt) {
        return scannerInstance.readCharPrompt(prompt);
    }

    static char readCharUntil(String prompt, Predicate<Character> validator) {
        return scannerInstance.readCharUntil(prompt, validator);
    }

    static BigDecimal readBigDecimal() {
        return scannerInstance.readBigDecimal();
    }

    static BigDecimal readBigDecimalPrompt(String prompt) {
        return scannerInstance.readBigDecimalPrompt(prompt);
    }

    static BigDecimal readBigDecimalUntil(String prompt, Predicate<BigDecimal> validator) {
        return scannerInstance.readBigDecimalUntil(prompt, validator);
    }

    static BigInteger readBigInteger() {
        return scannerInstance.readBigInteger();
    }

    static BigInteger readBigIntegerPrompt(String prompt) {
        return scannerInstance.readBigIntegerPrompt(prompt);
    }

    static BigInteger readBigIntegerUntil(String prompt, Predicate<BigInteger> validator) {
        return scannerInstance.readBigIntegerUntil(prompt, validator);
    }

    static <T extends Number> T readNumberPrompt(String prompt, Function<String, T> parser) {
        return scannerInstance.readNumberPrompt(prompt, parser);
    }

    static <T extends Number> T readNumberUntil(
            String prompt,
            Function<String, T> parser,
            Predicate<T> validator)
    {
        return scannerInstance.readNumberUntil(prompt, parser, validator);
    }

    static String readPrompt(String prompt) {
        return scannerInstance.readPrompt(prompt);
    }

    static String readUntil(String prompt, @NotNull Predicate<String> validator) {
        return scannerInstance.readUntil(prompt, validator);
    }

}
