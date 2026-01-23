package com.github.rickmvi.jtoolbox.ioc.annotations.web;

import java.lang.annotation.*;

/**
 * Enables and configures the embedded Web Server for the JToolbox application.
 *
 * <p>When this annotation is detected on a configuration class or the main application
 * class, the IoC container will bootstrap the internal web engine (e.g., based on
 * Undertow, Netty, or Sun HttpServer) using the provided settings.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;EnableWebServer(port = 9000, host = "localhost", enableCors = true)
 * &#64;Configuration
 * public class AppLauncher {
 * public static void main(String[] args) {
 * JToolboxApplication.run(AppLauncher.class, args);
 * }
 * }
 * </pre>
 *
 * [Image of a Web Server architecture showing Host Port and CORS middleware layer]
 *
 * @author rickmvi
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableWebServer {

    /**
     * The TCP port on which the web server will listen for incoming connections.
     * <p>Standard web ports are usually 80 (HTTP) or 443 (HTTPS), while
     * development defaults typically range from 8080 to 9000.</p>
     *
     * @return the network port number. Defaults to {@code 8080}.
     */
    int port() default 8080;

    /**
     * The network interface/host address to bind the server to.
     * <p>Using {@code "0.0.0.0"} allows the server to listen on all available
     * network interfaces (ideal for Docker/Production). Use {@code "127.0.0.1"}
     * or {@code "localhost"} to restrict access to the local machine only.</p>
     *
     * @return the binding host address. Defaults to {@code "0.0.0.0"}.
     */
    String host() default "0.0.0.0";

    /**
     * Toggles Cross-Origin Resource Sharing (CORS) support.
     * <p>If enabled, the server will include necessary headers to allow
     * browsers to make requests from different origins, which is essential
     * for SPA (Single Page Applications) like React or Angular.</p>
     *
     * @return {@code true} to enable CORS middleware, {@code false} to disable.
     * Defaults to {@code false}.
     */
    boolean enableCors() default false;

    /**
     * Defines the origins allowed to access the server's resources when
     * {@link #enableCors()} is true.
     * <p>Wildcard {@code "*"} allows all origins (not recommended for production).
     * For better security, specify exact domains like {@code "https://myapp.com"}.</p>
     *
     * @return a string containing the allowed origins. Defaults to {@code "*"}.
     */
    String corsOrigin() default "*";
}