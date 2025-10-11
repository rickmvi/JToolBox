package com.github.rickmvi.jtoolbox.console;

import org.junit.jupiter.api.Test;

class IOTest {

    @Test
    void format() {
        IO.println("======@======");
        IO.format("$N:{0}", 5);
        IO.println("======@======");
    }

    @Test
    void newlineWithoutArguments() {
        IO.format("{1:pad:10} {0}$n", "Before newline", 123);
        IO.newline();
        IO.println("After newline");
    }

    @Test
    void newlineWithArguments() {
        IO.println("Before multiple newlines");
        IO.newline(3);
        IO.println("After multiple newlines");
    }

    static class TestObject {
        public String field = "value";
    }

    @Test
    void interpolatedWithoutArguments() {
        TestObject testObject = new TestObject();
        IO.interpolated("Hello, World! {field} {1}", testObject, "fg3f");
    }

    @Test
    void interpolatedWithArguments() {
        IO.interpolated("Hello, {0}!", "Test");
    }

    @Test
    void interpolatedWithNullFormat() {
        IO.interpolated(null);
    }
}