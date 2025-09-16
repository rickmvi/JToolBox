package com.github.rickmvi.jtoolbox.lang.exceptions;

import com.github.rickmvi.jtoolbox.text.Formatted;

public class IndexNumberException extends RuntimeException {
    public IndexNumberException(String message) {
        super(message);
    }

    public IndexNumberException(Throwable cause) {
        super(cause);
    }

    public IndexNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexNumberException(int index, int size) {
        super(Formatted.format("Index {} out of bounds for length {}", index, size));
    }
}
