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

import com.github.rickmvi.jtoolbox.console.utils.convert.Stringifier;
import com.github.rickmvi.jtoolbox.text.StringFormat;
import com.github.rickmvi.jtoolbox.control.ifs.If;
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
 * integrating with {@link If} for conditional execution and {@link Stringifier} /
 * {@link StringFormat} for conversion and formatting.
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * Output.print("Hello, World!");
 * Output.write("Line with newline");
 * Output.formatted("Hello, %s!", "Joao");
 * Output.formatted("Hello, {}!", "Maria");
 * Output.formatted("Hello, {1:U}!", "Maria", "Joao");
 * Output.newline();
 * Output.to(System.err, "Error message");
 * Output.withOut(out -> out.println("Custom output logic"));
 * Output.withErr(err -> err.println("Custom error logic"));
 * }</pre>
 *
 * <p>
 * All methods are static and can be called without instantiating the interface.
 * </p>
 *
 * @see StringFormat
 * @see Stringifier
 * @author Rick M. Viana
 * @since 1.5
 */
public interface Output {

    static void print(Object o) {
        If.runTrue(o != null, () -> System.out.print(Stringifier.toString(o)))
                .orElse(Output::newline);
    }

    static void printf(Object o, Object... args) {
        If.runTrue(o != null, () -> System.out.printf(Stringifier.toString(o), args))
                .orElse(Output::newline);
    }

    static void write(Object o) {
        If.runTrue(o != null, () -> System.out.println(Stringifier.toString(o)))
                .orElse(Output::newline);
    }

    static void interpolated(String format, Object @Nullable ... args) {
        If.runTrue(format != null, () -> write(StringFormat.interpolate(format, args)))
                .orElse(Output::newline);
    }

    static void formatted(Object format, Object @Nullable ... args) {
        If.runTrue(format != null, () -> print(StringFormat.format(Stringifier.toString(format), args)))
                .orElse(Output::newline);
    }

    static void newline() {
        System.out.println();
    }

    static void to(PrintStream stream, Object text) {
        If.runTrue(text != null, () -> stream.print(Stringifier.toString(text)))
                .orElseThrow(() -> new IllegalArgumentException("Text cannot be null"));
    }

    static void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }

    static void withErr(@NotNull Consumer<PrintStream> action) {
        action.accept(System.err);
    }

}
