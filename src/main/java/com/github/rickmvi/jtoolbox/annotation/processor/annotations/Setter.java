package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Automatically generates setter methods during compilation.
 * <p>Setters will not be generated for 'final' fields. If applied to a class,
 * all eligible fields get setters.</p>
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Setter(chain = true)
 * public class Config {
 * private int timeout;
 * private String host;
 * }
 * // Usage: new Config().setTimeout(10).setHost("localhost");
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Setter {

    /** Access level for the generated setter. */
    AccessLevel value() default AccessLevel.PUBLIC;

    /** If true, the setter returns 'this' instead of 'void',
     * enabling method chaining (Fluent API).
     */
    boolean chain() default false;
}