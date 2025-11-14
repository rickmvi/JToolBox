package com.github.rickmvi.jtoolbox.util;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class ArrayUtils {

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static int length(byte[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(short[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(int[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(long[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(float[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(double[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(char[] array) {
        return array == null ? 0 : array.length;
    }

    public static int length(boolean[] array) {
        return array == null ? 0 : array.length;
    }

}
