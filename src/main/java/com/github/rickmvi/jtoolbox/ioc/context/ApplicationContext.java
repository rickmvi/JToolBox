package com.github.rickmvi.jtoolbox.ioc.context;

import com.github.rickmvi.jtoolbox.ioc.annotations.*;
import com.github.rickmvi.jtoolbox.ioc.bean.BeanDefinition;
import com.github.rickmvi.jtoolbox.ioc.bean.BeanDefinition.BeanCondition;
import com.github.rickmvi.jtoolbox.ioc.environment.Environment;
import com.github.rickmvi.jtoolbox.ioc.lifecycle.LifecyclePhase;
import com.github.rickmvi.jtoolbox.ioc.processor.BeanPostProcessor;
import com.github.rickmvi.jtoolbox.logger.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The ApplicationContext class is responsible for representing the core context of an application.
 * It manages the lifecycle of the application, processes configuration classes, and provides dependency injection capabilities.
 * It allows for bean definition scanning, processing, instantiation, initialization, and retrieval.
 */
public class ApplicationContext {

    private final Environment environment;
    private final BeanFactory beanFactory;
    private final BeanRegistry beanRegistry;
    private final List<BeanPostProcessor> beanPostProcessors;

    private LifecyclePhase currentPhase = LifecyclePhase.NOT_STARTED;
    private boolean started = false;

    public ApplicationContext(String[] args) {
        this.environment = new Environment(args);
        this.beanRegistry = new BeanRegistry();
        this.beanFactory = new BeanFactory(beanRegistry, environment);
        this.beanPostProcessors = new ArrayList<>();
    }

    // ========== Lifecycle Management ==========

    public void refresh() {
        if (started) {
            throw new IllegalStateException("ApplicationContext already started");
        }

        try {
            Logger.info("[CONTEXT] Starting ApplicationContext...");

            setPhase(LifecyclePhase.PREPARING);
            prepare();

            setPhase(LifecyclePhase.LOADING_BEAN_DEFINITIONS);
            loadBeanDefinitions();

            setPhase(LifecyclePhase.PROCESSING_BEAN_DEFINITIONS);
            processBeanDefinitions();

            setPhase(LifecyclePhase.INSTANTIATING_BEANS);
            instantiateBeans();

            setPhase(LifecyclePhase.INITIALIZING_BEANS);
            initializeBeans();

            setPhase(LifecyclePhase.REFRESHING);
            finishRefresh();

            setPhase(LifecyclePhase.READY);
            started = true;

            Logger.info("[CONTEXT] ApplicationContext started successfully with {} beans",
                    beanFactory.getSingletonCount());

        } catch (Exception e) {
            Logger.error("[CONTEXT] Failed to start ApplicationContext: {}", e.getMessage(), e);
            setPhase(LifecyclePhase.CLOSED);
            throw new ContextStartupException("Failed to start ApplicationContext", e);
        }
    }

    private void prepare() {
        Logger.debug("[CONTEXT] Preparing environment and configuration");
        environment.prepare();

        registerInternalBeanPostProcessors();
    }

    private void loadBeanDefinitions() {
        Logger.debug("[CONTEXT] Loading bean definitions");
        // Definições já foram registradas via scan
        // Esta fase permite extensões futuras
    }

    private void processBeanDefinitions() {
        Logger.debug("[CONTEXT] Processing bean definitions");
        // Aqui futuramente poderiam rodar BeanDefinitionPostProcessors
        validateBeanDefinitions();
    }

    private void instantiateBeans() {
        Logger.debug("[CONTEXT] Instantiating singleton beans");

        List<BeanDefinition> definitions = beanRegistry.getAllDefinitions().stream()
                .filter(def -> !def.isLazy() && def.isSingleton())
                .sorted(Comparator.comparingInt(BeanDefinition::getOrder))
                .collect(Collectors.toList());

        for (BeanDefinition definition : definitions) {
            try {
                beanFactory.getBean(definition.getName());
            } catch (Exception e) {
                Logger.error("[CONTEXT] Failed to instantiate bean: {}", definition.getName(), e);
                throw new BeanCreationException("Failed to instantiate bean: " + definition.getName(), e);
            }
        }
    }

