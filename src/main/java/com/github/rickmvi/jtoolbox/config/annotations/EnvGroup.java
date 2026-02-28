package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Agrupa variáveis por prefixo comum.
 *
 * <pre>
 * @EnvGroup("REDIS")
 * public class RedisConfig {
 *     String HOST;  // REDIS_HOST
 *     int PORT;     // REDIS_PORT
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnvGroup {
    /**
     * Prefixo a ser aplicado a todas as variáveis
     */
    String value();

    /**
     * Separador entre prefixo e nome da variável
     */
    String separator() default "_";
}