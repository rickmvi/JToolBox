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
package com.github.rickmvi.jtoolbox.console.util.internal;

import com.github.rickmvi.jtoolbox.console.util.Location;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Predicate;

/**
 * Contract for input readers capable of retrieving tokens, lines,
 * and typed values from console input.
 * <p>
 * This interface abstracts reading mechanisms, allowing initialization
 * with default or custom {@link java.util.Scanner}, locale configuration,
 * and safe typed reads.
 */
public interface InputScan extends InputReader {

    /** Initializes the scanner with a custom {@link java.util.Scanner} instance. */
    void init(@NotNull java.util.Scanner scanner);

    /** Sets the locale for parsing numbers, dates, etc. */
    void locale(@NotNull Location location);

    /** Checks if another token is available. */
    boolean hasNext();

    /** Checks if another line is available. */
    boolean hasNextLine();

    /** Reads the next token (single word). */
    String read();

    /** Reads the next token matching a regex. */
    String read(@NotNull String pattern);

    /** Reads the next full line. */
    String readLine();

    byte readByte();

    short readShort();

    /** Reads the next token as {@code int}. */
    int readInt();

    /** Reads the next token as {@code long}. */
    long readLong();

    /** Reads the next token as {@code float}. */
    float readFloat();

    /** Reads the next token as {@code double}. */
    double readDouble();

    /** Reads the next token as {@code boolean}. */
    boolean readBoolean();

    char readChar();

    BigDecimal readBigDecimal();

    BigDecimal readBigDecimal(@NotNull Predicate<BigDecimal> predicate);

    BigInteger readBigInteger();

    BigInteger readBigInteger(@NotNull Predicate<BigInteger> predicate);

    /** Safely reads the next token, returning {@code ""} if unavailable or invalid. */
    String readSafe();

    /** Closes the underlying scanner. */
    void close();
}
