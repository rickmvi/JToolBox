package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Marks a field as mandatory and triggers automated null-checks.
 *
 * <p>When a field is annotated with {@code @NonNull}, the JToolbox Annotation
 * Processor will inject a null-check (Guard Clause) at the beginning of
 * any generated method that assigns a value to this field.</p>
 *
 * <p><strong>Interactions:</strong></p>
 * <ul>
 * <li><strong>Constructors:</strong> Fields with {@code @NonNull} are included
 * in {@link RequiredArgsConstructor} even if they are not {@code final}.</li>
 * <li><strong>Setters:</strong> The generated setter will throw a
 * {@link NullPointerException} if a null value is passed.</li>
 * <li><strong>Validation:</strong> Acts as a compile-time hint for IDEs and
 * a runtime guarantee for the framework.</li>
 * </ul>
 *
 * <pre>
 * public class User {
 * &#64;NonNull private String username;
 * }
 * // Generated logic:
 * // if (username == null) throw new NullPointerException("username is marked non-null");
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface NonNull {
}