package com.github.rickmvi.jtoolbox.config.validation;

import java.lang.annotation.*;

/**
 * Valida que o valor corresponde a uma expressão regular.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Regex {
    String value();
    String message() default "Value does not match pattern {value}";
}