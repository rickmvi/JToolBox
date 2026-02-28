package com.github.rickmvi.jtoolbox.config.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class TypeConverter {

    private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>();

    static {
        registerDefaultConverters();
    }

    private static void registerDefaultConverters() {
        register(String.class, value -> value);
        register(Integer.class, Integer::valueOf);
        register(int.class, Integer::valueOf);
        register(Long.class, Long::valueOf);
        register(long.class, Long::valueOf);
        register(Double.class, Double::valueOf);
        register(double.class, Double::valueOf);
        register(Float.class, Float::valueOf);
        register(float.class, Float::valueOf);
        register(Boolean.class, Boolean::parseBoolean);
        register(boolean.class, Boolean::parseBoolean);
        register(Duration.class, Duration::parse);
        register(Path.class, Paths::get);
        register(File.class, File::new);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, Class<T> type) {
        if (value == null) {
            return null;
        }

        if (CONVERTERS.containsKey(type)) {
            return (T) CONVERTERS.get(type).convert(value);
        }

        if (type.isEnum()) {
            return convertEnum(value, (Class<? extends Enum>) type);
        }

        if (List.class.isAssignableFrom(type)) {
            return (T) convertToList(value, String.class);
        }

        if (Set.class.isAssignableFrom(type)) {
            return (T) convertToSet(value, String.class);
        }

        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> T convertEnum(String value, Class<? extends Enum> enumType) {
        return (T) Enum.valueOf(enumType, value.toUpperCase());
    }

    private static <T> List<T> convertToList(String value, Class<T> elementType) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(v -> convert(v, elementType))
                .collect(Collectors.toList());
    }

    private static <T> Set<T> convertToSet(String value, Class<T> elementType) {
        return new HashSet<>(convertToList(value, elementType));
    }

    public static <T> List<T> convertToList(String value, Class<T> elementType, String delimiter) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(value.split(delimiter))
                .map(String::trim)
                .map(v -> convert(v, elementType))
                .collect(Collectors.toList());
    }

    public static <T> void register(Class<T> type, Converter<T> converter) {
        CONVERTERS.put(type, converter);
    }

    public static boolean isSupported(Class<?> type) {
        return CONVERTERS.containsKey(type)
                || type.isEnum()
                || List.class.isAssignableFrom(type)
                || Set.class.isAssignableFrom(type);
    }

    @FunctionalInterface
    public interface Converter<T> {
        T convert(String value);
    }
}