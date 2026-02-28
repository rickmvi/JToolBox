package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Marca um campo, parâmetro ou método para injeção automática de variável de ambiente.
 *
 * <pre>
 * @Env("DB_HOST")
 * String host;
 *
 * @Env(value = "DB_PORT", defaultValue = "3306")
 * int port;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Env {
    /**
     * Nome da variável de ambiente
     */
    String value();

    /**
     * Valor padrão caso a variável não exista
     */
    String defaultValue() default "";
}
