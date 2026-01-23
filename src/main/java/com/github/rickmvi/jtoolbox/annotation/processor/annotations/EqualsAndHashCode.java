package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Generates implementations for {@code equals(Object other)} and {@code hashCode()}
 * during compilation.
 *
 * <p>By default, all non-static and non-transient fields are used. This ensures
 * that objects behave correctly when used as keys in a {@link java.util.HashMap}
 * or elements in a {@link java.util.HashSet}.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Superclass Integration:</strong> Use {@code callSuper = true} to include
 * properties from the parent class in the equality check.</li>
 * <li><strong>Selective Logic:</strong> Use {@code exclude} to ignore metadata
 * fields (like 'updatedAt') or {@code of} to define a strict identity based
 * on business keys.</li>
 * </ul>
 *
 *
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface EqualsAndHashCode {
    /**
     * If true, calls {@code super.equals(other)} and {@code super.hashCode()}.
     * <p>Warning: Ensure the superclass also implements these methods correctly
     * to avoid unintended identity comparisons.</p>
     */
    boolean callSuper() default false;

    /**
     * List of field names that should be ignored by the generated methods.
     */
    String[] exclude() default {};

    /**
     * Explicit list of field names to use. If specified, all other fields
     * are ignored.
     */
    String[] of() default {};
}