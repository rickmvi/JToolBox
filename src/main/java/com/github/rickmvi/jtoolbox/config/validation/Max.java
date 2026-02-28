package com.github.rickmvi.jtoolbox.config.validation;

import java.lang.annotation.*;

/**
 * Valida valor máximo para números.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Max {
    long value();
    String message() default "Value must be less than or equal to {value}";
}