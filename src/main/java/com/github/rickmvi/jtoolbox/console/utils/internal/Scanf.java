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
package com.github.rickmvi.jtoolbox.console.utils.internal;

import com.github.rickmvi.jtoolbox.console.IO;
import com.github.rickmvi.jtoolbox.console.utils.convert.BooleanParser;
import com.github.rickmvi.jtoolbox.console.utils.convert.CharacterParser;
import com.github.rickmvi.jtoolbox.console.utils.convert.NumberParser;
import com.github.rickmvi.jtoolbox.console.utils.Location;
import com.github.rickmvi.jtoolbox.control.Do;
import com.github.rickmvi.jtoolbox.control.Switch;
import com.github.rickmvi.jtoolbox.utils.Try;
import com.github.rickmvi.jtoolbox.utils.condition.ObjectC;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Contract;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.console.utils.Scan;
import com.github.rickmvi.jtoolbox.utils.SafeRun;
import com.github.rickmvi.jtoolbox.utils.constants.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
 * <p>
 * This class is used internally by the public-facing {@link Scan}.
 */
public class  Scanf implements InputScan, AutoCloseable {

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
    @Contract(pure = true)
    public String read() {
        ensureScannerInitialized();
        return scanner.map(Scanner::next).orElse("");
    }

    @Override
    @Contract(pure = true)
    public String read(@NotNull String pattern) {
        ensureScannerInitialized();
        return scanner.map(sc -> sc.next(pattern)).orElse("");
    }

    @Override
    @Contract(pure = true)
    public String readLine() {
        ensureScannerInitialized();
        return scanner.map(Scanner::nextLine).orElse("");
    }

    @Override
    public byte readByte() {
        ensureScannerInitialized();
        byte value = NumberParser.toByte(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    public short readShort() {
        ensureScannerInitialized();
        short value = NumberParser.toShort(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    @Contract(pure = true)
    public int readInt() {
        ensureScannerInitialized();
        int value = NumberParser.toInt(readSafe());
        clearBuffer();
        return value;
    }

    private boolean isNextLine() {
        return scanner.isPresent() && scanner.get().hasNextLine();
    }

    @Override
    @Contract(pure = true)
    public long readLong() {
        ensureScannerInitialized();
        long value = NumberParser.toLong(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    @Contract(pure = true)
    public float readFloat() {
        ensureScannerInitialized();
        float value = NumberParser.toFloat(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    @Contract(pure = true)
    public double readDouble() {
        ensureScannerInitialized();
        double value = NumberParser.toDouble(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    @Contract(pure = true)
    public boolean readBoolean() {
        ensureScannerInitialized();
        boolean value = BooleanParser.toBoolean(readSafe());
        clearBuffer();
        return value;
    }

    @Override
    public char readChar() {
        ensureScannerInitialized();
        return CharacterParser.toChar(readSafe());
    }

    @Override
    public BigDecimal readBigDecimal() {
        ensureScannerInitialized();
        BigDecimal value = scanner.map(Scanner::nextBigDecimal).orElse(BigDecimal.ZERO);
        clearBuffer();
        return value;
    }

    @Override
    public BigDecimal readBigDecimal(@NotNull Predicate<BigDecimal> predicate) {
        ensureScannerInitialized();
        BigDecimal value = scanner.map(Scanner::nextBigDecimal).filter(predicate).orElse(BigDecimal.ZERO);
        clearBuffer();
        return value;
    }

    @Override
    public BigInteger readBigInteger() {
        ensureScannerInitialized();
        BigInteger value = scanner.map(Scanner::nextBigInteger).orElse(BigInteger.ZERO);
        clearBuffer();
        return value;
    }

    @Override
    public BigInteger readBigInteger(@NotNull Predicate<BigInteger> predicate) {
        ensureScannerInitialized();
        BigInteger value = scanner.map(Scanner::nextBigInteger).filter(predicate).orElse(BigInteger.ZERO);
        clearBuffer();
        return value;
    }

    @Override
    public String readSafe() {
        return SafeRun.attemptOrElseGet(
                this::read,
                () -> "",
                Constants.NEXTSAFE_FAILED
        );
    }

    @Override
    public void close() {
        ensureScannerInitialized();
        scanner.ifPresent(Scanner::close);
    }

    private void ensureScannerInitialized() {
        If.isTrue(scanner.isEmpty(), this::instance).run();
    }

    private void clearBuffer() {
        scanner.ifPresent(scan -> {
            if (scan.hasNextLine()) scan.nextLine();
        });
    }

    @Override
    public String readPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) return "";
        displayPrompt(prompt);
        return readLine();
    }

    private static void displayPrompt(String prompt) {
        If.isTrue(!ObjectC.isNullOrEmpty(prompt), () -> IO.format("{0} ", prompt)).run();
    }

    @Override
    public byte readBytePrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readByte();
    }

    @Override
    public byte readByteUntil(String prompt, Predicate<Byte> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Byte> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readByte())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public short readShortPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readShort();
    }

    @Override
    public short readShortUntil(String prompt, Predicate<Short> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Short> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readShort())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public int readIntPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";

        AtomicInteger result  = new AtomicInteger(Integer.MIN_VALUE);
        AtomicBoolean success = new AtomicBoolean(false);

        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> {
                result.set(readInt());
                success.set(true);
            }).onFailure(e -> IO.newline()).orThrow();

        }, () -> !success.get());

        return result.get();
    }

    @Override
    public int readIntUntil(String prompt, IntPredicate validator) {
        ensureScannerInitialized();
        if (ObjectC.isNull(prompt)) throw new IllegalArgumentException("prompt cannot be null");
        if (validator == null)      throw new IllegalArgumentException("validator cannot be null");

        AtomicInteger input = new AtomicInteger();
        Do.runWhile(() -> {
            displayPrompt(prompt);

            Try.run(() -> input.set(readInt())).
                    onFailure(e -> input.set(Integer.MIN_VALUE)).
                    orThrow();

            IO.newline();
        }, () -> !validator.test(input.get()));
        return input.get();
    }

    @Override
    public long readLongPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readLong();
    }

    @Override
    public long readLongUntil(String prompt, Predicate<Long> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Long> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readLong())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public float readFloatPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readFloat();
    }

