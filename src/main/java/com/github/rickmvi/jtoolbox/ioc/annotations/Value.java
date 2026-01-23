package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to inject values into fields or parameters.
 * <p>Commonly used to inject externalized configuration properties defined in
 * {@code .properties}, {@code .yml} files, or environment variables. It can also
 * be used to inject simple literal values or expressions if the container
 * supports a SpEL-like (Spring Expression Language) evaluator.</p>
 *
 * <p><strong>Typical Usage Patterns:</strong></p>
 * <pre>
 * // Injecting a property with a placeholder
 * &#64;Value("${server.port}")
 * private int port;
 *
 * // Injecting a literal value
 * &#64;Value("DefaultAppName")
 * private String appName;
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Autowired
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * The actual value or placeholder to be injected.
     * <p>This string can be a literal value or a placeholder using the
     * syntax {@code ${property.name}}. If it's a placeholder, the IoC
     * container will resolve it against the available configuration sources.</p>
     *
     * @return the value or placeholder expression.
     */
    String value();
}