package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Transforms the class into a Fluent API provider.
 * <p>When applied, all eligible fields will receive a setter-like method
 * that returns {@code this}, allowing for method chaining. This is
 * particularly useful for configuration objects, DSLs, and internal
 * framework settings.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Method Chaining:</strong> Eliminates the need for multiple lines
 * of code to set attributes on a single object.</li>
 * <li><strong>Clean API:</strong> With {@code chain = true}, the "set" prefix
 * is removed, resulting in cleaner methods like {@code host("localhost")}
 * instead of {@code setHost("localhost")}.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Fluent(chain = true)
 * public class ServerConfig {
 * private String host;
 * private int port;
 * }
 * // Usage:
 * ServerConfig config = new ServerConfig()
 * .host("127.0.0.1")
 * .port(8080);
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Fluent {
    /**
     * Determines the naming convention for the generated methods.
     * <p>If {@code true} (default), the "set" prefix is removed (e.g., name()).
     * If {@code false}, the "set" prefix is kept but chaining is still enabled.</p>
     *
     * @return true to remove 'set' prefix, false to keep it.
     */
    boolean chain() default true;
}