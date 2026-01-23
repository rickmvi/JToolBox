package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a full Builder pattern implementation for the annotated class.
 * <p>This includes:
 * <ul>
 * <li>A static inner class (default name "Builder").</li>
 * <li>A static method to start the building process.</li>
 * <li>Fluent methods for each field.</li>
 * <li>A final build() method to return the instance.</li>
 * </ul>
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Builder {

    /** Name of the inner builder class. Defaults to "Builder". */
    String builderClassName() default "Builder";

    /** Name of the method that finalizes the build. Defaults to "build". */
    String buildMethodName() default "build";

    /** Name of the static entry-point method. Defaults to "builder". */
    String builderMethodName() default "builder";

    /** If true, generates a 'toBuilder()' instance method that initializes
     * the builder with current object values.
     */
    boolean toBuilder() default false;

    /** Visibility of the builder class. */
    AccessLevel access() default AccessLevel.PUBLIC;
}