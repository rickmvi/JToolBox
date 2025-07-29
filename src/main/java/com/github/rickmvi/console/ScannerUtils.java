package com.github.rickmvi.console;

import com.github.rickmvi.console.internal.ScannerHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

@lombok.experimental.UtilityClass
public class ScannerUtils {

    @lombok.Getter(value = lombok.AccessLevel.PRIVATE)
    private final static ScannerHandler core = new ScannerHandler();

    public static void init() {
        core.init();
    }

    public static void init(@NotNull Scanner scanner) {
        core.init(scanner);
    }

    public static void locale(@NotNull Location location) {
        core.locale(location);
    }

    public static boolean hasNext() {
        return core.hasNext();
    }

    public static boolean hasNextLine() {
        return core.hasNextLine();
    }

    @Contract(pure = true)
    public static String next() {
        return core.next();
    }

    @Contract(pure = true)
    public static String next(@NotNull String pattern) {
        return core.next(pattern);
    }

    @Contract(pure = true)
    public static String nextLine() {
        return core.nextLine();
    }

    @Contract(pure = true)
    public static int nextInt() {
        return core.nextInt();
    }

    @Contract(pure = true)
    public static long nextLong() {
        return core.nextLong();
    }

    @Contract(pure = true)
    public static double nextDouble() {
        return core.nextDouble();
    }

    @Contract(pure = true)
    public static float nextFloat() {
        return core.nextFloat();
    }

    @Contract(pure = true)
    public static boolean nextBoolean() {
        return core.nextBoolean();
    }

    @Contract(pure = true)
    public static String nextSafe() {
        return core.nextSafe();
    }

    public static void close() {
        core.close();
    }
}
