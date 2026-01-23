package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates getter methods for all fields.
 * Can be applied to class or individual fields.
 *
 * @author Rick M. Viana
 * @version 1.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Getter {

    /** Access level for generated getter. */
    AccessLevel value() default AccessLevel.PUBLIC;

    /** Whether to generate lazy initialization for the getter. */
    boolean lazy() default false;
}
