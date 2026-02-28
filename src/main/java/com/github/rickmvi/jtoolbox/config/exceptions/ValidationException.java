package com.github.rickmvi.jtoolbox.config.exceptions;

public class ValidationException extends ConfigException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}