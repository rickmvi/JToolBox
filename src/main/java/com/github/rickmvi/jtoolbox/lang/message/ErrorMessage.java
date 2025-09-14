package com.github.rickmvi.jtoolbox.lang.message;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@lombok.RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public enum ErrorMessage {
    INVALID_INDEX        ("Start index cannot be negative."),
    INVALID_START_INDEX  ("Invalid start index: %d. Index cannot be negative."),
    INVALID_END_INDEX    ("Invalid end index: %d. Index cannot exceed array length."),
    END_MENOR_THAN_START ("End index %d cannot be less than start index %d."),
    END_MENOR_THAN_ZERO  ("End index %d cannot be less than zero."),
    START_MENOR_THAN_ZERO("Start index %d cannot be less than zero."),
    INDEX_OUT_OF_BOUNDS  ("Index %d out of bounds for length %d."),
    INDEX_NEGATIVE       ("Index %d cannot be negative."),
    INDEX_GREATER_THAN   ("Index %d cannot be greater than %d."),
    INDEX_LESS_THAN      ("Index %d cannot be less than %d."),
    INDEX_EQUALS         ("Index %d cannot be equal to %d."),
    INDEX_NOT_FOUND      ("Index %d not found in array."),
    INDEX_NOT_UNIQUE     ("Index %d is not unique in array."),
    INDEX_NOT_UNIQUE_2   ("Index %d is not unique in array. Found %d duplicates."),
    START_BIGGER_THAN_END("Start index %d cannot be greater than end index %d."),
    NULL_VALUE           ("Value cannot be null."),
    EMPTY_ARRAY          ("Array cannot be empty."),
    STEP_OUT_OF_BOUNDS   ("Step out of bounds.");

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC)
    private final String message;

    @Contract(pure = true)
    public @NotNull String format(Object... args) {
        return String.format(message, args);
    }
}