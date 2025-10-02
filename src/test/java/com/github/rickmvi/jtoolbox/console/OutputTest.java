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
        Output.formatted("{0:trim}$n", "   abc   ");
        Output.formatted("{0:rev}$n", "abc");
        Output.formatted("{0:cap}$n", "hello world");
        Output.formatted("{0:sub:2,3}$n", "abade");
        Output.formatted("{0:pad:10}$n", "Hi");
        Output.formatted("{0:len}$n", "abcd");
        Output.formatted("$N:10");
        Output.print("Success!");
    }
}