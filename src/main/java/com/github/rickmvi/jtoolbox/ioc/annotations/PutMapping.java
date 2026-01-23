package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation for mapping HTTP {@code PUT} requests onto specific handler methods.
 *
 * <p>This is a specialized shortcut that acts as a synonym for
 * {@code @RequestMapping(method = Method.PUT)}. In a RESTful architecture,
 * PUT requests are typically used to update an existing resource or
 * replace it entirely.</p>
 *
 * <p><strong>Example usage within a Controller:</strong></p>
 * <pre>
 * &#144;RestController
 * &#144;RequestMapping("/api/users")
 * public class UserController {
 *
 * &#144;PutMapping("/{id}")
 * public User updateUser(&#144;PathVariable("id") Long id, &#144;RequestBody User user) {
 * return userService.update(id, user);
 * }
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see PostMapping
 * @see DeleteMapping
 * @see RequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PutMapping {

    /**
     * The path mapping URI for the PUT request.
     *
     * <p>This value is concatenated with the base path defined at the class level.
     * It may include URI templates (e.g., {@code "/{id}"}) to be resolved
     * by {@link PathVariable} parameters.</p>
     *
     * @return the relative path mapping.
     */
    String value() default "";
}