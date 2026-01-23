package com.github.rickmvi.jtoolbox.ioc.annotations.web;

import java.lang.annotation.*;

/**
 * Provides fine-grained configuration for the JToolbox Web MVC framework.
 * <p>This annotation allows developers to customize global behaviors of the
 * MVC pipeline, such as enabling automatic Bean Validation for incoming
 * request bodies and protecting the server against large payload attacks
 * by defining size limits.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;Configuration
 * &#64;WebMvcConfiguration(enableValidation = true, maxRequestSize = 20971520) // 20MB
 * public class MyWebConfig {
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see EnableWebServer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebMvcConfiguration {

    /**
     * Toggles the automatic validation mechanism for request parameters and bodies.
     * <p>When enabled (default), the framework will automatically perform
     * validation checks on objects annotated with {@code @RequestBody} before
     * invoking the controller method. If validation fails, a 400 Bad Request
     * response will be issued.</p>
     *
     * @return {@code true} if validation is active, {@code false} otherwise.
     */
    boolean enableValidation() default true;

    /**
     * Defines the maximum allowed size (in bytes) for any incoming HTTP request body.
     * <p>This is a critical security setting to prevent Denial of Service (DoS)
     * attacks. If a client sends a payload exceeding this limit, the server
     * will immediately terminate the connection or return a 413 Payload Too
     * Large status.</p>
     * <p>The default is 10MB (10,485,760 bytes).</p>
     *
     * @return the maximum request size in bytes.
     */
    long maxRequestSize() default 10485760;
}