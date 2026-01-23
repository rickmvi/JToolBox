package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Injects delegation methods into the class for the annotated field.
 *
 * <p>Any public method present in the field's type (or specified types)
 * will be generated in the containing class, forwarding the call directly
 * to the annotated field.</p>
 *
 * <p><strong>Key Benefits:</strong></p>
 * <ul>
 * <li><strong>Composition over Inheritance:</strong> Expose functionality
 * without the fragility of a "super" class.</li>
 * <li><strong>Encapsulation:</strong> Hide the internal field while
 * exposing its interface.</li>
 * <li><strong>Selective Delegation:</strong> Use {@code types} to limit
 * delegation to specific interfaces or {@code excludes} to hide methods.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * public class MyCollection {
 * &#64;Delegate(excludes = "clear")
 * private final List<String> list = new ArrayList<>();
 * }
 * // Now MyCollection has .add(), .size(), etc., but NOT .clear().
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Delegate {
    /**
     * Limits delegation to methods of these specific types.
     * If empty, all public methods of the field's type are used.
     */
    Class<?>[] types() default {};

    /**
     * A list of method names that should not be delegated.
     */
    String[] excludes() default {};
}