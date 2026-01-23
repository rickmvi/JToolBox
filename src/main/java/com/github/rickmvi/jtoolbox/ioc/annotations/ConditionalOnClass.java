package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * {@link ConditionalOnClass} annotation that checks for the presence of specific classes
 * on the application's classpath.
 * <p>The annotated component or {@link Bean} method will only be registered in the
 * IoC container if all specified classes in {@link #value()} are loadable.</p>
 * <p>This is commonly used in framework modules to provide optional integration
 * with third-party libraries (e.g., only loading a Jackson bean if the Jackson
 * library is detected).</p>
 * <pre>
 * &#64;Bean
 * &#64;ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper.class")
 * public MyJsonProcessor jsonProcessor() {
 * return new MyJsonProcessor();
 * }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnClass {

    /**
     * The classes that must be present in the classpath for the condition to match.
     * <p>The condition is satisfied only if <b>all</b> listed classes are found.
     * If any class is missing, the annotated bean definition will be ignored.</p>
     * @return the array of required classes.
     */
    Class<?>[] value();
}