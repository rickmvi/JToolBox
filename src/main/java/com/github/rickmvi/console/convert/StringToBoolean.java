package com.github.rickmvi.console.convert;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class StringToBoolean {

    public boolean toBoolean(@Nullable String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean toBoolean(@Nullable String value, boolean fallback) {
        try {
            return value != null ? Boolean.parseBoolean(value) : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public boolean toBoolean(@Nullable String value, @NotNull Supplier<Boolean> fallback) {
        try {
            return value != null ? Boolean.parseBoolean(value) : fallback.get();
        } catch (Exception e) {
            return fallback.get();
        }
    }

    public Optional<Boolean> toBooleanOptional(@Nullable String value) {
        try {
            return value != null ? Optional.of(Boolean.parseBoolean(value)) : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
