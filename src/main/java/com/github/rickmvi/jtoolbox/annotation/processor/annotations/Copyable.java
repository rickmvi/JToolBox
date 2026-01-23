package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a copy() method that creates a shallow copy of the object.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Copyable {
    /**
     * Name of the copy method.
     */
    String methodName() default "copy";

    /**
     * Access level for generated copy method.
     */
    AccessLevel access() default AccessLevel.PUBLIC;
}