package com.github.rickmvi.jtoolbox.utils;

import com.github.rickmvi.jtoolbox.debug.SLogger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.github.rickmvi.jtoolbox.control.ConditionalHelper.ifTrueThrow;

public final class Primitives {

    @Contract(value = " -> fail", pure = true)
    private Primitives() {
        throw new AssertionError("No com.github.rickmvi.jtoolbox.utils.Primitives instances for you!");
    }

    /* =================================== EQUALS METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean equals(byte x, byte y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean equals(short x, short y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean equals(int x, int y) {
        return (x ^ y) == 0;
    }

    @Contract(pure = true)
    public static boolean equals(long x, long y) {
        return (x ^ y) == 0L;
    }

    @Contract(pure = true)
    public static boolean equals(float x, float y) {
        return Float.compare(x, y) == 0f;
    }

    @Contract(pure = true)
    public static boolean equals(double x, double y) {
        return Double.compare(x, y) == 0;
    }

    @Contract(pure = true)
    public static boolean equals(char x, char y) {
        return x == y;
    }

    @Contract(pure = true)
    public static boolean equals(boolean x, boolean y) {
        return x == y;
    }

    /* =================================== NOT EQUALS METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean notEquals(byte x, byte y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(short x, short y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(int x, int y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(long x, long y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(float x, float y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(double x, double y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(char x, char y) {
        return !equals(x, y);
    }

    @Contract(pure = true)
    public static boolean notEquals(boolean x, boolean y) {
        return !equals(x, y);
    }

    /* =================================== IS ZERO METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isZero(byte value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isZero(short value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isZero(int value) {
        return value == 0;
    }

    @Contract(pure = true)
    public static boolean isZero(long value) {
        return value == 0L;
    }

    @Contract(pure = true)
    public static boolean isZero(float value) {
        return Float.compare(value, 0.0f) == 0f;
    }

    @Contract(pure = true)
    public static boolean isZero(double value) {
        return Double.compare(value, 0.0d) == 0;
    }

    /* =================================== IS POSITIVE METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isPositive(byte value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(short value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(int value) {
        return value > 0;
    }

    @Contract(pure = true)
    public static boolean isPositive(long value) {
        return value > 0L;
    }

    @Contract(pure = true)
    public static boolean isPositive(float value) {
        return Float.compare(value, 0.0f) > 0f;
    }

    @Contract(pure = true)
    public static boolean isPositive(double value) {
        return Double.compare(value, 0.0d) > 0;
    }

    /* =================================== IS NEGATIVE METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isNegative(byte value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(short value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(int value) {
        return value < 0;
    }

    @Contract(pure = true)
    public static boolean isNegative(long value) {
        return value < 0L;
    }

    @Contract(pure = true)
    public static boolean isNegative(float value) {
        return Float.compare(value, -0.0f) < 0f;
    }

    @Contract(pure = true)
    public static boolean isNegative(double value) {
        return Double.compare(value, -0.0d) < 0;
    }

    /* =================================== IS GREATER OR EQUALS METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(byte x, byte y) {
        return equals(x, y) || x > y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(short x, short y) {
        return equals(x, y) || x > y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(int x, int y) {
        return equals(x, y) || x > y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(long x, long y) {
        return equals(x, y) || x > y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(float x, float y) {
        return equals(x, y) || x > y;
    }

    @Contract(pure = true)
    public static boolean isGreaterOrEqual(double x, double y) {
        return equals(x, y) || x > y;
    }

    /* =================================== IS LESS OR EQUALS METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isLessOrEqual(byte x, byte y) {
        return equals(x, y) || x < y;
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(short x, short y) {
        return equals(x, y) || x < y;
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(int x, int y) {
        return equals(x, y) || x < y;
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(long x, long y) {
        return equals(x, y) || x < y;
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(float x, float y) {
        return equals(x, y) || x < y;
    }

    @Contract(pure = true)
    public static boolean isLessOrEqual(double x, double y) {
        return equals(x, y) || x < y;
    }

    /* =================================== REQUIRED NON NEGATIVE METHOD'S ======================================== */

