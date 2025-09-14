package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.lang.message.ErrorMessage;

public class InvalidStartIndexException extends RuntimeException {

    public InvalidStartIndexException() {
        super(ErrorMessage.INVALID_INDEX.getMessage());
    }

    public InvalidStartIndexException(String message) {
        super(message);
    }

    public InvalidStartIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStartIndexException(Throwable cause) {
        super(cause);
    }

    public InvalidStartIndexException(int index) {
        super(ErrorMessage.INVALID_START_INDEX.format(index));
    }

}
