package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Defines the sorting order for annotated components.
 * <p>This annotation is primarily used when multiple beans of the same type
 * are injected into a collection (e.g., {@code List<MyInterface>}). The
 * JToolbox container will sort the beans based on their order value.</p>
 * <p><strong>Rules:</strong></p>
 * <ul>
 * <li>Lower values have <b>higher priority</b> (they appear first in the list).</li>
 * <li>Higher values have <b>lower priority</b> (they appear later in the list).</li>
 * <li>Components without this annotation are treated with a default value of {@code 0}.</li>
 * </ul>
 * <p>Example usage:</p>
 * <pre>
 * &#64;Order(-10) // High priority, executes first
 * &#64;Component
 * public class HighPriorityFilter implements Filter { ... }
 * &#64;Order(50)  // Low priority, executes later
 * &#64;Component
 * public class LowPriorityFilter implements Filter { ... }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {

    /**
     * The order value.
     * <p>Default is {@code 0}. Can be any integer (positive, negative, or zero).</p>
     * @return the order value.
     */
    int value() default 0;
}