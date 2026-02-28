package com.github.rickmvi.jtoolbox.config.validation;

import java.lang.annotation.*;

/**
 * Valida que o valor não é vazio (String, Collection, etc).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface NotEmpty {
    String message() default "Value cannot be empty";
}