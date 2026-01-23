package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Stereotype annotation indicating that an annotated class is a "Repository",
 * originally defined by Domain-Driven Design (DDD) as a mechanism for
 * encapsulating storage, retrieval, and search behavior which emulates
 * a collection of objects.
 *
 * <p>This is a specialized form of {@link Component}, serving as a specialization
 * of the persistence layer. Classes annotated with {@code @Repository} are
 * eligible for automated scanning and registration within the JToolbox
 * IoC container.</p>
 *
 * <p><strong>Typical usage:</strong></p>
 * <pre>
 * &#64;Repository("userRepository")
 * public class JdbcUserRepository implements UserRepository {
 * // Data access logic (SQL, NoSQL, etc.)
 * }
 * </pre>
 *
 * [Image of layered architecture showing Controller Service and Repository layers]
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a bean in case of an autodetected component.
     * * <p>If not specified, the bean name will be generated based on the
     * lowercase non-qualified class name.</p>
     *
     * @return the suggested bean name, if any.
     */
    String value() default "";
}