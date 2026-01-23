package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates equals() and hashCode() methods.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface EqualsAndHashCode {
    /**
     * Whether to call super.equals() and super.hashCode().
     */
    boolean callSuper() default false;

    /**
     * Fields to exclude from equals/hashCode.
     */
    String[] exclude() default {};

    /**
     * Fields to include (if specified, only these will be included).
     */
    String[] of() default {};
}