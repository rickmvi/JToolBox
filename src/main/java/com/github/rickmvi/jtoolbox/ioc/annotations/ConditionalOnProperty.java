package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * {@link ConditionalOnProperty} annotation that checks if a specific configuration property
 * is present and, optionally, if it matches a specific value.
 *
 * <p>The annotated component or {@link Bean} method will only be registered
 * if the property defined in {@link #value()} exists in the environment
 * and satisfies the condition.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // This bean will only be created if 'app.feature.enabled' is set to 'true'
 * &#64;Bean
 * &#64;ConditionalOnProperty(value = "app.feature.enabled", havingValue = "true")
 * public FeatureService featureService() {
 * return new FeatureService();
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Bean
 * @see Component
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnProperty {

    /**
     * The name of the property to check.
     * <p>The IoC container will look up this key in the application's
     * configuration sources (e.g., properties file, environment variables).</p>
     * @return the property key to test.
     */
    String value();

    /**
     * The string representation of the expected value for the property.
     * <p>If provided, the property value must match this string for the condition
     * to pass. If left as an empty string (default), the condition matches
     * as long as the property exists, regardless of its value.</p>
     * @return the expected value of the property.
     */
    String havingValue() default "";
}