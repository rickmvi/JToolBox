package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Transforms the annotated class into a pure utility class.
 *
 * <p>A class annotated with {@code @UtilityClass} will undergo the following
 * changes during compilation:</p>
 * <ul>
 * <li>It will be marked as {@code final}.</li>
 * <li>A {@code private} no-args constructor will be generated that throws
 * an {@link UnsupportedOperationException}.</li>
 * <li>The JToolbox processor will verify that all members (methods and fields)
 * are explicitly marked as {@code static}.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;UtilityClass
 * public class StringUtils {
 * public String capitalize(String in) {
 * return in.toUpperCase(); // Becomes static automatically
 * }
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface UtilityClass {
}