package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to indicate that a dependency should be injected by the IoC container.
 * <p>Unlike a simple type-based injection, {@code @Inject} allows for more granular
 * control by permitting injection by a specific bean name. It can be applied to
 * fields for direct injection or to parameters (usually within constructors or
 * factory methods).</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Injection by type
 * &#64;Inject
 * private UserRepository userRepository;
 *
 * // Injection by specific bean name
 * &#64;Inject("mysqlUserRepository")
 * private UserRepository userRepository;
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Autowired
 * @see Bean
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {

    /**
     * The specific name of the bean to be injected.
     * <p>If provided, the IoC container will look for a bean with this exact name
     * (the qualifier). If left as an empty string (default), the container
     * will attempt to resolve the dependency primarily by its type.</p>
     *
     * @return the name of the target bean, or an empty string for type-based resolution.
     */
    String value() default "";

    /**
     * Determines whether the dependency is mandatory.
     * <p>If set to {@code true} (default), the container will throw a
     * {@code DependencyResolutionException} (or similar) if no matching bean is found.
     * If {@code false}, the container will assign {@code null} and proceed with
     * the application startup.</p>
     *
     * @return {@code true} if the dependency must be found, {@code false} otherwise.
     */
    boolean required() default true;
}