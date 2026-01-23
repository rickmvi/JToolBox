package com.github.rickmvi.jtoolbox.ioc.annotations.core;

import java.lang.annotation.*;

/**
 * Enable auto-configuration of the JToolbox Application Context.
 *
 * <p>Auto-configuration attempts to guess and configure beans that you are likely
 * to need based on your classpath. For example, if {@code jtoolbox-orm} is on
 * the classpath, this annotation triggers the creation of a DataSource and
 * EntityManager automatically.</p>
 *
 * <p>Auto-configuration classes are usually applied based on your classpath and
 * what beans you have defined. You can use the {@link #exclude()} attribute to
 * selectively disable specific auto-configurations.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;Configuration
 * &#64;EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
 * public class MyConfig {
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
public @interface EnableAutoConfiguration {

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     * <p>Use this when you want to take full manual control over a specific component
     * that JToolbox would otherwise configure automatically.</p>
     *
     * @return the array of classes to exclude.
     */
    Class<?>[] exclude() default {};
}