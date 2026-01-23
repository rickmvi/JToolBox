package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * General-purpose annotation for mapping web requests onto specific handler methods.
 *
 * <p>This annotation can be used to define the primary URI path for a resource and
 * the HTTP methods it supports. Specialized annotations like {@link GetMapping}
 * and {@link PostMapping} are essentially shortcuts for this annotation.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;RestController
 * public class MyController {
 *
 * // Maps both GET and POST to /api/data
 * &#64;RequestMapping(value = "/api/data", method = {"GET", "POST"})
 * public Response handleMultipleMethods() {
 * return new Response("Handled!");
 * }
 * }
 * </pre>
 *
 * [Image of HTTP request routing logic showing path and method matching]
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    /**
     * The primary path mapping URI.
     * <p>This value defines the endpoint address. It can include URI templates
     * (e.g., {@code "/users/{id}"}) which can be captured via {@link PathVariable}.</p>
     *
     * @return the path mapping.
     */
    String value() default "";

    /**
     * The HTTP request methods to map to, narrowing the primary mapping.
     * <p>Expected values are standard HTTP verbs like "GET", "POST", "PUT", etc.
     * If this array is left empty, the framework behavior typically defaults
     * to GET or handles all methods depending on the dispatcher implementation.</p>
     *
     * @return the supported HTTP methods.
     */
    String[] method() default {};
}