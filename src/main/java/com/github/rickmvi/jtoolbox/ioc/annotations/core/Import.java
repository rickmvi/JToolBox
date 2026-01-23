package com.github.rickmvi.jtoolbox.ioc.annotations.core;

import java.lang.annotation.*;

/**
 * Indicates one or more component classes to import — typically {@link Import} classes.
 *
 * <p>Provides functionality equivalent to the {@code <import/>} element in XML.
 * Allows for importing {@code @Configuration} classes, {@code ImportSelector}
 * implementations, or regular {@code @Component} classes.</p>
 *
 * <p>By using {@code @Import}, you can modularize your application setup, keeping
 * related bean definitions together while aggregating them into a single
 * application context.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;Configuration
 * public class DatabaseConfig {
 * &#64;Bean
 * public DataSource dataSource() { ... }
 * }
 *
 * &#64;Configuration
 * &#64;Import(DatabaseConfig.class)
 * public class AppConfig {
 * // This class now has access to the DataSource bean
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see JToolboxApplication
 * @see ComponentScan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    /**
     * Specifies one or more classes to be imported into the current JToolbox context.
     *
     * <p>These classes will be processed by the container as if they were
     * explicitly declared in the initial context scanning.</p>
     *
     * @return an array of classes to be imported.
     */
    Class<?>[] value();
}