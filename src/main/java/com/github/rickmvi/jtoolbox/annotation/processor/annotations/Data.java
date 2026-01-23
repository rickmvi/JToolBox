package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates all common methods: getters, setters, toString, equals, hashCode, constructor.
 * Equivalent to combining multiple annotations.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Data {
    /**
     * Static factory method name for constructor.
     */
    String staticConstructor() default "";
}
