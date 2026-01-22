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

import com.github.rickmvi.jtoolbox.console.IO;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.util.Numbers;
import com.github.rickmvi.jtoolbox.util.convert.BooleanParser;
import com.github.rickmvi.jtoolbox.util.convert.CharacterParser;
import com.github.rickmvi.jtoolbox.console.util.internal.InputScan;
import com.github.rickmvi.jtoolbox.control.Switch;
import com.github.rickmvi.jtoolbox.util.Try;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * Internal implementation of the {@link InputScan} interface.
 * <p>
 * This class handles low-level operations for console input using {@link Scanner},
 * including validation, localization, safe reading, and type conversion.
 * It provides robust handling of user input, encapsulating common input
 * operations and converting raw strings into typed values.
 */
public class Scanf implements InputScan, AutoCloseable {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private Optional<Scanner> scanner = Optional.empty();

    private void instance() {
        scanner = Optional.of(new Scanner(System.in));
    }

    @Override
    public void init(@NotNull Scanner scanner) {
        this.scanner = Optional.of(scanner);
    }

    @Override
    public void locale(@NotNull Location location) {
        ensureScannerInitialized();
        scanner.ifPresent(sc -> Switch.on(location)
                .caseValue(Location.US, () -> sc.useLocale(Locale.US))
                .caseValue(Location.PTBR, () -> sc.useLocale(Locale.of("pt", "BR")))
                .caseValue(Location.ROOT, () -> sc.useLocale(Locale.ROOT))
                .caseDefault(l -> sc.useLocale(Locale.getDefault()))
                .get());
    }

    @Override
    public boolean hasNext() {
        ensureScannerInitialized();
        return scanner.map(Scanner::hasNext).orElse(false);
    }

    @Override
    public boolean hasNextLine() {
        ensureScannerInitialized();
        return scanner.map(Scanner::hasNextLine).orElse(false);
    }

    @Override
    public String read() {
        ensureScannerInitialized();
        return scanner.map(Scanner::next).orElse("");
    }

    @Override
    public String read(@NotNull String pattern) {
        ensureScannerInitialized();
        return scanner.map(sc -> sc.next(pattern)).orElse("");
    }

    @Override
    public String readLine() {
        ensureScannerInitialized();
        return scanner.map(Scanner::nextLine).orElse("");
    }

    private <T> T readTypeSafe(Function<String, T> parser) {
        ensureScannerInitialized();
        AtomicReference<T> value = new AtomicReference<>();

        do {
            String input = readLine();

            Try.run(() -> value.set(parser.apply(input)))
                    .onFailure(e -> IO.err("Invalid entry. Please try again."));
        } while (value.get() == null);

        return value.get();
    }

    @Override
    public byte readByte() {
        return readTypeSafe(Numbers::toByte);
    }

    @Override
    public short readShort() {
        return readTypeSafe(Numbers::toShort);
    }

    @Override
    public int readInt() {
        return readTypeSafe(Numbers::toInt);
    }

    @Override
    public long readLong() {
        return readTypeSafe(Numbers::toLong);
    }

    @Override
    public float readFloat() {
        return readTypeSafe(Numbers::toFloat);
    }

    @Override
    public double readDouble() {
        return readTypeSafe(Numbers::toDouble);
    }

    @Override
    public boolean readBoolean() {
        return readTypeSafe(BooleanParser::toBoolean);
    }

    @Override
    public char readChar() {
        return readTypeSafe(CharacterParser::toChar);
    }

