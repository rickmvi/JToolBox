package com.github.rickmvi.jtoolbox.lang.message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public enum ErrorMessage {
    INVALID_INDEX("Start index cannot be negative."),
    INVALID_START_INDEX("Invalid start index: %d. Index cannot be negative."),
    INVALID_END_INDEX("Invalid end index: %d. Index cannot exceed array length."),
    END_MENOR_THAN_START("End index %d cannot be less than start index %d."),
    NULL_VALUE("Value cannot be null."),
    EMPTY_ARRAY("Array cannot be empty.");

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC)
    private final String message;

    @Contract(pure = true)
    public @NotNull String format(Object... args) {
        return String.format(message, args);
    }
}