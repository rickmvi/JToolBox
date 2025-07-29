package com.github.rickmvi.console.convert;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class StringToNumber {

    @Contract("null -> 0")
    public int toInt(@Nullable String value) {
        return toInt(value, 0);
    }

    @Contract("null -> 0L")
    public long toLong(@Nullable String value) {
        return toLong(value, 0L);
    }

    @Contract("null -> 0.0")
    public double toDouble(@Nullable String value) {
        return toDouble(value, 0.0);
    }

    @Contract("null -> 0.0f")
    public float toFloat(@Nullable String value) {
        return toFloat(value, 0.0f);
    }

    public int toInt(@Nullable String value, int fallback) {
        try {
            return value != null ? Integer.parseInt(value) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public long toLong(@Nullable String value, long fallback) {
        try {
            return value != null ? Long.parseLong(value) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public double toDouble(@Nullable String value, double fallback) {
        try {
            return value != null ? Double.parseDouble(value) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public float toFloat(@Nullable String value, float fallback) {
        try {
            return value != null ? Float.parseFloat(value) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public int toInt(@Nullable String value, @NotNull Supplier<Integer> fallback) {
        try {
            return value != null ? Integer.parseInt(value) : fallback.get();
        } catch (NumberFormatException e) {
            return fallback.get();
        }
    }

    public long toLong(@Nullable String value, @NotNull Supplier<Long> fallback) {
        try {
            return value != null ? Long.parseLong(value) : fallback.get();
        } catch (NumberFormatException e) {
            return fallback.get();
        }
    }

    public double toDouble(@Nullable String value, @NotNull Supplier<Double> fallback) {
        try {
            return value != null ? Double.parseDouble(value) : fallback.get();
        } catch (NumberFormatException e) {
            return fallback.get();
        }
    }

    public float toFloat(@Nullable String value, @NotNull Supplier<Float> fallback) {
        try {
            return value != null ? Float.parseFloat(value) : fallback.get();
        } catch (NumberFormatException e) {
            return fallback.get();
        }
    }

    public Optional<Integer> toIntOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Integer.parseInt(value)) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Long> toLongOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Long.parseLong(value)) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Double> toDoubleOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Double.parseDouble(value)) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Float> toFloatOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Float.parseFloat(value)) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
