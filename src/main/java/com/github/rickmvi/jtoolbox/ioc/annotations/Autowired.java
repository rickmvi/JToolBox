package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Marks a constructor, field, or parameter as to be autowired by JToolbox's
 * dependency injection facilities.
 * <p>This is the primary annotation used to express dependency requirements
 * within the IoC container. The container will attempt to resolve and
 * inject a matching bean for the annotated element.</p>
 * <p>Usage examples:</p>
 * <pre>
 * // Field Injection
 * &#64;Autowired
 * private MyService myService;
 * // Constructor Injection
 * &#64;Autowired
 * public MyController(MyService service) { ... }
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * Declares whether the annotated dependency is required.
     * * <p>Defaults to {@code true}. If set to {@code true}, the IoC container
     * will throw an exception if a matching bean cannot be resolved.
     * If {@code false}, the container will skip injection or inject
     * {@code null} if the dependency is missing.</p>
     * * @return {@code true} if the dependency is mandatory, {@code false} otherwise.
     */
    boolean required() default true;
}