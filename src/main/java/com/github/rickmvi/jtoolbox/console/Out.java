package com.github.rickmvi.jtoolbox.console;

import com.github.rickmvi.jtoolbox.control.Conditionals;
import com.github.rickmvi.jtoolbox.text.Formatted;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Map;
import java.util.function.Consumer;

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
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class Out {

    /**
     * Prints the given text to the console without a newline.
     * <p>
     * Does nothing if the text is {@code null} or empty.
     *
     * @param text the text to print, may be {@code null} or empty
     */
    public static void print(@Nullable String text) {
        Conditionals.ifTrue(text != null && !text.isEmpty(), () -> System.out.print(text));
    }

    /**
     * Prints the given text to the console followed by a newline.
     * <p>
     * Does nothing if the text is {@code null} or empty.
     *
     * @param text the text to print, may be {@code null} or empty
     */
    public static void printLine(@Nullable String text) {
        Conditionals.ifTrue(text != null && !text.isEmpty(), () -> System.out.println(text));
    }

    /**
     * Prints a formatted string to the console using the specified format string and arguments.
     * <p>
     * Does nothing if the format string is empty.
     *
     * @param format the format string (as in {@link String#format}), must not be empty
     * @param args   the arguments referenced by the format specifiers in the format string
     */
    public static void printf(@NotNull String format, Object... args) {
        Conditionals.ifTrue(!format.isEmpty(), () -> System.out.printf(format, args));
    }

    /**
     * Prints an empty line to the console.
     */
    public static void emptyLine() {
        System.out.println();
    }

    /**
     * Prints the string representation of the given object to the console without a newline.
     * <p>
     * Does nothing if the object is {@code null}.
     *
     * @param obj the object to print, may be {@code null}
     */
    public static void printObject(@Nullable Object obj) {
        Conditionals.ifTrue(obj != null, () -> System.out.print(obj));
    }

    /**
     * Prints the string representation of the given object to the console followed by a newline.
     * <p>
     * Does nothing if the object is {@code null}.
     *
     * @param obj the object to print, may be {@code null}
     */
    public static void printLineObject(@Nullable Object obj) {
        Conditionals.ifTrue(obj != null, () -> System.out.println(obj));
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
    public static void printfObject(@Nullable Object format, @Nullable Object... args) {
        Conditionals.ifTrue(format != null && args != null, () -> System.out.printf(format.toString(), args));
    }

    /**
     * Prints a debug message prefixed with "[DEBUG]" followed by the string representation of the given object.
     * <p>
     * Does nothing if the object is {@code null}.
     *
     * @param obj the debug message object, may be {@code null}
     */
    public static void printDebug(@Nullable Object obj) {
        Conditionals.ifTrue(obj != null, () -> System.out.println("[DEBUG] " + obj));
    }

    /**
     * Prints the stack trace of the given throwable to the console output stream.
     * <p>
     * Does nothing if the throwable is {@code null}.
     *
     * @param t the throwable whose stack trace is to be printed, may be {@code null}
     */
    public static void printStackTrace(@Nullable Throwable t) {
        Conditionals.ifTrue(t != null, () -> t.printStackTrace(System.out));
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
     * Prints a formatted string by applying a custom formatting template with placeholders,
     * delegating formatting to {@link Formatted#format(String, Object...)}.
     * <p>
     * Does nothing if the template is empty.
     *
     * @param template the formatting template string, must not be empty
     * @param args     the arguments to replace placeholders in the template
     */
    public static void printFormatted(@NotNull String template, Object... args) {
        Conditionals.ifTrue(!template.isEmpty(), () -> print(Formatted.format(template, args)));
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
        Conditionals.ifTrue(!template.isEmpty(), () -> print(Formatted.formatTokens(template, args)));
    }

    /**
     * Prints a formatted string using named placeholders replaced by values in the given map.
     * Delegates to {@link Formatted#formatNamed(String, Map, boolean)}.
     * <p>
     * Does nothing if the template is empty or the values map is empty.
     *
     * @param template      the named placeholder template string, must not be empty
     * @param values        a map of placeholder names to replacement values, must not be empty
     * @param failIfMissing if {@code true}, formatting fails if a placeholder is missing in the map
     */
    public static void printNamedFormatted(@NotNull String template, @NotNull Map<String, ?> values, boolean failIfMissing) {
        Conditionals.ifTrue(!template.isEmpty() && !values.isEmpty(),
                () -> print(Formatted.formatNamed(template, values, failIfMissing)));
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
