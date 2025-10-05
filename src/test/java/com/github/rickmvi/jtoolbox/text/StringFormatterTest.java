package com.github.rickmvi.jtoolbox.text;

import com.github.rickmvi.jtoolbox.console.IO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFormatTest {

    /**
     * The `StringFormat.formatTest` method formats a given template string by replacing
     * placeholders with the values of the provided objects. Placeholders may refer to fields
     * within objects or match the type of primitive-like objects.
     */

    @Test
    void testFormatTest_withPrimitiveLikePlaceholders() {
        String template = "The value is {Integer} and {Boolean}.";
        Object[] values = {42, true};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("The value is 42 and true.", result);
    }

    @Test
    void testFormatTest_withObjectFields() {
        class TestObject {
            public String test = "A";
            public String field2 = "B";
        }

        String template = "Object fields: {test} and {field2}.";
        Object[] values = {new TestObject()};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("Object fields: A and B.", result);
    }

    @Test
    void testFormatTest_withNullPlaceholder() {
        String template = "The value is {Integer}.";
        Object[] values = {2};

        String result = StringFormat.interpolate(template, values);
        IO.interpolated(template, values);

        assertEquals("The value is 2.", result);
    }

    @Test
    void testFormatTest_withInvalidFieldName() {
        class TestObject {
            public String field = "value";
        }

        String template = "The object field is {invalidField}.";
        Object[] values = {new TestObject()};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("The object field is {invalidField}.", result);
    }

    @Test
    void testFormatTest_withEmptyTemplate() {
        String template = "";
        Object[] values = {42};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("", result);
    }

    @Test
    void testFormatTest_withNoPlaceholders() {
        String template = "No placeholders here.";
        Object[] values = {42};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("No placeholders here.", result);
    }

    @Test
    void testFormatTest_withExcessObjects() {
        String template = "The value is {Integer}.";
        Object[] values = {42, "extra"};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("The value is 42.", result);
    }

    @Test
    void testFormatTest_withMissingObjects() {
        String template = "The values are {field1} and {field2}.";
        Object[] values = {new Object()};

        String result = StringFormat.interpolate(template, values);
        System.out.println(result);

        assertEquals("The values are {field1} and {field2}.", result);
    }
}