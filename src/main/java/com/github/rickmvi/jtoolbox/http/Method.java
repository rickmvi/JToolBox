package com.github.rickmvi.jtoolbox.http;

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
