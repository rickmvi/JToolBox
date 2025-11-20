package com.github.rickmvi.jtoolbox.util;

import com.github.rickmvi.jtoolbox.control.If;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.DoubleStream;

import static com.github.rickmvi.jtoolbox.text.Stringifier.format;

/**
 * Utility class providing a variety of operations for primitive arrays.
 * This includes checking array emptiness, adding or removing elements,
 * concatenation, mapping, filtering, searching, reversing, and joining arrays.
 */
@UtilityClass
@SuppressWarnings("unused")
public class ArrayUtils {

    /* ==================================== HELPERS ========================================= */

    public static boolean isEmpty(byte[] array) { return array == null || array.length == 0; }
    public static boolean isEmpty(short[] array) { return array == null || array.length == 0; }
    public static boolean isEmpty(int[] array) { return array == null || array.length == 0; }
    public static boolean isEmpty(long[] array) { return array == null || array.length == 0; }
    public static boolean isEmpty(float[] array) { return array == null || array.length == 0; }
    public static boolean isEmpty(double[] array) { return array == null || array.length == 0; }

    public static int size(byte[] array) { return array == null ? 0 : array.length; }
    public static int size(short[] array) { return array == null ? 0 : array.length; }
    public static int size(int[] array) { return array == null ? 0 : array.length; }
    public static int size(long[] array) { return array == null ? 0 : array.length; }
    public static int size(float[] array) { return array == null ? 0 : array.length; }
    public static int size(double[] array) { return array == null ? 0 : array.length; }

    /* ==================================== ADD METHODS ========================================= */

