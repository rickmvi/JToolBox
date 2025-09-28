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
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.control.ifs.If;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * @author Rick M. Viana
 * @since 1.3
 */
public interface Output {

    static void print(Object o) {
        If.runTrue(o != null, () -> System.out.print(Stringifier.toString(o))).run();
    }

    @Contract(pure = true)
    static void write(Object o) {
        If.runTrue(o != null, () -> System.out.println(Stringifier.toString(o))).run();
    }

    @Contract(pure = true)
    static void printf(Object format, Object @Nullable ... args) {
        If.runTrue(format != null, () -> System.out.printf(Stringifier.toString(format), args)).run();
    }

    static void formatted(Object format, Object @Nullable ... args) {
        If.runTrue(format != null,
                () -> print(StringFormatter.format(Stringifier.toString(format), args))).run();
    }

    static void newline() {
        System.out.println();
    }

    static void to(PrintStream stream, Object text) {
        If.runTrue(text != null, () -> stream.print(Stringifier.toString(text))).run();
    }

    static void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }

    static void withErr(@NotNull Consumer<PrintStream> action) {
        action.accept(System.err);
    }

}
