package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a constructor with no parameters.
 *
 * <p>This is particularly useful for frameworks that require a default constructor
 * for instantiation via reflection (e.g., JPA entities, JSON DTOs).</p>
 *
 * <p><strong>The "Force" Attribute:</strong></p>
 * <p>If the class contains {@code final} fields, a no-args constructor normally
 * causes a compilation error because final fields must be initialized.
 * By setting {@code force = true}, the JToolbox processor will initialize
 * these fields with default values (0, false, or null).</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;NoArgsConstructor(access = AccessLevel.PROTECTED)
 * public class UserEntity {
 * private Long id;
 * private String username;
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NoArgsConstructor {
    /**
     * Specifies the access level of the generated constructor.
     * Often set to {@code PROTECTED} for JPA entities.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /**
     * If specified, the constructor becomes private and a static
     * factory method is generated.
     */
    String staticName() default "";

    /**
     * If {@code true}, forces the generation of the constructor even if
     * uninitialized {@code final} fields exist.
     * <p>Warning: This may bypass Java's safety guarantees for final fields.</p>
     */
    boolean force() default false;
}