package com.github.rickmvi.jtoolbox.dotenv.exceptions;

public class DotenvException extends RuntimeException {
    public DotenvException(String message) {
        super(message);
    }

    public DotenvException(String message, Throwable cause) {
        super(message, cause);
    }

    public DotenvException(Throwable cause) {
        super(cause);
    }
}
