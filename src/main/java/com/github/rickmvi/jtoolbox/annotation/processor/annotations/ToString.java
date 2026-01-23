package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Generates an implementation of the {@code toString()} method during compilation.
 * <p>By default, it produces a string starting with the class name, followed by
 * each field in order, separated by commas and enclosed in parentheses.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Selective Inclusion:</strong> Use {@code of} to list specific fields or
 * {@code exclude} to hide sensitive data (like passwords).</li>
 * <li><strong>Superclass Integration:</strong> {@code callSuper = true} appends
 * the output of the parent's {@code toString()} to the result.</li>
 * <li><strong>Clean Formatting:</strong> {@code includeFieldNames} toggles between
 * {@code field=value} and just {@code value}.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;ToString(exclude = "password")
 * public class User {
 * private Long id;
 * private String name;
 * private String password;
 * }
 * // Output: User(id=1, name=Rick)
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ToString {
    /**
     * Whether to include the name of the field in the output.
     * <p>If true: {@code User(id=1)}. If false: {@code User(1)}.</p>
     */
    boolean includeFieldNames() default true;

    /**
     * Whether to call {@code super.toString()} and include its result.
     */
    boolean callSuper() default false;

    /**
     * Fields that should be omitted from the generated string.
     */
    String[] exclude() default {};

    /**
     * Fields that should be explicitly included. If defined, all other
     * fields are ignored.
     */
    String[] of() default {};
}