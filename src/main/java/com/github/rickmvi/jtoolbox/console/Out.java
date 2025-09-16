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
import com.github.rickmvi.jtoolbox.control.Conditionals;
import com.github.rickmvi.jtoolbox.utils.NullSafety;
import com.github.rickmvi.jtoolbox.text.Formatted;
import com.github.rickmvi.jtoolbox.debug.SLogger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Map;

import static com.github.rickmvi.jtoolbox.control.Conditionals.ifTrue;

/**
 * Utility class for simplified console output operations.
 * <p>
 * This class provides static methods to print text and objects to the standard output (console)
 * with built-in null and empty checks to avoid printing undesired empty or null values.
 * It also supports formatted output and conditional execution using the {@link Conditionals} utility.
 * <p>
 * The class is designed as a utility with static methods only, using Lombok's {@code @UtilityClass}
 * to prevent instantiation.
 *
 * @author Rick M. Viana
 * @since 1.2
 */
@lombok.experimental.UtilityClass
public class Out {

    /**
     * Prints the string representation of the given object to the standard output without a newline.
     * <p>
     * Does nothing if the object is {@code null}, or if its string representation is empty.
     *
     * @param o the object to print; may be {@code null}
     * @deprecated Use an alternative method such as {@link #display(Object)} for printing,
     * as this method will be removed in future versions.
     */
    @Deprecated
    public static void print(@Nullable Object o) {
        ifTrue(NullSafety.nonNull(o), () -> System.out.print(o));
    }

    /**
     * Prints the string representation of the given object to the console without a newline.
     * <p>
     * Does nothing if the object is {@code null}.
     *
     * @param o the object to print, may be {@code null}
     */
    public static void display(@Nullable Object o) {
        ifTrue(NullSafety.nonNull(o), () -> System.out.print(Stringifier.toString(o)));
    }

    /**
     * Prints the string representation of the given object to the console followed by a newline.
     * <p>
     * Does nothing if the object is {@code null}.
     *
     * @param o the object to print, may be {@code null}
     */
    @Deprecated
    public static void printLine(@Nullable Object o) {
        ifTrue(o != null, () -> System.out.println(o));
    }

    /**
     * Prints the string representation of the specified object to the console followed by a newline.
     * Does nothing if the object is {@code null} or its string representation is empty.
     *
     * @param o the object to be printed; may be {@code null}
     */
    public static void write(@Nullable Object o) {
        ifTrue(NullSafety.nonNull(o), () -> System.out.println(Stringifier.toString(o)));
    }

    /**
     * Prints a formatted string to the console using the string representation of the given format object
     * and arguments.
     * <p>
     * Does nothing if the format or arguments are {@code null}.
     *
     * @param format the format string or object, may be {@code null}
     * @param args   the arguments referenced by the format specifiers, may be {@code null}
     */
    @Deprecated
    public static void printf(@Nullable Object format, @Nullable Object... args) {
        ifTrue(NullSafety.nonNull(format)
                        && Objects.nonNull(args),
                () -> System.out.printf(format.toString(), args));
    }

    /**
     * Prints a formatted string to the console using the provided format object and arguments.
     * The string representation of the format object is used as the format string.
     * The method does nothing if the format object or the arguments are {@code null}.
     *
     * @param format the format object whose string representation is used as the format; may be {@code null}
     * @param args   the arguments referenced by the format specifiers; may be {@code null}
     */
    public static void formatted(@Nullable Object format, @Nullable Object... args) {
        ifTrue(NullSafety.nonNull(format)
                        && Objects.nonNull(args),
                () -> display(Formatted.format(Stringifier.toString(format), args)));
    }

    /**
     * Prints an empty line to the console.
     */
    public static void newline() {
        System.out.println();
    }

    /**
     * Prints the string representation of the given object to the specified {@link PrintStream}.
     * If the provided object is {@code null} or its string representation is empty,
     * nothing will be printed.
     *
     * @param stream the output stream to which the object should be printed; must not be null
     * @param text   the object to be printed; may be null
     * @throws NullPointerException if the provided {@code stream} is null
     */
    public static void to(@NotNull PrintStream stream, @Nullable Object text) {
        ifTrue(NullSafety.nonNull(text),
                () -> stream.print(Stringifier.toString(text)));
    }

    /**
     * Logs the debug information of the given object if it is not {@code null}.
     * Converts the object to its string representation using {@link Stringifier#toString(Object)}
     * and logs it using {@link SLogger#debug(String)}.
     *
     * @param o the object to be debugged; may be {@code null}
     */
    public static void debug(@Nullable Object o) {
        ifTrue(Objects.nonNull(o), () -> SLogger.debug(Stringifier.toString(o)));
    }

    /**
     * Prints the stack trace of the given throwable to the console output stream.
     * <p>
     * Does nothing if the throwable is {@code null}.
     *
     * @param t the throwable whose stack trace is to be printed, may be {@code null}
     */
    public static void outputStackTrace(@Nullable Throwable t) {
        Conditionals.ifFalse(Objects.isNull(t), () -> t.printStackTrace(System.out));
    }

    /**
     * Provides direct access to the standard output {@link PrintStream} for custom printing actions.
     *
     * @param action a consumer that accepts the system standard output stream
     */
    public static void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }

    /**
     * Prints a formatted string by applying token-based placeholders,
     * delegating formatting to {@link Formatted#formatTokens(String, Object...)}.
     * <p>
     * Does nothing if the template is empty.
     *
     * @param template the token-based formatting template string, must not be empty
     * @param args     the arguments to replace token placeholders in the template
     */
    public static void printTokenFormatted(@NotNull String template, Object... args) {
        ifTrue(!template.isEmpty(), () -> display(Formatted.formatTokens(template, args)));
    }

    /**
     * Prints a formatted string using named placeholders replaced by values in the given map.
     * Delegates to {@link Formatted#formatNamed(String, Map, boolean)}.
     * <p>
     * Does nothing if the template is empty or the value map is empty.
     *
     * @param template      the named placeholder template string, must not be empty
     * @param values        a map of placeholder names to replacement values, must not be empty
     * @param failIfMissing if {@code true}, formatting fails if a placeholder is missing in the map
     */
    public static void printNamedFormatted(@NotNull String template, @NotNull Map<String, ?> values, boolean failIfMissing) {
        ifTrue(!template.isEmpty() && !values.isEmpty(),
                () -> display(Formatted.formatNamed(template, values, failIfMissing)));
    }

    /**
     * Prints a formatted string using named placeholders replaced by values in the given map.
     * Defaults to not failing if any placeholder is missing.
     *
     * @param template the named placeholder template string, must not be empty
     * @param values   a map of placeholder names to replacement values, must not be empty
     */
    public static void printNamedFormatted(@NotNull String template, @NotNull Map<String, ?> values) {
        printNamedFormatted(template, values, false);
    }
}
