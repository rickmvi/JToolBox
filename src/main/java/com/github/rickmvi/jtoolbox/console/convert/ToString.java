package com.github.rickmvi.jtoolbox.console.convert;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for safely converting objects to string representations.
 * Handles nulls, primitives, collections, arrays, enums, and fallbacks.
 */
@lombok.experimental.UtilityClass
public class ToString {

    /**
     * Converts the given object to a safe string representation.
     * Returns "null" if the object is null.
     */
    @Contract("null -> !null")
    public static @NotNull String toString(@Nullable Object o) {
        return toString(o, "null");
    }

    /**
     * Converts the given object to a safe string representation.
     * Returns the default value if the object is null.
     */
    @Contract("null,_-> param2")
    public static @NotNull String toString(@Nullable Object o, @NotNull String defaultValue) {
        if (o == null) return defaultValue;

        try {
            if (o instanceof String) return (String) o;
            if (o instanceof Number || o instanceof Boolean || o instanceof Character) return o.toString();
            switch (o) {
                case Enum<?> anEnum -> {
                    return anEnum.name();
                }
                case Throwable throwable -> {
                    return throwable.toString();
                }
                case Collection<?> objects -> {
                    return toCollectionString(objects);
                }
                case Map<?, ?> map -> {
                    return toMapString(map);
                }
                default -> {
                }
            }
            if (o.getClass().isArray()) return toArrayString(o);

            String result = o.toString();
            if (result == null || result.trim().isEmpty()) {
                return o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode());
            }
            return result;
        } catch (Exception e) {
            return "Error@toString(" + e.getClass().getSimpleName() + ")";
        }
    }

    @ApiStatus.Internal
    private static @NotNull String toArrayString(Object array) {
        if (array instanceof Object[]) return Arrays.toString((Object[]) array);
        if (array instanceof int[]) return Arrays.toString((int[]) array);
        if (array instanceof long[]) return Arrays.toString((long[]) array);
        if (array instanceof double[]) return Arrays.toString((double[]) array);
        if (array instanceof float[]) return Arrays.toString((float[]) array);
        if (array instanceof boolean[]) return Arrays.toString((boolean[]) array);
        if (array instanceof char[]) return Arrays.toString((char[]) array);
        if (array instanceof byte[]) return Arrays.toString((byte[]) array);
        if (array instanceof short[]) return Arrays.toString((short[]) array);
        return "UnknownArray@" + array.getClass().getComponentType();
    }

    @ApiStatus.Internal
    private static @NotNull String toCollectionString(@NotNull Collection<?> col) {
        return "[" + col.stream()
                .map(ToString::toString)
                .reduce((a, b) -> a + ", " + b).orElse("") + "]";
    }

    @ApiStatus.Internal
    private static @NotNull String toMapString(@NotNull Map<?, ?> map) {
        return "{" + map.entrySet().stream()
                .map(e -> toString(e.getKey()) + "=" + toString(e.getValue()))
                .reduce((a, b) -> a + ", " + b).orElse("") + "}";
    }
}
