package com.github.rickmvi.jtoolbox.container;

import com.github.rickmvi.jtoolbox.container.annotations.Service;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;

@ApiStatus.Internal
public interface ReflectionUtil {

    static Set<Class<?>> scan(String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        return reflections.getTypesAnnotatedWith(Service.class);
    }

}
