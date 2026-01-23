package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation for mapping HTTP {@code GET} requests onto specific handler methods.
 *
 * <p>This is a specialized shortcut that acts as a synonym for
 * {@code @RequestMapping(method = Method.GET)}. GET requests are intended to
 * retrieve resources and should be considered safe and idempotent (they should
 * not modify the server state).</p>
 *
 * <p>Example usage within a Controller:</p>
 * <pre>
 * &#64;RestController
 * &#64;RequestMapping("/api/products")
 * public class ProductController {
 *
 * &#64;GetMapping("/{id}")
 * public Product getProduct(@PathVariable("id") Long id) {
 * return service.findById(id);
 * }
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {

    /**
     * The path mapping URI for the GET request.
     *
     * <p>This value is concatenated with the base path defined at the class level.
     * If a value starts with a slash, it is treated as a relative path.
     * If left empty, the method handles GET requests to the controller's base path.</p>
     *
     * @return the relative path mapping.
     */
    String value() default "";
}