    @Override
    public BigDecimal readBigDecimal() {
        ensureScannerInitialized();
        BigDecimal value = readTypeSafe(this::parseBigDecimal);
        if (scanner.map(Scanner::hasNextLine).orElse(false)) {
            scanner.get().nextLine();
        }
        return value;
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull BigDecimal parseBigDecimal(String s) {
        return new BigDecimal(s);
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull BigInteger parseBigInteger(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigDecimal readBigDecimal(@NotNull Predicate<BigDecimal> predicate) {
        return readBigDecimalUntil("", predicate);
    }

    @Override
    public BigInteger readBigInteger() {
        ensureScannerInitialized();
        BigInteger value = readTypeSafe(this::parseBigInteger);
        if (scanner.map(Scanner::hasNextLine).orElse(false)) {
            scanner.get().nextLine();
        }
        return value;
    }

    @Override
    public BigInteger readBigInteger(@NotNull Predicate<BigInteger> predicate) {
        return readBigIntegerUntil("", predicate);
    }

    @Override
    public String readSafe() {
        return Try.of(this::read)
                .onFailure(Throwable::printStackTrace)
                .toOptional()
                .orElse("");
    }

    @Override
    public void close() {
        ensureScannerInitialized();
        scanner.ifPresent(Scanner::close);
    }

    private void ensureScannerInitialized() {
        If.whenEmpty(scanner).then(this::instance).execute();
    }

    @Override
    public String readPrompt(String prompt) {
        ensureScannerInitialized();
        if (prompt == null || prompt.isEmpty()) return "";
        displayPrompt(prompt);
        return readLine();
    }

    private <T> T readValueUntil(String prompt, Function<String, T> parser, @NotNull Predicate<T> validator) {
        Objects.requireNonNull(prompt, "prompt cannot be null");
        Objects.requireNonNull(parser, "parser cannot be null");

        ensureScannerInitialized();

        AtomicReference<T> value = new AtomicReference<>();

        Function<String, T> safeParser = input -> Try.of(() -> parser.apply(input))
                .onFailure(e -> IO.err("Invalid entry. Please try again."))
                .toOptional()
                .orElse(null);

        do {
            displayPrompt(prompt);
            T parsedValue = safeParser.apply(readLine());
            value.set(parsedValue);

            If.whenNotNull(value.get())
                    .and(!validator.test(value.get()))
                    .then(() -> IO.err("Value does not meet validation criteria. Try again."))
                    .execute();

            IO.newline();
        } while (value.get() == null || !validator.test(value.get()));

        return value.get();
    }

    private static void displayPrompt(String prompt) {
        If.whenNotNull(prompt)
                .and(!prompt.isEmpty())
                .then(() -> IO.format("{0}> ", prompt))
                .execute();
    }

    @Override
    public byte readBytePrompt(String prompt) {
        return readByteUntil(prompt, b -> true);
    }

    @Override
    public byte readByteUntil(String prompt, Predicate<Byte> predicate) {
        return readValueUntil(prompt, Numbers::toByte, predicate);
    }

    @Override
    public short readShortPrompt(String prompt) {
        return readShortUntil(prompt, s -> true);
    }

    @Override
    public short readShortUntil(String prompt, Predicate<Short> predicate) {
        return readValueUntil(prompt, Numbers::toShort, predicate);
    }

    @Override
    public int readIntPrompt(String prompt) {
        return readIntUntil(prompt, i -> true);
    }

    @Override
    public int readIntUntil(String prompt, IntPredicate validator) {
        Predicate<Integer> wrappedValidator = validator::test;
        return readValueUntil(prompt, Numbers::toInt, wrappedValidator);
    }

    @Override
    public long readLongPrompt(String prompt) {
        return readLongUntil(prompt, l -> true);
    }

    @Override
    public long readLongUntil(String prompt, Predicate<Long> predicate) {
        return readValueUntil(prompt, Numbers::toLong, predicate);
    }

    @Override
    public float readFloatPrompt(String prompt) {
        return readFloatUntil(prompt, f -> true);
    }

    @Override
    public float readFloatUntil(String prompt, Predicate<Float> predicate) {
        return readValueUntil(prompt, Numbers::toFloat, predicate);
    }

    @Override
    public double readDoublePrompt(String prompt) {
        return readDoubleUntil(prompt, d -> true);
    }

    @Override
    public double readDoubleUntil(String prompt, Predicate<Double> predicate) {
        return readValueUntil(prompt, Numbers::toDouble, predicate);
    }

    @Override
    public boolean readBooleanPrompt(String prompt) {
        return readBooleanUntil(prompt, b -> true);
    }

    @Override
    public boolean readBooleanUntil(String prompt, Predicate<Boolean> predicate) {
        return readValueUntil(prompt, BooleanParser::toBoolean, predicate);
    }

    @Override
    public char readCharPrompt(String prompt) {
        return readCharUntil(prompt, c -> true);
    }

    @Override
    public char readCharUntil(String prompt, Predicate<Character> predicate) {
        return readValueUntil(prompt, CharacterParser::toChar, predicate);
    }

    @Override
    public BigDecimal readBigDecimalPrompt(String prompt) {
        return readBigDecimalUntil(prompt, bd -> true);
    }

    @Override
    public BigDecimal readBigDecimalUntil(String prompt, Predicate<BigDecimal> predicate) {
        return readValueUntil(prompt, this::parseBigDecimal, predicate);
    }

    @Override
    public BigInteger readBigIntegerPrompt(String prompt) {
        return readBigIntegerUntil(prompt, bi -> true);
    }

    @Override
    public BigInteger readBigIntegerUntil(String prompt, Predicate<BigInteger> predicate) {
        return readValueUntil(prompt, this::parseBigInteger, predicate);
    }

    @Override
    public <T extends Number> T readNumberPrompt(String prompt, Function<String, T> parser) {
        return readNumberUntil(prompt, parser, t -> true);
    }

    @Override
    public String readUntil(String prompt, Predicate<String> validator) {
        ensureScannerInitialized();
        if (prompt == null || prompt.isEmpty()) return "";
        Objects.requireNonNull(validator, "validator cannot be null");

        AtomicReference<String> input = new AtomicReference<>("");
        do {
            displayPrompt(prompt);
            input.set(readLine());

            If.onlyIf(!validator.test(input.get()), () -> IO.err("Value does not meet validation criteria. Try again."));
            IO.newline();
        } while (!validator.test(input.get()));

        return input.get();
    }

    public <T extends Number> T readNumberUntil(
            String prompt,
            Function<String, T> parser,
            Predicate<T> validator
    ) {
        return readValueUntil(prompt, parser, validator);
    }
}