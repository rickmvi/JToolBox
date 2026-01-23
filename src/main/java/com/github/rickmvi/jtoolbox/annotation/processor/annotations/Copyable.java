package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a method to create a shallow copy of the annotated class instance.
 *
 * <p>Unlike Java's native {@code clone()} method, the generated {@code copy()}
 * method is type-safe and does not require catching {@code CloneNotSupportedException}.
 * It effectively takes all current field values and passes them to a constructor
 * to create a new, independent instance.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Type Safety:</strong> Returns the exact type of the class,
 * avoiding manual casting.</li>
 * <li><strong>Customization:</strong> The method name and visibility can be
 * adjusted via {@link #methodName()} and {@link #access()}.</li>
 * <li><strong>Shallow Copy:</strong> Primitive values are copied, but object
 * references still point to the same instances as the original.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Copyable(methodName = "duplicate")
 * &#64;AllArgsConstructor
 * public class Task {
 * private String title;
 * private boolean completed;
 * }
 *
 * // Usage:
 * Task original = new Task("Estudar Go", false);
 * Task clone = original.duplicate();
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Copyable {
    /**
     * The name of the generated copy method.
     * Defaults to "copy".
     */
    String methodName() default "copy";

    /**
     * The access level of the generated method.
     * Defaults to {@link AccessLevel#PUBLIC}.
     */
    AccessLevel access() default AccessLevel.PUBLIC;
}