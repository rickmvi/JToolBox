package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation for mapping HTTP {@code POST} requests onto specific handler methods.
 *
 * <p>This is a specialized shortcut that acts as a synonym for
 * {@code @RequestMapping(method = Method.POST)}. POST requests are typically
 * used to create new resources or submit sensitive data, as the data is carried
 * in the request body rather than the URL.</p>
 *
 * <p><strong>Example usage within a Controller:</strong></p>
 * <pre>
 * &#64;RestController
 * &#64;RequestMapping("/api/users")
 * public class UserController {
 *
 * &#64;PostMapping
 * public User createUser(&#64;RequestBody User user) {
 * return userService.save(user);
 * }
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see RequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostMapping {

    /**
     * The path mapping URI for the POST request.
     *
     * <p>This value is concatenated with the base path defined at the class level.
     * If left empty, the method handles POST requests directed exactly at the
     * controller's base path.</p>
     *
     * @return the relative path mapping.
     */
    String value() default "";
}