    private void initializeBeans() {
        Logger.debug("[CONTEXT] Initializing beans");
        // Beans já foram inicializados durante a criação
        // Esta fase permite extensões futuras
    }

    private void finishRefresh() {
        Logger.debug("[CONTEXT] Finishing context refresh");
        publishContextRefreshedEvent();
    }

    private void publishContextRefreshedEvent() {
        // Futura implementação de eventos
    }

    // ========== Component Scanning ==========

    public void scan(String... basePackages) {
        ensurePhase(LifecyclePhase.NOT_STARTED, LifecyclePhase.PREPARING);

        Logger.info("[SCAN] Scanning packages: {}", Arrays.toString(basePackages));

        for (String basePackage : basePackages) {
            Set<Class<?>> classes = PackageScanner.scan(basePackage);

            for (Class<?> clazz : classes) {
                processClass(clazz);
            }
        }

        Logger.info("[SCAN] Found {} bean definitions", beanRegistry.getBeanDefinitionCount());
    }

    private void processClass(Class<?> clazz) {
        Component component = findComponentAnnotation(clazz);
        if (component == null) return;

        String beanName = getBeanName(clazz, component.value());

        BeanDefinition.BeanDefinitionBuilder builder = BeanDefinition.builder()
                .name(beanName)
                .beanClass(clazz)
                .scope(component.scope())
                .lazy(clazz.isAnnotationPresent(Lazy.class))
                .primary(clazz.isAnnotationPresent(Primary.class))
                .order(clazz.isAnnotationPresent(Order.class) ?
                        clazz.getAnnotation(Order.class).value() : 0);

        if (clazz.isAnnotationPresent(Qualifier.class)) {
            builder.qualifier(clazz.getAnnotation(Qualifier.class).value());
        }

        builder.constructor(findConstructor(clazz));

        // Lifecycle methods
        List<Method> postConstruct = new ArrayList<>();
        List<Method> preDestroy = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                postConstruct.add(method);
            }
            if (method.isAnnotationPresent(PreDestroy.class)) {
                preDestroy.add(method);
            }
        }

        builder.postConstructMethods(postConstruct);
        builder.preDestroyMethods(preDestroy);

        processConditions(clazz, builder);

        BeanDefinition definition = builder.build();
        beanRegistry.registerBeanDefinition(beanName, definition);

        // Processa @Bean methods
        if (clazz.isAnnotationPresent(Configuration.class)) {
            processBeanMethods(clazz);
        }
    }

    private void processBeanMethods(Class<?> configClass) {
        for (Method method : configClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Bean.class)) continue;

            Bean bean = method.getAnnotation(Bean.class);
            String beanName = bean.value().isEmpty() ? method.getName() : bean.value();

            BeanDefinition definition = BeanDefinition.builder()
                    .name(beanName)
                    .beanClass(method.getReturnType())
                    .scope(bean.scope())
                    .factoryMethod(method)
                    .lazy(method.isAnnotationPresent(Lazy.class))
                    .build();

            beanRegistry.registerBeanDefinition(beanName, definition);
        }
    }

    // ========== Bean Retrieval ==========

    public <T> T getBean(String name) {
        ensureActive();
        return beanFactory.getBean(name);
    }

    public <T> T getBean(Class<T> type) {
        ensureActive();
        return beanFactory.getBean(type);
    }

    public <T> T getBean(Class<T> type, String qualifier) {
        ensureActive();
        return beanFactory.getBean(type, qualifier);
    }

    public <T> List<T> getBeans(Class<T> type) {
        ensureActive();
        return beanFactory.getBeans(type);
    }

    public boolean containsBean(String name) {
        return beanRegistry.containsBeanDefinition(name);
    }

    // ========== BeanPostProcessor Management ==========

    public void addBeanPostProcessor(BeanPostProcessor processor) {
        beanPostProcessors.add(processor);
        beanPostProcessors.sort(Comparator.comparingInt(BeanPostProcessor::getOrder));
    }

    private void registerInternalBeanPostProcessors() {
        // Registra post-processors padrão
    }

    List<BeanPostProcessor> getBeanPostProcessors() {
        return Collections.unmodifiableList(beanPostProcessors);
    }

    // ========== Environment Access ==========

    public Environment getEnvironment() {
        return environment;
    }

    // ========== Shutdown ==========

    public void close() {
        if (currentPhase == LifecyclePhase.CLOSED) {
            return;
        }

        setPhase(LifecyclePhase.SHUTTING_DOWN);
        Logger.info("[CONTEXT] Shutting down ApplicationContext...");

        beanFactory.destroySingletons();

        setPhase(LifecyclePhase.CLOSED);
        started = false;

        Logger.info("[CONTEXT] ApplicationContext shut down complete");
    }

    // ========== Lifecycle State ==========

    private void setPhase(LifecyclePhase phase) {
        this.currentPhase = phase;
        Logger.trace("[CONTEXT] Phase: {}", phase);
    }

    private void ensurePhase(LifecyclePhase... allowedPhases) {
        for (LifecyclePhase phase : allowedPhases) {
            if (currentPhase == phase) {
                return;
            }
        }
        throw new IllegalStateException(
                "Operation not allowed in phase " + currentPhase +
                        ", expected one of: " + Arrays.toString(allowedPhases)
        );
    }

    private void ensureActive() {
        if (!currentPhase.isActive()) {
            throw new IllegalStateException("ApplicationContext is not active: " + currentPhase);
        }
    }

    public LifecyclePhase getCurrentPhase() {
        return currentPhase;
    }

    public boolean isActive() {
        return currentPhase.isActive();
    }

    // ========== Validation ==========

    private void validateBeanDefinitions() {
        // Valida dependências circulares, beans faltando, etc
        // Implementação futura
    }

    // ========== Utility Methods ==========

    private @Nullable Component findComponentAnnotation(@NotNull Class<?> clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            return clazz.getAnnotation(Component.class);
        }

        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Component.class)) {
                return annotation.annotationType().getAnnotation(Component.class);
            }
        }

        return null;
    }

    private @NotNull String getBeanName(Class<?> clazz, @NotNull String value) {
        if (!value.isEmpty()) {
            return value;
        }

        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private Constructor<?> findConstructor(@NotNull Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        }

        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException(
                    "No suitable constructor found for " + clazz.getName()
            );
        }
    }

    private void processConditions(@NotNull Class<?> clazz, BeanDefinition.BeanDefinitionBuilder builder) {
        List<BeanCondition> conditions = new ArrayList<>();

        if (clazz.isAnnotationPresent(ConditionalOnClass.class)) {
            ConditionalOnClass condition = clazz.getAnnotation(ConditionalOnClass.class);
            conditions.add(new ClassCondition(condition.value()));
        }

        if (clazz.isAnnotationPresent(ConditionalOnProperty.class)) {
            ConditionalOnProperty condition = clazz.getAnnotation(ConditionalOnProperty.class);
            conditions.add(new PropertyCondition(
                    environment, condition.value(), condition.havingValue()
            ));
        }

        builder.conditions(conditions);
    }

    // ========== Conditions ==========

    private static class ClassCondition implements BeanCondition {
        private final Class<?>[] classes;

        ClassCondition(Class<?>[] classes) {
            this.classes = classes;
        }

        @Override
        public boolean matches() {
            for (Class<?> clazz : classes) {
                try {
                    Class.forName(clazz.getName());
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
            return true;
        }

        @Contract(pure = true)
        @Override
        public @NotNull String description() {
            return "ConditionalOnClass: " + Arrays.toString(classes);
        }
    }

    private static class PropertyCondition implements BeanCondition {
        private final Environment environment;
        private final String propertyName;
        private final String expectedValue;

        PropertyCondition(Environment environment, String propertyName, String expectedValue) {
            this.environment = environment;
            this.propertyName = propertyName;
            this.expectedValue = expectedValue;
        }

        @Override
        public boolean matches() {
            String value = environment.getProperty(propertyName);
            if (value == null) return false;
            if (expectedValue.isEmpty()) return true;
            return expectedValue.equals(value);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String description() {
            return "ConditionalOnProperty: " + propertyName +
                    (expectedValue.isEmpty() ? "" : "=" + expectedValue);
        }
    }

    // ========== Exceptions ==========

    public static class ContextStartupException extends RuntimeException {
        public ContextStartupException(String message, Throwable cause) {
            super(message, cause);
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