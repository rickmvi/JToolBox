package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates constructor with no arguments.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NoArgsConstructor {
    /**
     * Access level for generated constructor.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /**
     * Static factory method name (if specified, generates static method instead).
     */
    String staticName() default "";

    /**
     * Whether to force generation even if final fields exist.
     */
    boolean force() default false;
}
