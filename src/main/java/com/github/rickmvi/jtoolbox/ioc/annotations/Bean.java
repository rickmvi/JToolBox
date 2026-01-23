package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a method produces a bean to be managed by the JToolbox IoC container.
 * <p>The return type of the method is registered as the bean type. This is particularly
 * useful for integrating third-party libraries where you cannot annotate the class
 * directly with {@link Component}.</p>
 * <p>Example usage:</p>
 * <pre>
 * &#64;Configuration
 * public class AppConfig {
 * &#64;Bean(value = "dataSource", scope = Component.Scope.SINGLETON)
 * public MyDataSource myDataSource() {
 * return new MyDataSource();
 * }
 * }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Autowired
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * The name of the bean.
     * <p>If not specified (empty string), the name of the annotated method
     * will be used as the bean name.</p>
     * @return the bean name, or an empty string to use the method name.
     */
    String value() default "";

    /**
     * Specifies the lifecycle scope of the bean produced by this method.
     * <p>Defaults to {@link Component.Scope#SINGLETON}, meaning a single instance
     * is shared across the entire application context.</p>
     * @return the {@link Component.Scope} of the bean.
     */
    Component.Scope scope() default Component.Scope.SINGLETON;
}