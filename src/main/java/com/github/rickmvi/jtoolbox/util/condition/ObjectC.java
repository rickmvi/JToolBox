package com.github.rickmvi.jtoolbox.util.condition;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ObjectC {

    @Contract("null -> true")
    public static boolean isNullOrEmpty(@Nullable Object o) {
        return o == null || o.toString().isEmpty();
    }

    @Contract(value = "null -> true; !null -> false", pure = true)
    public static boolean isNull(@Nullable Object o) {
        return o == null;
    }

    @Contract(value = "null -> false; !null -> true", pure = true)
    public static boolean isNotNull(@Nullable Object o) {
        return !isNull(o);
    }

    public static boolean isEmpty(@Nullable Object o) {
        return isNullOrEmpty(o) || o.toString().trim().isEmpty();
    }

    public static boolean isNotEmpty(@Nullable Object o) {
        return !isEmpty(o);
    }

    @Contract(value = "null -> false; !null -> true", pure = true)
    public static boolean isTrue(@Nullable Boolean b) {
        return b != null && b;
    }

}
