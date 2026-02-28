package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Define o perfil de ambiente (dev, test, prod).
 *
 * <pre>
 * @Profile("prod")
 * @Env("DB_HOST")
 * String host;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Profile {
    /**
     * Nome do perfil (dev, test, prod, etc)
     */
    String value();
}