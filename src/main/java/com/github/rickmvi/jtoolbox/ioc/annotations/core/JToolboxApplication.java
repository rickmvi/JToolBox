package com.github.rickmvi.jtoolbox.ioc.annotations.core;

import java.lang.annotation.*;

/**
 * Indicates a configuration class that declares one or more {@code @Bean} methods
 * and also triggers auto-configuration and component scanning.
 *
 * <p>This is a convenience annotation that is equivalent to declaring
 * {@code @Configuration}, {@code @ComponentScan}, and (if applicable)
 * {@code @EnableAutoConfiguration}.</p>
 *
 * <p>When an application is launched using {@code JToolbox.run(MyClass.class)},
 * the framework looks for this annotation to determine the base package for
 * scanning all managed components.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;JToolboxApplication
 * public class MainApplication {
 * public static void main(String[] args) {
 * JToolboxApplication.run(MainApplication.class, args);
 * }
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see com.github.rickmvi.jtoolbox.ioc.annotations.Component
 * @see com.github.rickmvi.jtoolbox.ioc.annotations.web.EnableWebServer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JToolboxApplication {
}