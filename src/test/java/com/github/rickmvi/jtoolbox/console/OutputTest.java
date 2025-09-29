package com.github.rickmvi.jtoolbox.console;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OutputTest {

    @Contract(value = " -> new", pure = true)
    static @NotNull BigDecimal value() {
        return new BigDecimal("100000");
    }

    @Test
    void formatted() {
        Output.formatted("{0:dc}$n", value());
        Output.print("Success!");
    }
}