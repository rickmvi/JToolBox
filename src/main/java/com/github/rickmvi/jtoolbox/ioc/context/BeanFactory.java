package com.github.rickmvi.jtoolbox.ioc.context;

import com.github.rickmvi.jtoolbox.ioc.annotations.Autowired;
import com.github.rickmvi.jtoolbox.ioc.annotations.Inject;
import com.github.rickmvi.jtoolbox.ioc.annotations.Qualifier;
import com.github.rickmvi.jtoolbox.ioc.annotations.Value;
import com.github.rickmvi.jtoolbox.ioc.bean.BeanDefinition;
import com.github.rickmvi.jtoolbox.ioc.environment.Environment;
import com.github.rickmvi.jtoolbox.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The BeanFactory class is responsible for managing the lifecycle of beans and resolving their dependencies in a
 * dependency injection context. This class enables the creation, retrieval, and injection of beans based on the
 * provided configuration, such as singleton or prototype scopes, and supports resolving beans by type or qualifiers.
 */
public class BeanFactory {

    private final BeanRegistry        registry;
    private final Environment         environment;
    private final Map<String, Object> singletonBeans    = new ConcurrentHashMap<>();
    private final Set<String>         currentlyCreating = ConcurrentHashMap.newKeySet();

    public BeanFactory(BeanRegistry registry, Environment environment) {
        this.registry    = registry;
        this.environment = environment;
    }

    // ========== Bean Creation ==========

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        BeanDefinition definition = registry.getBeanDefinition(name);

        if (definition == null) {
            throw new BeanNotFoundException("Bean not found: " + name);
        }

        // Singleton
        if (definition.isSingleton()) {
            return (T) singletonBeans.computeIfAbsent(name, k -> createBean(definition));
        }

