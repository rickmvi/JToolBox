package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Lifecycle callback annotation used on a method that needs to be executed
 * after dependency injection is done to perform any initialization.
 * <p>This method is called by the JToolbox IoC container exactly once for
 * each bean instance, after all {@link Autowired} or {@link Inject}
 * members have been initialized.</p>
 *
 * <p><strong>Requirements:</strong></p>
 * <ul>
 * <li>The method must not have any parameters.</li>
 * <li>The return type should typically be {@code void}.</li>
 * <li>Only one method per class should ideally be annotated with {@code @PostConstruct}.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;Component
 * public class DatabaseService {
 * &#64;Autowired
 * private Config config;
 * private Connection connection;
 * &#64;PostConstruct
 * public void init() {
 * // This runs after 'config' is injected
 * this.connection = DriverManager.getConnection(config.getUrl());
 * System.out.println("Database connection established!");
 * }
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see PreDestroy
 * @see Autowired
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostConstruct {
}