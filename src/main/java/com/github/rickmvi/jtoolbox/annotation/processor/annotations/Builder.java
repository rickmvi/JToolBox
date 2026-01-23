package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates builder pattern for the class.
 * Creates a static inner Builder class with fluent API.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Builder {
    /**
     * Name of the builder class.
     */
    String builderClassName() default "Builder";

    /**
     * Name of the build method.
     */
    String buildMethodName() default "build";

    /**
     * Name of the builder() static method.
     */
    String builderMethodName() default "builder";

    /**
     * Whether to generate toBuilder() method.
     */
    boolean toBuilder() default false;

    /**
     * Access level for the builder class.
     */
    AccessLevel access() default AccessLevel.PUBLIC;
}
