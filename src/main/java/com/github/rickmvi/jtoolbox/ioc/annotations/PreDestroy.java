package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Lifecycle callback annotation used on a method to be signaled when the
 * container is in the process of removing the bean instance.
 * <p>The method annotated with {@code @PreDestroy} is typically used to
 * release resources that the bean has been holding, such as closing
 * database connections, stopping background threads, or clearing caches.</p>
 * <p><strong>Requirements:</strong></p>
 * <ul>
 * <li>The method must not have any parameters.</li>
 * <li>The return type should typically be {@code void}.</li>
 * <li>The method will be called only once by the JToolbox IoC container
 * during the shutdown phase of the application context.</li>
 * </ul>
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Component
 * public class ConnectionPool {
 * &#64;PreDestroy
 * public void shutdown() {
 * // Close all active connections before the bean is destroyed
 * pool.closeAll();
 * System.out.println("Connection pool closed successfully.");
 * }
 * }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see PostConstruct
 * @see Component
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreDestroy {
}