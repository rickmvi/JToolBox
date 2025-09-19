package com.github.rickmvi.jtoolbox.lang.exceptions;

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
        super(ErrorMessage.INDEX_OUT_OF_BOUNDS.format(index, size));
    }
}
