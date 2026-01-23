package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method parameter should be bound to a URI template variable.
 * <p>If the method is handled by a web mapping (like {@link GetMapping} or {@link PostMapping}),
 * the JToolbox container will extract the value from the requested URI and inject it into
 * the annotated parameter, performing type conversion automatically.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;GetMapping("/users/{id}")
 * public User getUserById(&#64;PathVariable("id") Long userId) {
 * return userService.findById(userId);
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see RequestMapping
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    /**
     * The name of the path variable to bind to.
     * <p>This value must match the placeholder name defined in the mapping
     * annotation (e.g., if the path is {@code "/users/{userId}"}, the value
     * should be {@code "userId"}).</p>
     * <p>If left as an empty string (default), the framework will attempt to
     * use the name of the method parameter itself as the variable name.</p>
     *
     * @return the name of the URI template variable.
     */
    String value() default "";
}