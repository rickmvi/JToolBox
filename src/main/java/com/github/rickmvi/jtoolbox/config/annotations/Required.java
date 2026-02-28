package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Marca uma variável como obrigatória.
 *
 * <pre>
 * @Env("API_KEY")
 * @Required
 * String apiKey;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Required {
    /**
     * Mensagem customizada de erro
     */
    String message() default "";
}