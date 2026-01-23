package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates toString() method.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ToString {
    /**
     * Whether to include field names.
     */
    boolean includeFieldNames() default true;

    /**
     * Whether to call super.toString().
     */
    boolean callSuper() default false;

    /**
     * Fields to exclude from toString.
     */
    String[] exclude() default {};

    /**
     * Fields to include (if specified, only these will be included).
     */
    String[] of() default {};
}
