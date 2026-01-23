package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * A specialized {@link Component} that marks a class as a Web Controller
 * in a RESTful environment.
 *
 * <p>Classes annotated with {@code @RestController} are automatically scanned
 * by the JToolbox IoC container. Their methods, when annotated with mapping
 * annotations such as {@link GetMapping} or {@link PostMapping}, serve as
 * endpoints for incoming HTTP requests.</p>
 *
 * <p>Unlike a traditional Controller, a {@code @RestController} implies that
 * the return value of every method is serialized directly into the HTTP
 * response body (acting as a implicit {@code @ResponseBody}).</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;RestController("/api/v1/users")
 * public class UserController {
 *
 * &#64;GetMapping("/{id}")
 * public User getUser(@PathVariable("id") String id) {
 * return userService.findById(id);
 * }
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see RequestMapping
 * @see Repository
 * @see Service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a bean in case of an autodetected component.
     *
     * <p>Additionally, in many framework implementations, this value can
     * also serve as a shortcut for the base {@link RequestMapping} path.</p>
     *
     * @return the suggested bean name, if any.
     */
    String value() default "";
}