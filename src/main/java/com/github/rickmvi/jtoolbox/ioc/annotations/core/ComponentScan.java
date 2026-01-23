package com.github.rickmvi.jtoolbox.ioc.annotations.core;

import java.lang.annotation.*;

/**
 * Configures component scanning directives for the JToolbox IoC container.
 *
 * <p>By default, if no packages are specified, the scanning will start from the package
 * of the class that is annotated with {@code @ComponentScan} (or {@link JToolboxApplication}).
 * This annotation allows for fine-grained control over which parts of the classpath
 * should be searched for {@link com.github.rickmvi.jtoolbox.ioc.annotations.Component}
 * and its stereotypes.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;Configuration
 * &#64;ComponentScan({"com.myapp.services", "com.myapp.controllers"})
 * public class AppConfig {
 * }
 * </pre>
 *
 *
 *
 * @author rickmvi
 * @since 1.0.0
 * @see com.github.rickmvi.jtoolbox.ioc.annotations.Component
 * @see JToolboxApplication
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {

    /**
     * Alias for {@link #basePackages()}.
     * <p>Allows for a more concise annotation declaration, such as
     * {@code @ComponentScan("com.my.pkg")}.</p>
     *
     * @return the array of base packages to scan.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>{@link #value()} is an alias for this attribute.</p>
     *
     * @return the array of base packages to scan.
     */
    String[] basePackages() default {};
}