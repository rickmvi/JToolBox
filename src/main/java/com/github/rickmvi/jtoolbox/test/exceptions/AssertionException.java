package com.github.rickmvi.jtoolbox.test.exceptions;

public class AssertionException extends RuntimeException {
    public AssertionException(String message) {
        super("[Assertion Failed] " + message);
    }

    public AssertionException(String message, Throwable cause) {
        super("[Assertion Failed] " + message, cause);
    }
}
