package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a constructor for "required" fields only.
 *
 * <p>A field is considered "required" if it is:
 * <ul>
 * <li>Marked as {@code final} and not initialized at the site of declaration.</li>
 * <li>Annotated with an annotation named {@code @NonNull} (if supported by the processor).</li>
 * </ul></p>
 *
 * <p>This annotation is highly recommended for Service and Controller classes within
 * the JToolbox framework to facilitate <strong>Constructor Injection</strong>,
 * which is safer and easier to test than field injection.</p>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;RequiredArgsConstructor
 * &#64;Service
 * public class UserService {
 * private final UserRepository repository; // Required (final)
 * private final EmailService emailService; // Required (final)
 * private String optionalNote;            // Ignored (not final)
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RequiredArgsConstructor {
    /**
     * Specifies the access level of the generated constructor.
     * Defaults to {@link AccessLevel#PUBLIC}.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /**
     * If specified, the constructor will be private and a static factory
     * method with this name will be created to wrap it.
     *
     * @return the name of the static factory method.
     */
    String staticName() default "";
}