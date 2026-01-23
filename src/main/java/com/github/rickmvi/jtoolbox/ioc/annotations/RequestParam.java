package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method parameter should be bound to a web request
 * query parameter.
 *
 * <p>The JToolbox container extracts the value from the URL's query string
 * (e.g., {@code ?name=value}) and performs automatic type conversion to the
 * target parameter type.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;GetMapping("/users/search")
 * public List<User> search(
 * &#64;RequestParam("name") String name,
 * &#64;RequestParam(value = "page", defaultValue = "0") int page
 * ) {
 * return userService.findByName(name, page);
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see PathVariable
 * @see RequestHeader
 * @see RequestBody
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /**
     * The name of the query parameter to bind to.
     * <p>If left as an empty string (default), the framework will attempt to
     * use the name of the method parameter as the query parameter name.</p>
     *
     * @return the name of the query parameter.
     */
    String value() default "";

    /**
     * Whether the parameter is required.
     * <p>Defaults to {@code true}. If the parameter is missing from the request
     * and no {@link #defaultValue()} is provided, the container will throw
     * an exception during request handling.</p>
     *
     * @return {@code true} if the parameter is mandatory, {@code false} otherwise.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback when the request parameter is
     * not provided or is empty.
     * <p>Supplying a default value implicitly sets {@link #required()} to {@code false}.</p>
     *
     * @return the default value.
     */
    String defaultValue() default "";
}