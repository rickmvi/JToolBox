package com.github.rickmvi.jtoolbox.util.http;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Method {

    GET     ("GET"),
    POST    ("POST"),
    PUT     ("PUT"),
    DELETE  ("DELETE"),
    HEAD    ("HEAD"),
    OPTIONS ("OPTIONS"),
    TRACE   ("TRACE"),
    CONNECT ("CONNECT"),
    PATCH   ("PATCH");

    private final String method;

    public String method() {
        return this.method;
    }

    /**
     * Converts a string representation of an HTTP method to its corresponding {@code Methods} enum constant.
     * This method performs a case-insensitive match against the defined HTTP methods in the {@code Methods} enum.
     *
     * @param method the string representation of the HTTP method; must not be {@code null}
     * @return an {@code Optional} containing the corresponding {@code Methods} enum constant if a match is found;
     *         otherwise, an empty {@code Optional}
     * @throws NullPointerException if the {@code method} parameter is {@code null}
     */
    public static @Nullable Method fromString(String method) {
        var mtd = Objects.requireNonNull(method);
        for (Method m : Method.values()) {
            if (m.method.equalsIgnoreCase(mtd)) {
                return m;
            }
        }
        return null;
    }

}
