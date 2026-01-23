package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates delegation methods to a field.
 * <p>
 * Example:
 * @Delegate
 * private List<String> items = new ArrayList<>();
 *
 * Will generate: add(), remove(), size(), etc. delegating to items.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Delegate {
    /**
     * Types to delegate (if empty, uses field type).
     */
    Class<?>[] types() default {};

    /**
     * Methods to exclude from delegation.
     */
    String[] excludes() default {};
}
