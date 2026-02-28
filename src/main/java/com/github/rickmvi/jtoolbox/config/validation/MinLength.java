package com.github.rickmvi.jtoolbox.config.validation;

import java.lang.annotation.*;

/**
 * Valida comprimento mínimo de String.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface MinLength {
    int value();
    String message() default "Minimum length is {value}";
}