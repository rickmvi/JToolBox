package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Automatically generates getter methods during compilation.
 * <p>If applied to a class, getters will be generated for all non-static fields.
 * If applied to a field, only that field will have a getter.</p>
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Getter
 * public class User {
 * private String name; // Generates getName()
 * &#64;Getter(lazy = true)
 * private String heavyResource; // Generates lazy-loaded getter
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Getter {

    /** The visibility of the generated method.
     * Defaults to PUBLIC.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /** If true, the field will be initialized only when the getter is first called.
     * Note: This usually requires the field to be private.
     */
    boolean lazy() default false;
}