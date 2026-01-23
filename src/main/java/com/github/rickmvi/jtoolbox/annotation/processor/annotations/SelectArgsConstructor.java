package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a constructor that accepts a specific subset of fields as parameters.
 *
 * <p>The developer must provide the exact names of the fields to be included.
 * This is ideal for classes where only part of the state defines the object
 * for a particular business context, or for supporting legacy signatures.</p>
 *
 * <p><strong>Key Benefits:</strong></p>
 * <ul>
 * <li><strong>Granular Control:</strong> No need to rely on field modifiers (like final).</li>
 * <li><strong>Domain Specific:</strong> Useful for creating constructors for
 * "Business Keys" while ignoring metadata like 'createdAt' or 'updatedAt'.</li>
 * <li><strong>Validation:</strong> The JToolbox processor will validate if the
 * provided field names actually exist in the class at compile time.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;SelectArgsConstructor({"id", "name"})
 * public class User {
 * private Long id;
 * private String name;
 * private String email; // Not in constructor
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 * @see AllArgsConstructor
 * @see RequiredArgsConstructor
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SelectArgsConstructor {
    /**
     * An array of field names to be included as constructor parameters.
     * <p>The order in the array determines the order of the parameters.</p>
     */
    String[] value();

    /**
     * The visibility level of the generated constructor.
     * Defaults to {@link AccessLevel#PUBLIC}.
     */
    AccessLevel access() default AccessLevel.PUBLIC;

    /**
     * If specified, generates a private constructor and a public static
     * factory method with this name.
     */
    String staticName() default "";
}