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
package com.github.rickmvi.jtoolbox.console;

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.text.Stringifier;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.control.Condition;
import com.github.rickmvi.jtoolbox.util.Numbers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * Utility interface for console output operations.
 * <p>
 * Provides static methods to print, write, and format text to the standard output
 * or error streams. All methods handle {@code null} inputs gracefully, ignoring
 * them without throwing exceptions.
 * <p>
 * This interface is designed to simplify console I/O in a fluent and safe manner,
 * integrating with {@link Condition} for conditional execution and {@link Stringifier} /
 * {@link StringFormatter} for conversion and formatting.
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * IO.print("Hello, World!");
 * IO.println("Line with newline");
 * IO.format("Hello, %s!", "Joao");
 * IO.format("Hello, {}!", "Maria");
 * IO.format("Hello, {1:U}!", "Maria", "Joao");
 * IO.newline();
 * IO.to(System.err, "Error message");
 * IO.withOut(out -> out.println("Custom output logic"));
 * IO.withErr(err -> err.println("Custom error logic"));
 * }</pre>
 *
 * <p>
 * All methods are static and can be called without instantiating the interface.
 * </p>
 *
 * @see StringFormatter
 * @see Stringifier
 * @see Condition
 * @author Rick M. Viana
 * @since 1.7
 */
public interface IO {

    static void print(Object o) {
        If.whenNotNull(o)
                .then(() -> System.out.print(Stringifier.toString(o)))
                .otherwise(IO::newline);
    }
    
    static void print() {
        newline();
    }

    static void printf(Object o, Object... args) {
        If.whenNotNull(o)
                .then(() -> System.out.printf(Stringifier.toString(o), args))
                .otherwise(IO::newline);
    }

    static void printf() {
        newline();
    }

    static void println(Object o) {
        If.whenNotNull(o)
                .then(() -> System.out.println(Stringifier.toString(o)))
                .otherwise(IO::newline);
    }

    static void println() {
        newline();
    }

    static void format(Object format, Object @Nullable ... args) {
        If.whenNotNull(format)
                .then(() -> println(StringFormatter.format(Stringifier.toString(format), args)))
                .otherwise(IO::newline);
    }

    static void format() {
        newline();
    }

    static void interpolated(String format, Object @Nullable ... args) {
        If.whenNotNull(format)
                .then(() -> println(StringFormatter.interpolate(format, args)))
                .otherwise(IO::newline);
    }

    static void interpolated() {
        newline();
    }

    static void newline() {
        System.out.println();
    }

    static void newline(int count) {
        int value = Numbers.requiredNonNegative(count, 0);
        IO.format("$N:{0}", value);
    }

    static void to(PrintStream stream, Object text) {
        If.whenNotNull(stream)
                .then(() -> stream.print(Stringifier.toString(text)))
                .orThrow(() -> new IllegalArgumentException("Stream cannot be null"));
    }

    static void err(String text) {
        IO.to(System.err, text);
    }

    static void err(Object text) {
        IO.to(System.err, Stringifier.toString(text));
    }

    static void err(Object @Nullable ... args) {
        IO.format(System.err, args);
    }

    static void err(@NotNull Throwable t) {
        t.printStackTrace(System.err);
    }

    static void out(String text) {
        IO.to(System.out, text);
    }

    static void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }

    static void withErr(@NotNull Consumer<PrintStream> action) {
        action.accept(System.err);
    }

}
