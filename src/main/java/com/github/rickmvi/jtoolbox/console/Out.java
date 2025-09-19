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
import com.github.rickmvi.jtoolbox.control.Condition;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.debug.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.io.PrintStream;

import static com.github.rickmvi.jtoolbox.control.Condition.ifTrue;

/**
 * Utility class for simplified console output operations.
 * <p>
 * This class provides static methods to print text and objects to the standard output (console)
 * with built-in null and empty checks to avoid printing undesired empty or null values.
 * It also supports formatted output and conditional execution using the {@link Condition} utility.
 * <p>
 * The class is designed as a utility with static methods only, using Lombok's {@code @UtilityClass}
 * to prevent instantiation.
 *
 * @author Rick M. Viana
 * @since 1.2
 */
@lombok.experimental.UtilityClass
public class Out {

    public static void print(@Nullable Object o) {
        ifTrue(o != null, () -> System.out.print(Stringifier.toString(o)));
    }

    public static void write(@Nullable Object o) {
        ifTrue(o != null, () -> System.out.println(Stringifier.toString(o)));
    }

    public static void formatted(@Nullable Object format, @Nullable Object... args) {
        ifTrue(format != null && args != null,
                () -> print(StringFormatter.format(Stringifier.toString(format), args)));
    }

    public static void newline() {
        System.out.println();
    }

    public static void to(@NotNull PrintStream stream, @Nullable Object text) {
        ifTrue(text != null, () -> stream.print(Stringifier.toString(text)));
    }

    public static void debug(@Nullable Object o) {
        ifTrue(o != null, () -> Logger.debug(Stringifier.toString(o)));
    }

    public static void outputStackTrace(@Nullable Throwable t) {
        Condition.ifFalse(t == null, () -> t.printStackTrace(System.out));
    }

    public static void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }

    public static void withErr(@NotNull Consumer<PrintStream> action) {
        action.accept(System.err);
    }
}
