package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import com.github.rickmvi.jtoolbox.annotation.processor.AccessLevel;
import java.lang.annotation.*;

/**
 * Generates a constructor that accepts one argument for every field in the class.
 *
 * <p>When applied to a class, the JToolbox Annotation Processor will generate
 * a constructor that initializes all declared fields in the exact order
 * they appear in the source code.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Comprehensive Initialization:</strong> Useful for ensuring that no field
 * is left uninitialized, particularly mandatory {@code final} fields.</li>
 * <li><strong>Static Factory Support:</strong> If a {@code staticName} is provided,
 * the constructor becomes private, and a public static factory method is created instead.</li>
 * <li><strong>IoC Integration:</strong> Provides a clear entry point for
 * Constructor Injection within the JToolbox container.</li>
 * </ul>
 *
 * <p><strong>Example:</strong></p>
 * <pre>
 * &#64;AllArgsConstructor(staticName = "of")
 * public class Product {
 * private final String id;
 * private String name;
 * private double price;
 * }
 * // Usage: Product p = Product.of("P100", "Laptop", 1500.0);
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AllArgsConstructor {
    /**
     * The visibility of the generated constructor.
     * Defaults to {@link AccessLevel#PUBLIC}.
     */
    AccessLevel value() default AccessLevel.PUBLIC;

    /**
     * If specified, the generated constructor will be private and a static
     * factory method with this name will be generated.
     * @return the name of the static factory method.
     */
    String staticName() default "";
}