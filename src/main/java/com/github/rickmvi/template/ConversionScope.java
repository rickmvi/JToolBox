package com.github.rickmvi.template;

import java.util.Optional;
import java.util.function.Function;

@lombok.experimental.UtilityClass
@SuppressWarnings("unused")
public class ConversionScope {

    public <T, R>Optional<R> convertSafely(T value, Function<T, R> converter) {
        if (value == null) return Optional.empty();
        try {
            return Optional.ofNullable(converter.apply(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
