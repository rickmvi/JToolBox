package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Marca um campo para permitir reload dinâmico.
 *
 * <pre>
 * @Env("DEBUG")
 * @Reloadable
 * boolean debug;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Reloadable {
}