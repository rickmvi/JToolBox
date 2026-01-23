package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that an annotated class is a "component".
 * Such classes are considered as candidates for auto-detection when using
 * annotation-based configuration and classpath scanning.
 *
 * <p>This is the base stereotype for any managed bean in JToolbox. Other
 * specialized stereotypes like {@code @Service}, {@code @Repository}, and
 * {@code @RestController} are built upon this annotation.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;Component(value = "myComponent", scope = Component.Scope.PROTOTYPE)
 * public class MyTaskProcessor {
 * // ...
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Service
 * @see Repository
 * @see RestController
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * The value may indicate a suggestion for a logical component name (ID).
     * <p>If not specified, the JToolbox container will generate a default name
     * based on the simple class name (e.g., "OrderService" becomes "orderService").</p>
     *
     * @return the suggested bean name, if any.
     */
    String value() default "";

    /**
     * Specifies the scope of the bean instance.
     * <p>The scope determines how the container manages the lifecycle and
     * visibility of the bean instances.</p>
     *
     * @return the {@link Scope} of the component. Default is {@link Scope#SINGLETON}.
     */
    Scope scope() default Scope.SINGLETON;

    /**
     * Defines the strategies for bean instantiation and lifecycle management.
     */
    enum Scope {
        /**
         * Scopes a single bean definition to a single object instance per
         * JToolbox IoC container. This is the default scope.
         */
        SINGLETON,

        /**
         * Scopes a single bean definition to any number of object instances.
         * A new instance is created every time the bean is requested.
         */
        PROTOTYPE,

        /**
         * Scopes a single bean definition to the lifecycle of a single HTTP request.
         * Only valid in the context of a web-aware JToolbox application.
         */
        REQUEST
    }
}