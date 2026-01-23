package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation which indicates that a method parameter should be bound to a web request header.
 *
 * <p>The JToolbox container will extract the value of the specified header from the
 * incoming HTTP request and inject it into the annotated parameter. Automatic
 * type conversion is applied to the extracted string value.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;GetMapping("/secure-data")
 * public String getSecureData(
 * &#64;RequestHeader("Authorization") String token,
 * &#64;RequestHeader(value = "X-Device-Id", required = false) String deviceId
 * ) {
 * return "Data for token: " + token;
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see GetMapping
 * @see RequestParam
 * @see PathVariable
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestHeader {

    /**
     * The name of the HTTP request header to bind to.
     * @return the header name.
     */
    String value();

    /**
     * Whether the header is required.
     * <p>Defaults to {@code true}. If the header is missing from the request and
     * no {@link #defaultValue()} is provided, the container will throw an
     * {@code IllegalArgumentException}.</p>
     *
     * @return {@code true} if the header is mandatory, {@code false} otherwise.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback when the header is not present
     * or is empty.
     * <p>Supplying a default value implicitly sets {@link #required()} to {@code false}.</p>
     *
     * @return the default value.
     */
    String defaultValue() default "";
}