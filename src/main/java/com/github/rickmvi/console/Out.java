package com.github.rickmvi.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.function.Consumer;

@lombok.experimental.UtilityClass
public class Out {

    public void print(@Nullable String text) {
        if (text == null || text.isEmpty()) return;
        System.out.print(text);
    }

    public void printLine(@Nullable String text) {
        if (text == null || text.isEmpty()) return;
        System.out.println(text);
    }

    public void printFormatted(@NotNull String format, Object... args) {
        if (format.isEmpty() || args == null || args.length == 0) return;
        System.out.printf(format, args);
    }

    public void emptyLine() {
        System.out.println();
    }

    public void printObject(@Nullable Object obj) {
        if (obj != null) System.out.print(obj);
    }

    public void printLineObject(@Nullable Object obj) {
        if (obj != null) System.out.println(obj);
    }

    public void printFormattedSafe(@Nullable Object format, @Nullable Object... args) {
        if (format != null && args != null)
            System.out.printf(format.toString(), args);
    }

    public void printDebug(@Nullable Object obj) {
        if (obj != null) System.out.println("[DEBUG] " + obj);
    }

    public void printStackTrace(@Nullable Throwable t) {
        if (t != null) t.printStackTrace(System.out);
    }

    public void withOut(@NotNull Consumer<PrintStream> action) {
        action.accept(System.out);
    }
}
