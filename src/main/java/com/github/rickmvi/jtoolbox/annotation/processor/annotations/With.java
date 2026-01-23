package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a "wither" method for the annotated field.
 *
 * <p>A wither is a clone-and-update operation. It returns a new instance of
 * the class with the specific field updated, leaving the original object
 * untouched.</p>
 *
 * <p><strong>Requirement:</strong> To work correctly, the class must have an
 * {@code @AllArgsConstructor} (or a constructor that accepts all fields
 * in the order they are declared), as the generated method uses it to
 * produce the new instance.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;AllArgsConstructor
 * &#64;Getter
 * public class Point {
 * &#64;With private final int x;
 * &#64;With private final int y;
 * }
 *
 * // Usage:
 * Point p1 = new Point(10, 20);
 * Point p2 = p1.withX(50);
 * // p1 remains (10, 20), p2 is (50, 20)
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface With {
    /**
     * The visibility of the generated 'with' method.
     * Defaults to {@link AccessLevel#PUBLIC}.
     */
    AccessLevel value() default AccessLevel.PUBLIC;
}