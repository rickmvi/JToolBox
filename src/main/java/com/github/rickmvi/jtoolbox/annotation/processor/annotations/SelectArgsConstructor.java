package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates constructor with selected fields as parameters.
 * Specify field names that should be included in the constructor.
 * <p>
 * Example:
 * @SelectArgsConstructor({"id", "name", "email"})
 * public class User {
 *     private Long id;
 *     private String name;
 *     private String email;
 *     private LocalDateTime createdAt;
 * }
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SelectArgsConstructor {
    /**
     * Field names to include in constructor.
     */
    String[] value();

    /**
     * Access level for generated constructor.
     */
    AccessLevel access() default AccessLevel.PUBLIC;

    /**
     * Static factory method name (if specified, generates static method instead).
     */
    String staticName() default "";
}
