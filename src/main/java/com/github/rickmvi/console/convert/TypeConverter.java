package com.github.rickmvi.console.convert;

import com.github.rickmvi.template.ConversionScope;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@lombok.experimental.UtilityClass
public final class TypeConverter {

    public enum PrimitiveType {
        INT, LONG, DOUBLE, FLOAT, BOOLEAN, STRING
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> convertTo(@Nullable String value, @NotNull Class<T> type) {
        return ConversionScope.convertSafely(value, v -> {
            if (type == Integer.class)  return (T) Integer.valueOf(v);
            if (type == Long.class)     return (T) Long.valueOf(v);
            if (type == Double.class)   return (T) Double.valueOf(v);
            if (type == Float.class)    return (T) Float.valueOf(v);
            if (type == Boolean.class)  return (T) Boolean.valueOf(v);
            if (type == String.class)   return (T) v;
            throw new IllegalArgumentException("Type not supported" + type);
        });
    }

    public Optional<?> convertTo(@Nullable String value, @NotNull PrimitiveType type) {
        return ConversionScope.convertSafely(value, v -> switch (type) {
           case INT ->     Integer.valueOf(v);
           case LONG ->    Long.valueOf(v);
           case DOUBLE ->  Double.valueOf(v);
           case FLOAT ->   Float.valueOf(v);
           case BOOLEAN -> Boolean.valueOf(v);
           case STRING ->  v;
        });
    }

    @Contract("null, _, _ -> null")
    public Object convert(@Nullable String value, @NotNull PrimitiveType type, @Nullable Object fallback) {
        return ConversionScope.convertSafely(value, s -> switch (type) {
           case INT ->     StringToNumber.toInt      (s, (Integer) fallback);
           case LONG ->    StringToNumber.toLong     (s, (Long)    fallback);
           case DOUBLE ->  StringToNumber.toDouble   (s, (Double)  fallback);
           case FLOAT ->   StringToNumber.toFloat    (s, (Float)   fallback);
           case BOOLEAN -> StringToBoolean.toBoolean (s, (Boolean) fallback);
           case STRING ->  s != null ? s : fallback;
        });
    }
}
