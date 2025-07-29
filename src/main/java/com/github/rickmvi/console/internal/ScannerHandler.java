/*
 * Console API - Biblioteca utilitária para entrada, saída e formatação no console.
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
package com.github.rickmvi.console.internal;

import com.github.rickmvi.console.InputScanner;
import com.github.rickmvi.console.Location;
import com.github.rickmvi.console.convert.StringToBoolean;
import com.github.rickmvi.console.convert.StringToNumber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Scanner;

public class ScannerHandler implements InputScanner {

    @lombok.Getter
    @lombok.Setter(value = lombok.AccessLevel.PRIVATE)
    private Optional<Scanner> scanner = Optional.empty();

    @Override
    public void init() {
        scanner = Optional.of(new Scanner(System.in));
    }

    @Override
    public void init(@NotNull Scanner scanner) {
        this.scanner = Optional.of(scanner);
    }

    @Override
    public void locale(@NotNull Location location) {
        validate();
        scanner.ifPresent(sc -> {
            switch (location) {
                case US -> sc.useLocale(java.util.Locale.US);
                case PTBR -> sc.useLocale(java.util.Locale.of("pt", "BR"));
                case ROOT -> sc.useLocale(java.util.Locale.ROOT);
                case DEFAULT -> sc.useLocale(java.util.Locale.getDefault());
            }
        });
    }

    @Override
    public boolean hasNext() {
        validate();
        return scanner.map(Scanner::hasNext).orElse(false);
    }

    @Override
    public boolean hasNextLine() {
        validate();
        return scanner.map(Scanner::hasNextLine).orElse(false);
    }

    @Override
    @Contract(pure = true)
    public String next() {
        validate();
        return scanner.map(Scanner::next).orElse("");
    }

    @Override
    @Contract(pure = true)
    public String next(@NotNull String pattern) {
        validate();
        return scanner.map(sc -> sc.next(pattern)).orElse("");
    }

    @Override
    @Contract(pure = true)
    public String nextLine() {
        validate();
        return scanner.map(Scanner::nextLine).orElse("");
    }

    @Override
    @Contract(pure = true)
    public int nextInt() {
        return StringToNumber.toInt(nextSafe(), 0);
    }

    @Override
    @Contract(pure = true)
    public long nextLong() {
        return StringToNumber.toLong(nextSafe());
    }

    @Override
    @Contract(pure = true)
    public float nextFloat() {
        return StringToNumber.toFloat(nextSafe());
    }

    @Override
    @Contract(pure = true)
    public double nextDouble() {
        return StringToNumber.toDouble(nextSafe());
    }

    @Override
    @Contract(pure = true)
    public boolean nextBoolean() {
        return StringToBoolean.toBoolean(nextSafe());
    }

    @Override
    @Contract(pure = true)
    public String nextSafe() {
        try {
            return next();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void close() {
        validate();
        scanner.ifPresent(Scanner::close);
    }

    private void validate() {
        if (scanner.isEmpty())
            throw new IllegalStateException("Mistake: Scanner not initialized. Call ScannerUtils.init() first.");
    }
}
