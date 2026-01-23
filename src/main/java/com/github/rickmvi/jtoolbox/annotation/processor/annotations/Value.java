package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Generates an immutable value class.
 * <p>Applying {@code @Value} is shorthand for applying several annotations
 * that together create a "Value Object". Specifically, it performs the
 * following transformations during compilation:</p>
 * <ul>
 * <li>The class is marked as {@code final}.</li>
 * <li>All fields are made {@code private} and {@code final}.</li>
 * <li>An {@link AllArgsConstructor} is generated.</li>
 * <li>{@link Getter} methods are generated for all fields.</li>
 * <li>{@link ToString} and {@link EqualsAndHashCode} are generated.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Value(staticConstructor = "of")
 * public class UserIdentity {
 * String id;
 * String email;
 * }
 * // Usage:
 * UserIdentity id = UserIdentity.of("123", "rick@jtoolbox.com");
 * // id.setEmail("..."); // Compilation error: No setters generated!
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Value {
    /**
     * If specified, the generated constructor will be private, and a public
     * static factory method with this name will be created.
     * @return the name of the static factory method.
     */
    String staticConstructor() default "";
}