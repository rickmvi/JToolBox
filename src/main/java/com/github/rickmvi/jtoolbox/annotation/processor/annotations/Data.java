package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * A shortcut annotation that bundles the functionality of several common
 * JToolbox annotations into one.
 *
 * <p>Applying {@code @Data} is equivalent to applying:
 * <ul>
 * <li>{@link Getter} on all fields.</li>
 * <li>{@link Setter} on all non-final fields.</li>
 * <li>{@link ToString} for the class.</li>
 * <li>{@link EqualsAndHashCode} for the class.</li>
 * <li>{@link RequiredArgsConstructor} for final fields or non-null fields.</li>
 * </ul>
 * </p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Data(staticConstructor = "of")
 * public class User {
 * private final Long id;
 * private String name;
 * }
 * // Usage:
 * User user = User.of(1L); // Using static factory
 * user.setName("Rick");
 * System.out.println(user.getName());
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Data {
    /**
     * If provided, the generated constructor will be private, and a static
     * factory method with this name will be created.
     * @return the name of the static factory method.
     */
    String staticConstructor() default "";
}