    @Contract(value = "_ -> param1", pure = true)
    public static byte requiredNonNegative(byte value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static byte requiredNonNegative(byte value, byte fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        if (isNegative(fallback)) return 0;
        return nonNegative(value);
    }

    @Contract(value = "_, _ -> param1", pure = true)
    public static byte requiredNonNegative(byte value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegative(value);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static short requiredNonNegative(short value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static short requiredNonNegative(short value, short fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static short requiredNonNegative(short value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () ->
                new IllegalArgumentException(
                        supplierMessage == null ? null : supplierMessage.get()
                ));
        return nonNegative(value);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static int requiredNonNegative(int value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static int requiredNonNegative(int value, int fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static int requiredNonNegative(int value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () ->
                new IllegalArgumentException(
                        supplierMessage == null ? null : supplierMessage.get()
                ));
        return nonNegative(value);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static long requiredNonNegative(long value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static long requiredNonNegative(long value, long fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static long requiredNonNegative(long value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegative(value);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static float requiredNonNegative(float value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static float requiredNonNegative(float value, float fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static float requiredNonNegative(float value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegative(value);
    }

    @Contract("_ -> param1")
    public static double requiredNonNegative(double value) {
        ifTrueThrow(isNegative(value), Primitives::getValueCannotBeNegative);
        return nonNegative(value);
    }

    @Contract(pure = true)
    public static double requiredNonNegative(double value, double fallback) {
        if (isNegative(value)) {
            SLogger.warn("Value must be non-negative");
            return nonNegative(fallback);
        }
        return nonNegative(value);
    }

    @Contract("_, _ -> param1")
    public static double requiredNonNegative(double value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNegative(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegative(value);
    }

    /* =================================== REQUIRED POSITIVE METHOD'S ======================================== */

    @Contract("_ -> param1")
    public static byte requiredPositive(byte value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static byte requiredPositive(byte value, byte fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static byte requiredPositive(byte value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    @Contract("_ -> param1")
    public static short requiredPositive(short value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static short requiredPositive(short value, short fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static short requiredPositive(short value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    @Contract("_ -> param1")
    public static int requiredPositive(int value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static int requiredPositive(int value, int fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static int requiredPositive(int value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    @Contract("_ -> param1")
    public static long requiredPositive(long value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static long requiredPositive(long value, long fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static long requiredPositive(long value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    @Contract("_ -> param1")
    public static float requiredPositive(float value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static float requiredPositive(float value, float fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static float requiredPositive(float value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    @Contract("_ -> param1")
    public static double requiredPositive(double value) {
        ifTrueThrow(isNonPositive(value), Primitives::getValueMustBePositive);
        return nonNegativeNonZero(value);
    }

    public static double requiredPositive(double value, double fallback) {
        if (isNonPositive(value)) {
            SLogger.warn("Value must be positive");
            return nonNegativeNonZero(fallback);
        }
        return nonNegativeNonZero(value);
    }

    @Contract("_, _ -> param1")
    public static double requiredPositive(double value, Supplier<String> supplierMessage) {
        ifTrueThrow(isNonPositive(value), () -> new IllegalArgumentException(
                supplierMessage == null ? null : supplierMessage.get()
        ));
        return nonNegativeNonZero(value);
    }

    /* =================================== isEven AND isOdd METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isEven(byte value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(byte value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isEven(short value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(short value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isEven(int value) {
        return (value & 1) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(int value) {
        return (value & 1) != 0;
    }

    @Contract(pure = true)
    public static boolean isEven(long value) {
        return (value & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(long value) {
        return (value & 1L) != 0;
    }

    @Contract(pure = true)
    public static boolean isEven(float value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(float value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    @Contract(pure = true)
    public static boolean isEven(double value) {
        return isIntegral(value) && (((long) value) & 1L) == 0;
    }

    @Contract(pure = true)
    public static boolean isOdd(double value) {
        return isIntegral(value) && (((long) value) & 1L) != 0;
    }

    /* =================================== ASSISTANT METHOD'S ======================================== */

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    private static @NotNull IllegalArgumentException getValueCannotBeNegative() {
        return new IllegalArgumentException("Value must be non-negative");
    }

    @ApiStatus.Internal
    @Contract(value = " -> new", pure = true)
    private static @NotNull IllegalArgumentException getValueMustBePositive() {
        return new IllegalArgumentException("Value must be positive");
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private static boolean isIntegral(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value) && value % 1 == 0;
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    private static boolean isIntegral(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && value % 1 == 0;
    }

    /* =================================== NON NEGATIVE METHOD'S ======================================== */

    @Contract(pure = true)
    public static byte nonNegative(byte value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static short nonNegative(short value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static int nonNegative(int value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static long nonNegative(long value) {
        return isNegative(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static float nonNegative(float value) {
        return isNegative(value) ? 0.0f : value;
    }

    @Contract(pure = true)
    public static double nonNegative(double value) {
        return isNegative(value) ? 0.0 : value;
    }

    /* =================================== NON NEGATIVE NON ZERO METHOD'S ======================================== */

    @Contract(pure = true)
    public static byte nonNegativeNonZero(byte value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static short nonNegativeNonZero(short value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static int nonNegativeNonZero(int value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static long nonNegativeNonZero(long value) {
        return isNonPositive(value) ? 0 : value;
    }

    @Contract(pure = true)
    public static float nonNegativeNonZero(float value) {
        return isNonPositive(value) ? 0.0f : value;
    }

    @Contract(pure = true)
    public static double nonNegativeNonZero(double value) {
        return isNonPositive(value) ? 0.0 : value;
    }

    /* =================================== IS NON POSITIVE METHOD'S ======================================== */

    @Contract(pure = true)
    public static boolean isNonPositive(byte value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isNonPositive(short value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isNonPositive(int value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isNonPositive(long value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isNonPositive(float value) {
        return isNegative(value) || isZero(value);
    }

    @Contract(pure = true)
    public static boolean isNonPositive(double value) {
        return isNegative(value) || isZero(value);
    }
}
