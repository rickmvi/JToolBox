package com.github.rickmvi.jtoolbox.config.validation;

import java.lang.annotation.*;

/**
 * Valida valor mínimo para números.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Min {
    long value();
    String message() default "Value must be greater than or equal to {value}";
}
