package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicates whether a bean should be initialized lazily.
 * <p>Can be used on any class directly or indirectly annotated with {@link Component}
 * or on methods annotated with {@link Bean}.</p>
 * <p>When used on a {@code @Component} or {@code @Bean} definition, the proxy or
 * instance will not be created until it is first requested by the application
 * (e.g., via dependency injection into another bean or an explicit
 * {@code getBean()} call).</p>
 * <p>Benefits of using {@code @Lazy}:</p>
 * <ul>
 * <li>Decreased application startup time.</li>
 * <li>Reduced memory footprint for rarely used components.</li>
 * <li>Breaking circular dependencies in some specific scenarios.</li>
 * </ul>
 * <p>Example usage:</p>
 * <pre>
 * &#64;Lazy
 * &#64;Component
 * public class HeavyService {
 * public HeavyService() {
 * // Complex and slow initialization
 * }
 * }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

    /**
     * Whether lazy initialization should occur.
     * <p>Defaults to {@code true}. Setting this to {@code false} on a component
     * within a context that defaults to lazy initialization will force the
     * bean to be initialized eagerly at startup.</p>
     * @return {@code true} if lazy initialization is enabled, {@code false} otherwise.
     */
    boolean value() default true;
}