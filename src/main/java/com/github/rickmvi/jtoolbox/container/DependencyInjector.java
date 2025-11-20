package com.github.rickmvi.jtoolbox.container;

import com.github.rickmvi.jtoolbox.container.annotations.Inject;
import com.github.rickmvi.jtoolbox.container.annotations.PostConstruct;
import com.github.rickmvi.jtoolbox.container.annotations.Qualifier;
import com.github.rickmvi.jtoolbox.container.annotations.Service;
import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.text.Stringifier;
import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BooleanSupplier;

public class DependencyInjector {

    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Service> serviceAnnotations = new HashMap<>();
    private final Set<Class<?>> serviceClasses;


    public DependencyInjector(String basePackage) {
        this.serviceClasses = ReflectionUtil.scan(basePackage);

        for (Class<?> clazz : serviceClasses) {
            serviceAnnotations.put(clazz, clazz.getAnnotation(Service.class));
        }

        Try.runThrowing(this::init)
                .onFailure(e -> Logger.error("Error initialing constructor DependencyInjector", e, e.getMessage()))
                .orThrow();
    }

    private void init() {
        for (Class<?> clazz : serviceClasses) {
            Service serviceAnnotation = serviceAnnotations.get(clazz);
            if (serviceAnnotation.scope() == ScopeType.SINGLETON) {
                createAndRegister(clazz);
            }
        }

        for (Object instance : new ArrayList<>(instances.values())) {
            injectFields(instance);
            callPostConstruct(instance);
        }
    }

    private void createAndRegister(Class<?> clazz) {
        if (instances.containsKey(clazz)) return;

        Object instance = createInstance(clazz);
        instances.put(clazz, instance);
    }

    private Object createInstance(@NotNull Class<?> clazz) {
        Constructor<?> constructorToUse = Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("No constructors found for " + clazz));

        Class<?>[] paramTypes = constructorToUse.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Parameter parameter = constructorToUse.getParameters()[i];

            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);

            Optional<Object> dependency = Optional.ofNullable(resolveDependency(paramType, qualifier));

            exception(dependency::isEmpty,
                    "Unable to resolve dependency for {} in constructor of {}",
                    paramType,
                    clazz);

            if (dependency.isPresent())
                params[i] = dependency.get();
        }

        constructorToUse.setAccessible(true);
        return Try.ofThrowing(() -> constructorToUse.newInstance(params)).orThrow();
    }

    private void injectFields(@NotNull Object instance) {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            resolveAndInject(instance, field, clazz);
        }
    }

    private void resolveAndInject(@NotNull Object instance, @NotNull Field field, Class<?> clazz) {
        if (field.isAnnotationPresent(Inject.class)) {
            Class<?> fieldType = field.getType();
            Qualifier qualifier = field.getAnnotation(Qualifier.class);

            Optional<Object> dependency = Optional.ofNullable(resolveDependency(fieldType, qualifier));

            exception(dependency::isEmpty,
                    "No dependencies found for: {} in {}",
                    fieldType,
                    clazz);

            field.setAccessible(true);

            Try.runThrowing(() -> field.set(instance, dependency.get())).orThrow();
        }
    }

    private void callPostConstruct(@NotNull Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                exception(() -> method.getParameterCount() != 0,
                        "@PostConstruct must be parameterless: {} in {}",
                        method.getName(),
                        instance.getClass());

                method.setAccessible(true);
                Try.ofThrowing(() -> method.invoke(instance)).orThrow();
                return;
            }
        }
    }

    private void exception(@NotNull BooleanSupplier predicate, String message, Object... args) {
        If.ThrowWhen(predicate.getAsBoolean(), () -> new RuntimeException(Stringifier.format(message, args)));
    }

    private @Nullable Object resolveDependency(Class<?> type, Qualifier qualifier) {
        if (instances.containsKey(type)) return instances.get(type);

        Optional<Class<?>> definition = serviceClasses.stream()
                .filter(type::isAssignableFrom)
                .filter(c -> {
                    if (qualifier == null) return true;
                    Service serviceAnnotation = serviceAnnotations.get(c);
                    return serviceAnnotation != null && qualifier.value().equals(serviceAnnotation.value());
                })
                .findFirst();

        if (definition.isPresent()) {
            Class<?> clazz = definition.get();
            Service serviceAnnotation = serviceAnnotations.get(clazz);

            return getInstance(serviceAnnotation, clazz);
        }

        return null;
    }

    private @NotNull Object getInstance(@NotNull Service serviceAnnotation, Class<?> clazz) {
        if (serviceAnnotation.scope() != ScopeType.SINGLETON) {
            Object newInstance = createInstance(clazz);
            injectFields(newInstance);
            callPostConstruct(newInstance);
            return newInstance;
        }

        createAndRegister(clazz);
        Object instance = instances.get(clazz);
        injectFields(instance);
        callPostConstruct(instance);
        return instance;
    }

    public <T> T get(@NotNull Class<T> type) {
        Optional<Class<?>> definition = serviceClasses.stream()
                .filter(type::isAssignableFrom)
                .findFirst();

        if (definition.isPresent()) {
            Class<?> clazz = definition.get();
            Service serviceAnnotation = serviceAnnotations.get(clazz);

            Try.of(() -> {
                if (serviceAnnotation.scope() != ScopeType.SINGLETON) return type.cast(resolveDependency(clazz, null));
                return type.cast(resolveDependency(type, null));
            }).onFailure(e -> Logger.error("Error getting dependency: {}", e, type.getName())).orThrow();
        }

        return null;
    }

}