        // Prototype
        return (T) createBean(definition);
    }

    public <T> T getBean(Class<T> type) {
        List<String> candidates = registry.getBeanNamesForType(type);

        if (candidates.isEmpty()) {
            throw new BeanNotFoundException("No bean found for type: " + type.getName());
        }

        if (candidates.size() > 1) {
            // Busca @Primary
            Optional<String> primary = candidates.stream()
                    .filter(name -> registry.getBeanDefinition(name).isPrimary())
                    .findFirst();

            if (primary.isPresent()) {
                return getBean(primary.get());
            }

            throw new BeanNotFoundException(
                    "Multiple beans found for type " + type.getName() + ": " + candidates +
                            ". Use @Primary or @Qualifier to resolve ambiguity."
            );
        }

        return getBean(candidates.getFirst());
    }

    public <T> T getBean(Class<T> type, String qualifier) {
        List<String> candidates = registry.getBeanNamesForType(type);

        Optional<String> match = candidates.stream()
                .filter(name -> {
                    BeanDefinition def = registry.getBeanDefinition(name);
                    return qualifier.equals(def.getQualifier()) || qualifier.equals(name);
                })
                .findFirst();

        return match.map(this::<T>getBean)
                .orElseThrow(() -> new BeanNotFoundException(
                        "No bean found for type " + type.getName() +
                                " with qualifier: " + qualifier
                ));
    }

    public <T> List<T> getBeans(Class<T> type) {
        return registry.getBeanNamesForType(type).stream()
                .map(this::<T>getBean)
                .collect(Collectors.toList());
    }

    private @NotNull Object createBean(@NotNull BeanDefinition definition) {
        String beanName = definition.getName();

        // Detecta dependência circular
        if (currentlyCreating.contains(beanName)) {
            throw new BeanCreationException(
                    "Circular dependency detected for bean: " + beanName
            );
        }

        // Verifica condições
        if (!definition.conditionsMatch()) {
            throw new BeanCreationException(
                    "Conditions not met for bean: " + beanName
            );
        }

        currentlyCreating.add(beanName);

        try {
            Logger.trace("[FACTORY] Creating bean: {}", beanName);

            Object instance;

            // Cria via factory method ou construtor
            if (definition.getFactoryMethod() != null) {
                instance = createBeanFromFactory(definition);
            } else {
                instance = createBeanFromConstructor(definition);
            }

            // Injeta dependências
            injectDependencies(instance);

            // Executa @PostConstruct
            executePostConstruct(instance, definition);

            Logger.trace("[FACTORY] Bean created: {} [{}]",
                    beanName, instance.getClass().getSimpleName());

            return instance;

        } catch (Exception e) {
            Logger.error("[FACTORY] Failed to create bean {}: {}", beanName, e.getMessage());
            throw new BeanCreationException("Failed to create bean: " + beanName, e);
        } finally {
            currentlyCreating.remove(beanName);
        }
    }

    private @NotNull Object createBeanFromConstructor(@NotNull BeanDefinition definition) throws Exception {
        Constructor<?> constructor = definition.getConstructor();
        constructor.setAccessible(true);

        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveParameter(parameters[i]);
        }

        return constructor.newInstance(args);
    }

    private Object createBeanFromFactory(@NotNull BeanDefinition definition) throws Exception {
        Method method = definition.getFactoryMethod();
        method.setAccessible(true);

        // Obtém instância da classe @Configuration
        Object configBean = getBean(method.getDeclaringClass());

        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveParameter(parameters[i]);
        }

        return method.invoke(configBean, args);
    }

    // ========== Dependency Injection ==========

    private void injectDependencies(@NotNull Object instance) throws Exception {
        Class<?> clazz = instance.getClass();

        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(Inject.class) ||
                    field.isAnnotationPresent(Autowired.class)) {
                injectField(instance, field);
            } else if (field.isAnnotationPresent(Value.class)) {
                injectValue(instance, field);
            }
        }
    }

    private void injectField(Object instance, @NotNull Field field) throws Exception {
        field.setAccessible(true);

        Inject    inject    = field.getAnnotation(Inject.class);
        Autowired autowired = field.getAnnotation(Autowired.class);
        Qualifier qualifier = field.getAnnotation(Qualifier.class);

        boolean required = inject != null ? inject.required() :
                (autowired != null && autowired.required());

        try {
            Object dependency;

            if (qualifier != null) {
                dependency = getBean(field.getType(), qualifier.value());
            } else if (inject != null && !inject.value().isEmpty()) {
                dependency = getBean(inject.value());
            } else {
                dependency = getBean(field.getType());
            }

            field.set(instance, dependency);

        } catch (BeanNotFoundException e) {
            if (required) {
                throw e;
            }
            Logger.debug("[FACTORY] Optional dependency not found for field: {}.{}",
                    instance.getClass().getSimpleName(), field.getName());
        }
    }

    private void injectValue(Object instance, @NotNull Field field) throws Exception {
        field.setAccessible(true);

        Value value = field.getAnnotation(Value.class);
        String expression = value.value();

        String resolved = environment.resolvePlaceholders(expression);
        Object converted = convertValue(resolved, field.getType());

        field.set(instance, converted);
    }

    private Object resolveParameter(@NotNull Parameter parameter) {
        if (parameter.isAnnotationPresent(Value.class)) {
            Value value = parameter.getAnnotation(Value.class);
            String resolved = environment.resolvePlaceholders(value.value());
            return convertValue(resolved, parameter.getType());
        }

        Qualifier qualifier = parameter.getAnnotation(Qualifier.class);

        if (qualifier != null) {
            return getBean(parameter.getType(), qualifier.value());
        }

        return getBean(parameter.getType());
    }

    // ========== Lifecycle ==========

    private void executePostConstruct(Object bean, BeanDefinition definition) {
        for (Method method : definition.getPostConstructMethods()) {
            try {
                method.setAccessible(true);
                method.invoke(bean);
            } catch (Exception e) {
                Logger.error("[FACTORY] Error in @PostConstruct for {}: {}",
                        definition.getName(), e.getMessage());
            }
        }
    }

    public void destroySingletons() {
        Logger.debug("[FACTORY] Destroying singleton beans");

        for (Map.Entry<String, Object> entry : singletonBeans.entrySet()) {
            BeanDefinition definition = registry.getBeanDefinition(entry.getKey());
            Object bean = entry.getValue();

            if (definition != null) {
                for (Method method : definition.getPreDestroyMethods()) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                    } catch (Exception e) {
                        Logger.error("[FACTORY] Error in @PreDestroy for {}: {}",
                                entry.getKey(), e.getMessage());
                    }
                }
            }
        }

        singletonBeans.clear();
    }

    // ========== Utilities ==========

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class)                                 return value;
        if (targetType == int.class     || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class    || targetType == Long.class)    return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class  || targetType == Double.class)  return Double.parseDouble(value);
        if (targetType == float.class   || targetType == Float.class)   return Float.parseFloat(value);

        throw new IllegalArgumentException("Unsupported type conversion: " + targetType);
    }

    public int getSingletonCount() {
        return singletonBeans.size();
    }

    // ========== Exceptions ==========

    public static class BeanNotFoundException extends RuntimeException {
        public BeanNotFoundException(String message) {
            super(message);
        }
    }

    public static class BeanCreationException extends RuntimeException {
        public BeanCreationException(String message) {
            super(message);
        }
        public BeanCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}