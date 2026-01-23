package com.github.rickmvi.jtoolbox.ioc.context;

import com.github.rickmvi.jtoolbox.ioc.bean.BeanDefinition;
import com.github.rickmvi.jtoolbox.logger.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the registration and retrieval of bean definitions in an IoC container.
 * Provides capabilities to register, remove, and query bean definitions by name
 * or type, as well as utilities for indexing and clearing all definitions.
 */
public class BeanRegistry {

    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<String>> typeIndex = new ConcurrentHashMap<>();

    public void registerBeanDefinition(String beanName, BeanDefinition definition) {
        if (beanDefinitions.containsKey(beanName)) {
            Logger.warn("[REGISTRY] Bean already registered, replacing: {}", beanName);
        }

        beanDefinitions.put(beanName, definition);

        typeIndex.computeIfAbsent(definition.getBeanClass(), k -> new ArrayList<>())
                .add(beanName);

        Logger.debug("[REGISTRY] Registered bean: {} [{}]",
                beanName, definition.getBeanClass().getSimpleName());
    }

    public void removeBeanDefinition(String beanName) {
        BeanDefinition definition = beanDefinitions.remove(beanName);

        if (definition != null) {
            List<String> names = typeIndex.get(definition.getBeanClass());
            if (names != null) {
                names.remove(beanName);
            }
        }
    }

    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitions.get(beanName);
    }

    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitions.containsKey(beanName);
    }

    public List<String> getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();

        List<String> exact = typeIndex.get(type);
        if (exact != null) {
            result.addAll(exact);
        }

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())
                    && !result.contains(entry.getKey())) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    public Collection<BeanDefinition> getAllDefinitions() {
        return Collections.unmodifiableCollection(beanDefinitions.values());
    }

    public Set<String> getAllBeanNames() {
        return Collections.unmodifiableSet(beanDefinitions.keySet());
    }

    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    public void clear() {
        beanDefinitions.clear();
        typeIndex.clear();
    }
}