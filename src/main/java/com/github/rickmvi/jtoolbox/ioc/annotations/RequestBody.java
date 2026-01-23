package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation indicating a method parameter should be bound to the body of the web request.
 *
 * <p>The body of the request is passed through an {@code HttpMessageConverter} to resolve the
 * method argument depending on the content type of the request (e.g., JSON to POJO).</p>
 *
 * <p>This annotation is typically used in methods that handle {@code POST}, {@code PUT},
 * or {@code PATCH} requests, where the data is sent within the HTTP message body rather
 * than as URL parameters.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;PostMapping("/users")
 * public User createUser(&#64;RequestBody User newUser) {
 * // 'newUser' is automatically populated from the request JSON
 * return userService.save(newUser);
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see PostMapping
 * @see PutMapping
 * @see RequestHeader
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}