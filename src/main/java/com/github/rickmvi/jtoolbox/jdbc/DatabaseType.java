package com.github.rickmvi.jtoolbox.jdbc;

import org.jetbrains.annotations.NotNull;

public enum DatabaseType {

    POSTGRESQL("jdbc:postgresql://"),
    MYSQL("jdbc:mysql://"),
    SQLITE("jdbc:sqlite:"),
    ORACLE("jdbc:oracle:thin:@"),
    SQLSERVER("jdbc:sqlserver://"),
    H2("jdbc:h2:");

    private final String prefix;

    DatabaseType(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

    public static @NotNull DatabaseType fromPrefix(String url) {
        for (DatabaseType conn : values()) {
            if (url.startsWith(conn.prefix)) {
                return conn;
            }
        }
        throw new IllegalArgumentException("Unknown JDBC URL prefix: " + url);
    }

}
