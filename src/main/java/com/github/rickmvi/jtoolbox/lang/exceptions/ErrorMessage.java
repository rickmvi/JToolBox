package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.utils.constants.Constants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public enum ErrorMessage {

    INDEX_NEGATIVE      (Constants.INDEX_NEGATIVE),
    INDEX_OUT_OF_BOUNDS (Constants.INDEX_OUT_OF_BOUNDS),
    INDEX_LESS_THAN     (Constants.INDEX_LESS_THAN),
    INDEX_GREATER_THAN  (Constants.INDEX_GREATER_THAN),
    INDEX_EQUALS        (Constants.INDEX_EQUALS),
    INDEX_NOT_FOUND     (Constants.INDEX_NOT_FOUND),
    INDEX_NOT_UNIQUE    (Constants.INDEX_NOT_UNIQUE),
    INDEX_DUPLICATES    (Constants.INDEX_DUPLICATES),

    START_NEGATIVE      (Constants.START_NEGATIVE),
    END_NEGATIVE        (Constants.END_NEGATIVE),
    START_GREATER_THAN_END(Constants.START_GREATER_THAN_END),
    END_GREATER_THAN_LENGTH(Constants.END_GREATER_THAN_LENGTH),
    END_LESS_THAN_START (Constants.END_LESS_THAN_START),

    NULL_VALUE          (Constants.NULL_VALUE),
    EMPTY_ARRAY         (Constants.EMPTY_ARRAY),
    STEP_OUT_OF_BOUNDS  (Constants.STEP_OUT_OF_BOUNDS);

    @lombok.Getter(value = lombok.AccessLevel.PRIVATE, onMethod_ = @Contract(pure = true))
    private final String message;

    @Contract(pure = true)
    public String display() {
        return this.message;
    }

    public static @NotNull ErrorMessage of(String message) {
        for (ErrorMessage error : values()) {
            if (error.getMessage().equals(message)) return error;
        }
        throw new IllegalArgumentException("Invalid error message: " + message);
    }

    @Contract(pure = true)
    public @NotNull String format(Object... args) {
        return String.format(this.message, args);
    }
}