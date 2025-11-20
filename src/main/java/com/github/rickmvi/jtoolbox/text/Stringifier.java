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
package com.github.rickmvi.jtoolbox.text;

import com.github.rickmvi.jtoolbox.control.Switch;
import com.github.rickmvi.jtoolbox.util.Numbers;
import com.github.rickmvi.jtoolbox.util.Try;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for converting various Java objects into their string representations.
 * <p>
 * This class provides methods to safely convert objects of different types—including
 * primitives, arrays, collections, maps, enums, and exceptions—into non-null
 * strings. It ensures that null values are handled gracefully and provides default
 * representations when needed.
 * </p>
 *
 * <h2>Core Methods:</h2>
 * <ul>
 *     <li>{@link #valueOf(Object)} – Converts an object to string, returning "null" for null or empty values.</li>
 *     <li>{@link #toString(Object)} – Converts an object to string, providing a default "null" for null objects.</li>
 *     <li>{@link #toString(Object, String)} – Converts an object to string, using a custom default string if the object is null.</li>
 *     <li>{@link #valueOf(Object, String)} – Returns the object itself or its string representation, falling back to a default value.</li>
 * </ul>
 *
 * <h2>Supported Types:</h2>
 * <ul>
 *     <li>String, Number, Boolean, Character</li>
 *     <li>Enums (returns the enum name)</li>
 *     <li>Exceptions (returns {@code Throwable.toString()})</li>
 *     <li>Collections (converted to a comma-separated list within square brackets)</li>
 *     <li>Maps (converted to key=value pairs within curly braces)</li>
 *     <li>Arrays of any primitive or object type</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 * <pre>{@code
 * String s1 = Stringifier.valueOf(null); // "null"
 * String s2 = Stringifier.toString(123); // "123"
 * String s3 = Stringifier.toString(new int[]{1,2,3}); // "[1, 2, 3]"
 * String s4 = Stringifier.toString(Map.of("a", 1, "b", 2)); // "{a=1, b=2}"
 * }</pre>
 *
 * <p>
 * This class is a {@link UtilityClass}, so all methods are static,
 * and it cannot be instantiated.
 * </p>
 *
 * @author Rick M. Viana
 * @since 1.3
 */
@UtilityClass
public class Stringifier {

    @Contract(value = "null -> !null", pure = true)
    public static String valueOf(@Nullable Object o) {
        return o == null || o.toString().isEmpty() ? "null" : Stringifier.valueOf(o, "null");
    }

    public static Character charAtZero(Object o) {
        return Stringifier.charAt(o, 0);
    }

    @Contract("null -> !null")
    public static String toString(@Nullable Object o) {
        return Stringifier.toString(o, "null");
    }

    @Contract("null,_-> param2")
    public static String toString(@Nullable Object o, @NotNull String defaultValue) {
        if (o == null) return defaultValue;

        return Try.of(() -> {
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
                    return joinCollection(objects);
                }
                case Map<?, ?> map -> {
                    return serializeMap(map);
                }
                default -> {
                }
            }
            if (o.getClass().isArray()) return toArrayString(o);

            String result = o.toString();
            if (result == null || result.trim().isEmpty())
                return o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode());
            return result;
        }).recover(e -> "Error@toString(" + e.getClass().getSimpleName() + ")").orElse(defaultValue);
    }

    public static String valueOf(Object o, String defaultValue) {
        return Switch.<Object, String>on(o)
                .caseNull(() -> defaultValue)
                .caseCondition(v ->
                        v instanceof Numbers ||
                        v instanceof Boolean ||
                        v instanceof Character,
                        Object::toString
                )
                .caseDefault(Object::toString)
                .recover(e -> "Error@toString(" + e.getClass().getSimpleName() + ")")
                .get();
    }

    private static String toArrayString(Object array) {
        return Switch.<Object, String>on(array)
                .caseType(Object[].class,  Arrays::toString)
                .caseType(int[].class,     Arrays::toString)
                .caseType(long[].class,    Arrays::toString)
                .caseType(double[].class,  Arrays::toString)
                .caseType(float[].class,   Arrays::toString)
                .caseType(short[].class,   Arrays::toString)
                .caseType(byte[].class,    Arrays::toString)
                .caseType(char[].class,    Arrays::toString)
                .caseType(boolean[].class, Arrays::toString)
                .caseDefault(arr -> "UnknownArray@" + arr.getClass().getComponentType())
                .get();
    }

    private static @NotNull String joinCollection(@NotNull Collection<?> col) {
        return "[" + col.stream()
                .map(Stringifier::toString)
                .reduce((a, b) -> a + ", " + b).orElse("") + "]";
    }

    private static @NotNull String serializeMap(@NotNull Map<?, ?> map) {
        return "{" + map.entrySet().stream()
                .map(e -> toString(e.getKey()) + "=" + toString(e.getValue()))
                .reduce((a, b) -> a + ", " + b).orElse("") + "}";
    }

    public static Character charAt(Object o, int index) {
        Objects.requireNonNull(o, "Cannot get char at index " + index + " from null");
        if (Numbers.isNegative(index)) throw new IllegalArgumentException("Index cannot be negative: " + index);
        return Switch.<Object, Character>on(o).
                caseType(String.class, s -> s.charAt(index)).
                caseType(char[].class, chars -> chars[index]).
                caseType(CharSequence.class, charSequence -> charSequence.charAt(index)).
                caseDefault(v -> {
                    throw new IllegalArgumentException("Cannot get char at index " + index + " from " + v);
                })
                .get();
    }

    public static int length(Object o) {
        Objects.requireNonNull(o, "Cannot get length from null");
        return Switch.<Object, Integer>on(o).
                caseType(String.class, String::length).
                caseType(char[].class, chars -> chars.length).
                caseType(CharSequence.class, CharSequence::length).
                caseDefault(v -> {
                    throw new IllegalArgumentException("Cannot get length from " + v);
                })
                .get();
    }

    public static String trim(Object o) {
        Objects.requireNonNull(o, "Cannot trim null");
        return Switch.<Object, String>on(o).
                caseType(String.class, String::trim).
                caseType(char[].class, chars -> new String(chars).trim()).
                caseType(CharSequence.class, charSequence -> charSequence.toString().trim()).
                caseDefault(v -> {
                    throw new IllegalArgumentException("Cannot trim " + v);
                })
                .get();
    }

    public static String name(Object var1) {
        Objects.requireNonNull(var1, "Object %s is Null".formatted(var1));
        return Stringifier.valueOf(Stringifier.toString(var1));
    }

    public static @NotNull String format(String format, Object... args) {
        return StringFormatter.format(format, args);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
