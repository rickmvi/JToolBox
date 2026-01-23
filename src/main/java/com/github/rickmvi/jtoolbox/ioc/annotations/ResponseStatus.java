package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to specify the HTTP response status code that should be returned
 * when the annotated method completes successfully.
 *
 * <p>This provides a declarative way to define response codes without having to
 * manually manipulate the {@code HttpContext} or {@code HttpResponse} objects
 * within the controller logic.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;PostMapping("/users")
 * &#64;ResponseStatus(201) // Returns '201 Created' instead of the default '200 OK'
 * public User createUser(&#64;RequestBody User user) {
 * return userService.save(user);
 * }
 *
 * &#64;DeleteMapping("/{id}")
 * &#64;ResponseStatus(204) // Returns '204 No Content'
 * public void deleteUser(&#64;PathVariable("id") Long id) {
 * userService.delete(id);
 * }
 * </pre>
 *
 * [Image of HTTP response structure showing status line and status codes]
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see PostMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseStatus {

    /**
     * The HTTP status code to use for the response.
     * <p>Common values include:</p>
     * <ul>
     * <li>200 (OK) - Default</li>
     * <li>201 (Created) - Successful resource creation</li>
     * <li>202 (Accepted) - Request accepted for processing</li>
     * <li>204 (No Content) - Successful request with no body to return</li>
     * </ul>
     *
     * @return the integer representing the HTTP status code.
     */
    int value() default 200;
}