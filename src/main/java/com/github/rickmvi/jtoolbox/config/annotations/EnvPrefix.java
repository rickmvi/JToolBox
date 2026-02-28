package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Define um prefixo automático para todas as variáveis da classe.
 *
 * <pre>
 * @EnvPrefix("DB_")
 * public class DatabaseConfig {
 *     @Env("HOST") String host;  // DB_HOST
 *     @Env("PORT") int port;     // DB_PORT
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnvPrefix {
    /**
     * Prefixo a ser aplicado
     */
    String value();
}
