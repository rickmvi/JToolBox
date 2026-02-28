package com.github.rickmvi.jtoolbox.config.exceptions;

public class InjectionException extends ConfigException {
    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}