package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates an immutable value class with all fields final.
 * Includes: AllArgsConstructor, Getter, ToString, EqualsAndHashCode.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Value {
    /**
     * Static factory method name for constructor.
     */
    String staticConstructor() default "";
}
