package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a with() method for the field that returns a copy with the changed value.
 * Useful for immutable classes.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface With {
    /**
     * Access level for generated with method.
     */
    AccessLevel value() default AccessLevel.PUBLIC;
}
