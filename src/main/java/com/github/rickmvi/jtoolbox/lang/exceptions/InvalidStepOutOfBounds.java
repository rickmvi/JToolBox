package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.lang.message.ErrorMessage;

public class InvalidStepOutOfBounds extends RuntimeException {
    public InvalidStepOutOfBounds() {
        super(ErrorMessage.STEP_OUT_OF_BOUNDS.getMessage());
    }

    public InvalidStepOutOfBounds(String message) {
        super(message);
    }

    public InvalidStepOutOfBounds(Throwable cause) {
        super(cause);
    }

    public InvalidStepOutOfBounds(String message, Throwable cause) {
        super(message, cause);
    }
}