    public float readFloatUntil(String prompt, Predicate<Float> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Float> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readFloat())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public double readDoublePrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readDouble();
    }

    @Override
    public double readDoubleUntil(String prompt, Predicate<Double> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Double> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readDouble())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public boolean readBooleanPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readBoolean();
    }

    @Override
    public boolean readBooleanUntil(String prompt, Predicate<Boolean> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Boolean> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readBoolean())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public char readCharPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readChar();
    }

    @Override
    public char readCharUntil(String prompt, Predicate<Character> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<Character> value = new AtomicReference<>();
        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readChar())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public BigDecimal readBigDecimalPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readBigDecimal();
    }

    @Override
    public BigDecimal readBigDecimalUntil(String prompt, Predicate<BigDecimal> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<BigDecimal> value = new AtomicReference<>();

        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readBigDecimal())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public BigInteger readBigIntegerPrompt(String prompt) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        displayPrompt(prompt);
        return readBigInteger();
    }

    @Override
    public BigInteger readBigIntegerUntil(String prompt, Predicate<BigInteger> predicate) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (predicate == null) throw new IllegalArgumentException("predicate cannot be null");

        AtomicReference<BigInteger> value = new AtomicReference<>();

        String finalPrompt = prompt;
        Do.runWhile(() -> {
            displayPrompt(finalPrompt);

            Try.run(() -> value.set(readBigInteger())).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
        }, () -> value.get() == null || !predicate.test(value.get()));

        return value.get();
    }

    @Override
    public <T extends Number> T readNumberPrompt(String prompt, Function<String, T> parser) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) prompt = "";
        if (parser == null) throw new IllegalArgumentException("parser cannot be null");
        displayPrompt(prompt);
        return parser.apply(read());
    }

    @Override
    public String readUntil(String prompt, Predicate<String> validator) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) return "";
        if (validator == null) throw new IllegalArgumentException("validator cannot be null");

        AtomicReference<String> input = new AtomicReference<>("");
        Do.runWhile(() -> {
            displayPrompt(prompt);
            input.set(readLine());
            IO.newline();
        }, () -> !validator.test(input.get()));
        return input.get();
    }

    @Override
    public <T extends Number> T readNumberUntil(
            String prompt,
            Function<String, T> parser,
            Predicate<T> validator
    ) {
        ensureScannerInitialized();
        if (ObjectC.isNullOrEmpty(prompt)) throw new IllegalArgumentException("prompt cannot be null");
        if (parser == null)                throw new IllegalArgumentException("parser cannot be null");
        if (validator == null)             throw new IllegalArgumentException("validator cannot be null");

        AtomicReference<T> value = new AtomicReference<>();
        Do.runWhile(() -> {
            displayPrompt(prompt);

            Try.run(() -> value.set(parser.apply(read()))).
                    onFailure(e -> value.set(null)).
                    orThrow();

            IO.newline();
            }, () -> value.get() == null || !validator.test(value.get()));

        return value.get();
    }
}
