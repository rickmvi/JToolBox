package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.lang.message.ErrorMessage;
import org.jetbrains.annotations.NotNull;

public class InvalidEndIndexException extends RuntimeException {

    public InvalidEndIndexException() {
        super(ErrorMessage.INVALID_END_INDEX.getMessage());
    }

    public InvalidEndIndexException(int startIndex, int endIndex) {
        super(ErrorMessage.END_MENOR_THAN_START.format(endIndex, startIndex));
    }

    public InvalidEndIndexException(int startIndex, int endIndex, @NotNull ErrorMessage errorMessage) {
        super(errorMessage.format(endIndex, startIndex));
    }

    public InvalidEndIndexException(String message) {
        super(message);
    }

    public InvalidEndIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEndIndexException(Throwable cause) {
        super(cause);
    }

    public InvalidEndIndexException(int index) {
        super(ErrorMessage.INVALID_END_INDEX.format(index));
    }
}
