package com.github.rickmvi.jtoolbox.jdbc;

import com.github.rickmvi.jtoolbox.util.Try;

public interface LoadDriver {

    static void load(String driverClassName) {
        Try.runThrowing(() -> Class.forName(driverClassName))
                .onFailure(e -> {
                    throw new RuntimeException("Failed to load JDBC driver: " + driverClassName, e);
                })
                .orThrow();
    }

}
