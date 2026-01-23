package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation for mapping HTTP {@code DELETE} requests onto specific handler methods.
 * <p>This is a specialized shortcut that acts as a synonym for
 * {@code @RequestMapping(method = Method.DELETE)}. Methods annotated with this
 * are typically used to perform resource removal operations.</p>
 * <p>Example usage within a Controller:</p>
 * <pre>
 * &#64;RestController
 * &#64;RequestMapping("/api/users")
 * public class UserController {
 * &#64;DeleteMapping("/{id}")
 * public void deleteUser(@PathVariable("id") String userId) {
 * service.remove(userId);
 * }
 * }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeleteMapping {

    /**
     * The path mapping URI for the DELETE request.
     * <p>This value is concatenated with the base path defined at the class level
     * (usually via {@code @RequestMapping}). If left empty, the method will
     * handle DELETE requests to the base path itself.</p>
     * @return the relative path mapping.
     */
    String value() default "";
}