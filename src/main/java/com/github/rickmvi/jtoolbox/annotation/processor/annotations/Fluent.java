package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a fluent API wrapper around the class.
 * Each setter returns 'this' for method chaining.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Fluent {
    /**
     * Whether to remove 'set' prefix from setters.
     */
    boolean chain() default true;
}