    public static byte @NotNull [] add(byte @NotNull [] array, int index, byte element) {
        assertIndexInBounds(index, array.length);

        byte[] result = new byte[array.length +1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    public static short @NotNull [] add(short @NotNull [] array, int index, short element) {
        assertIndexInBounds(index, array.length);

        short[] result = new short[array.length +1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    /**
     * Creates a new array by adding an element at a specific index.
     * @param array The source array.
     * @param index The insertion index.
     * @param element The element to insert.
     * @return A new array with the element inserted.
     */
    public static int @NotNull [] add(int @NotNull [] array, int index, int element) {
        assertIndexInBounds(index, array.length);

        int[] result = new int[array.length + 1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    /**
     * Ensures that the provided index is within valid bounds.
     *
     * @param index  The index to validate.
     * @param length The array length used for index validation.
     * @throws IndexOutOfBoundsException If the index is negative or outside [0, length].
     */
    private static void assertIndexInBounds(int index, int length) {
        If.ThrowWhen(index < 0 || index > length,
                () -> new IndexOutOfBoundsException(format("Index: {}, Length: {}", index, length)));
    }

    private static <T> void copyAndInsert(T array, int index, Object element, Object resultArray) {
        int length = java.lang.reflect.Array.getLength(array);
        System.arraycopy(array, 0, resultArray, 0, index);
        java.lang.reflect.Array.set(resultArray, index, element);
        System.arraycopy(array, index, resultArray, index + 1, length - index);
    }

    public static long @NotNull [] add(long @NotNull [] array, int index, long element) {
        assertIndexInBounds(index, array.length);

        long[] result = new long[array.length +1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    public static float @NotNull [] add(float @NotNull [] array, int index, float element) {
        assertIndexInBounds(index, array.length);

        float[] result = new float[array.length +1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    public static double @NotNull [] add(double @NotNull [] array, int index, double element) {
        assertIndexInBounds(index, array.length);

        double[] result = new double[array.length +1];
        System.arraycopy(array, 0, result, 0, index);
        result[index] = element;
        copyAndInsert(array, index, element, result);
        return result;
    }

    public static byte @NotNull [] add(byte @NotNull [] array, byte element) {
        return add(array, array.length, element);
    }

    public static short @NotNull [] add(short @NotNull [] array, short element) {
        return add(array, array.length, element);
    }

    public static int @NotNull [] add(int @NotNull [] array, int element) {
        return add(array, array.length, element);
    }

    public static long @NotNull [] add(long @NotNull [] array, long element) {
        return add(array, array.length, element);
    }

    public static double @NotNull [] add(double @NotNull [] array, double element) {
        return add(array, array.length, element);
    }

    /* ==================================== REMOVE METHODS ========================================= */

    public static byte @NotNull [] remove(byte @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        byte[] result = new byte[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static short @NotNull [] remove(short @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        short[] result = new short[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    /**
     * Creates a new array by removing the element at a specific index.
     * @param array The source array.
     * @param index The index of the element to remove.
     * @return A new array with the element removed.
     */
    public static int @NotNull [] remove(int @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        int[] result = new int[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static long @NotNull [] remove(long @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        long[] result = new long[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static float @NotNull [] remove(float @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        float[] result = new float[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    public static double @NotNull [] remove(double @NotNull [] array, int index) {
        assertIndexInBounds(index, array.length);

        double[] result = new double[array.length -1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, array.length - index - 1);
        return result;
    }

    /* ==================================== CONCAT METHOD ========================================= */

    public static byte @NotNull [] concat(byte @NotNull [] first, byte @NotNull [] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static short @NotNull [] concat(short @NotNull [] first, short @NotNull [] second) {
        short[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int @NotNull [] concat(int @NotNull [] first, int @NotNull [] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    public static long @NotNull [] concat(long @NotNull [] first, long @NotNull [] second) {
        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static float @NotNull [] concat(float @NotNull [] first, float @NotNull [] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static double @NotNull [] concat(double @NotNull [] first, double @NotNull [] second) {
        double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /* =================================== MAP METHODS ======================================== */

    public static byte @NotNull [] map(byte @NotNull [] array, @NotNull ByteUnaryOperator mapper) {
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.applyAsByte(array[i]);
        }
        return result;
    }

    public interface ByteUnaryOperator {
        byte applyAsByte(byte operand);
    }

    public static short @NotNull [] map(short @NotNull [] array, @NotNull ShortUnaryOperator mapper) {
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.applyAsShort(array[i]);
        }
        return result;
    }

    public interface ShortUnaryOperator {
        short applyAsShort(short operand);
    }

    public static float @NotNull [] map(float @NotNull [] array, @NotNull FloatUnaryOperator mapper) {
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.applyAsFloat(array[i]);
        }
        return result;
    }

    public interface FloatUnaryOperator {
        float applyAsFloat(float operand);
    }

    public static int @NotNull [] map(int @NotNull [] array, @NotNull IntUnaryOperator mapper) {
        return IntStream.of(array).map(mapper).toArray();
    }

    public static double @NotNull [] mapToDouble(int @NotNull [] array, @NotNull IntToDoubleFunction mapper) {
        return IntStream.of(array).mapToDouble(mapper).toArray();
    }

    public static long @NotNull [] map(long @NotNull [] array, @NotNull LongUnaryOperator mapper) {
        return LongStream.of(array).map(mapper).toArray();
    }

    public static double @NotNull [] map(double @NotNull [] array, @NotNull DoubleUnaryOperator mapper) {
        return DoubleStream.of(array).map(mapper).toArray();
    }

    /* =================================== FILTER METHODS ======================================= */

    public static byte @NotNull [] filter(byte @NotNull [] array, @NotNull BytePredicate predicate) {
        byte[] temp = new byte[array.length];
        int count = 0;
        for (byte element : array) {
            if (predicate.test(element)) {
                temp[count++] = element;
            }
        }
        return Arrays.copyOf(temp, count);
    }

    public interface BytePredicate {
        boolean test(byte value);
    }

    public static short @NotNull [] filter(short @NotNull [] array, @NotNull ShortPredicate predicate) {
        short[] temp = new short[array.length];
        int count = 0;
        for (short element : array) {
            if (predicate.test(element)) {
                temp[count++] = element;
            }
        }
        return Arrays.copyOf(temp, count);
    }

    public interface ShortPredicate {
        boolean test(short value);
    }

    public static float @NotNull [] filter(float @NotNull [] array, @NotNull FloatPredicate predicate) {
        float[] temp = new float[array.length];
        int count = 0;
        for (float element : array) {
            if (predicate.test(element)) {
                temp[count++] = element;
            }
        }
        return Arrays.copyOf(temp, count);
    }

    public interface FloatPredicate {
        boolean test(float value);
    }

    public static int @NotNull [] filter(int @NotNull [] array, @NotNull IntPredicate predicate) {
        return IntStream.of(array).filter(predicate).toArray();
    }

    public static long @NotNull [] filter(long @NotNull [] array, @NotNull LongPredicate predicate) {
        return LongStream.of(array).filter(predicate).toArray();
    }

    public static double @NotNull [] filter(double @NotNull [] array, @NotNull DoublePredicate predicate) {
        return DoubleStream.of(array).filter(predicate).toArray();
    }

    /* =================================== INDEX / CONTAINS METHODS ======================================= */

    public static int indexOf(byte @NotNull [] array, byte element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(byte @NotNull [] array, byte element) {
        return indexOf(array, element) != -1;
    }

    public static int indexOf(short @NotNull [] array, short element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(short @NotNull [] array, short element) {
        return indexOf(array, element) != -1;
    }

    public static int indexOf(int @NotNull [] array, int element) {
        return IntStream.range(0, array.length)
                .filter(i -> array[i] == element)
                .findFirst()
                .orElse(-1);
    }

    public static boolean contains(int @NotNull [] array, int element) {
        return indexOf(array, element) != -1;
    }

    public static int indexOf(long @NotNull [] array, long element) {
        return (int) LongStream.range(0, array.length)
                .filter(i -> array[(int)i] == element)
                .findFirst()
                .orElse(-1);
    }

    public static boolean contains(long @NotNull [] array, long element) {
        return indexOf(array, element) != -1;
    }

    public static int indexOf(float @NotNull [] array, float element) {
        for (int i = 0; i < array.length; i++) {
            if (Float.compare(array[i], element) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(float @NotNull [] array, float element) {
        return indexOf(array, element) != -1;
    }

    public static int indexOf(double @NotNull [] array, double element) {
        return IntStream.range(0, array.length)
                .filter(i -> array[i] == element)
                .findFirst()
                .orElse(-1);
    }

    public static boolean contains(double @NotNull [] array, double element) {
        return indexOf(array, element) != -1;
    }

    /* ==================================== REVERSE METHODS ========================================= */

    public static byte @NotNull [] reversed(byte @NotNull [] array) {
        byte[] result = Arrays.copyOf(array, array.length);
        reverseArray(result);
        return result;
    }

    public static void reverseInPlace(byte @NotNull [] array) {
        reverseArray(array);
    }

    private static void reverseArray(byte @NotNull [] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public static short @NotNull [] reversed(short @NotNull [] array) {
        short[] result = Arrays.copyOf(array, array.length);
        reverseArray(result);
        return result;
    }

    public static void reverseInPlace(short @NotNull [] array) {
        reverseArray(array);
    }

    private static void reverseArray(short @NotNull [] array) {
        for (int i = 0; i < array.length / 2; i++) {
            short temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public static int @NotNull [] reversed(int @NotNull [] array) {
        int[] result = Arrays.copyOf(array, array.length);
        reverseArray(result);
        return result;
    }

    public static void reverseInPlace(int @NotNull [] array) {
        reverseArray(array);
    }

    private static void reverseArray(int @NotNull [] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public static long @NotNull [] reversed(long @NotNull [] array) {
        long[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < result.length / 2; i++) {
            long temp = result[i];
            result[i] = result[result.length - 1 - i];
            result[result.length - 1 - i] = temp;
        }
        return result;
    }

    public static float @NotNull [] reversed(float @NotNull [] array) {
        float[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < result.length / 2; i++) {
            float temp = result[i];
            result[i] = result[result.length - 1 - i];
            result[result.length - 1 - i] = temp;
        }
        return result;
    }

    public static double @NotNull [] reversed(double @NotNull [] array) {
        double[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < result.length / 2; i++) {
            double temp = result[i];
            result[i] = result[result.length - 1 - i];
            result[result.length - 1 - i] = temp;
        }
        return result;
    }

    /* ==================================== JOIN METHODS ========================================= */

    public static @NotNull String join(byte @NotNull [] array, @NotNull String delimiter) {
        return joinPrimitiveArray(array, delimiter);
    }

    public static @NotNull String join(short @NotNull [] array, @NotNull String delimiter) {
        return joinPrimitiveArray(array, delimiter);
    }

    public static @NotNull String join(float @NotNull [] array, @NotNull String delimiter) {
        return joinPrimitiveArray(array, delimiter);
    }

    public static @NotNull String join(int @NotNull [] array, @NotNull String delimiter) {
        PrimitiveIterator.OfInt iterator = IntStream.of(array).iterator();
        return joinIterator(iterator, delimiter);
    }

    public static @NotNull String join(long @NotNull [] array, @NotNull String delimiter) {
        PrimitiveIterator.OfLong iterator = LongStream.of(array).iterator();
        return joinIterator(iterator, delimiter);
    }

    public static @NotNull String join(double @NotNull [] array, @NotNull String delimiter) {
        PrimitiveIterator.OfDouble iterator = DoubleStream.of(array).iterator();
        return joinIterator(iterator, delimiter);
    }

    private static @NotNull String joinPrimitiveArray(Object @NotNull []unused, @NotNull String delimiter) {
        throw new UnsupportedOperationException("This overload is not intended for reference arrays");
    }

    private static @NotNull String joinPrimitiveArray(byte @NotNull [] array, @NotNull String delimiter) {
        if (array.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(delimiter).append(array[i]);
        }
        return builder.toString();
    }

    private static @NotNull String joinPrimitiveArray(short @NotNull [] array, @NotNull String delimiter) {
        if (array.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(delimiter).append(array[i]);
        }
        return builder.toString();
    }

    private static @NotNull String joinPrimitiveArray(float @NotNull [] array, @NotNull String delimiter) {
        if (array.length == 0) return "";
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(delimiter).append(array[i]);
        }
        return builder.toString();
    }

    private static @NotNull String joinIterator(PrimitiveIterator.@NotNull OfInt iterator, @NotNull String delimiter) {
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(iterator.nextInt());
        while (iterator.hasNext()) {
            builder.append(delimiter).append(iterator.nextInt());
        }
        return builder.toString();
    }

    private static @NotNull String joinIterator(PrimitiveIterator.@NotNull OfLong iterator, @NotNull String delimiter) {
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(iterator.nextLong());
        while (iterator.hasNext()) {
            builder.append(delimiter).append(iterator.nextLong());
        }
        return builder.toString();
    }

    private static @NotNull String joinIterator(PrimitiveIterator.@NotNull OfDouble iterator, @NotNull String delimiter) {
        if (!iterator.hasNext()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(iterator.nextDouble());
        while (iterator.hasNext()) {
            builder.append(delimiter).append(iterator.nextDouble());
        }
        return builder.toString();
    }

}
