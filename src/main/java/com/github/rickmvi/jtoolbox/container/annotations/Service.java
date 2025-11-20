package com.github.rickmvi.jtoolbox.container.annotations;

import com.github.rickmvi.jtoolbox.container.ScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
    ScopeType scope() default ScopeType.SINGLETON;
}
