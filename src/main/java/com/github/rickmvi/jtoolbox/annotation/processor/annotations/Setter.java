package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates setter methods for all non-final fields.
 * Can be applied to class or individual fields.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Setter {
    /**
     * Access level for generated setter.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /**
     * Whether to return 'this' for method chaining.
     */
    boolean chain() default false;
}
