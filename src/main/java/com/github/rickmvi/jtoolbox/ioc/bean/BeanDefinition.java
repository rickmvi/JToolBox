package com.github.rickmvi.jtoolbox.ioc.bean;

import com.github.rickmvi.jtoolbox.ioc.annotations.Component;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the blueprint and metadata of a managed bean within the IoC container.
 * <p>
 * This class encapsulates all necessary information required to instantiate, configure,
 * and manage the lifecycle of a bean, including its class type, scope, dependencies,
 * initialization/destruction callbacks, and instantiation conditions.
 * </p>
 *
 * @author rickmvi
 * @since 1.0.0
 */
@Data
@Builder
public class BeanDefinition {

    /**
     * The unique identifier (canonical name) of the bean within the container.
     */
    private String name;

    /**
     * The actual {@link Class} type of the bean being managed.
     */
    private Class<?> beanClass;

    /**
     * The lifecycle scope of the bean (e.g., SINGLETON, PROTOTYPE).
     * Defines when a new instance is created versus returning an existing one.
     */
    private Component.Scope scope;

    /**
     * The specific constructor resolved to instantiate this bean.
     * Used for Constructor Injection.
     */
    private Constructor<?> constructor;

    /**
     * The factory method used to create the bean instance.
     * Typically used when the bean is defined via {@code @Bean} inside a configuration class.
     */
    private Method factoryMethod;

    /**
     * The instance of the configuration class that owns the {@link #factoryMethod}.
     * Required to invoke the factory method via reflection.
     */
    private Object factoryBean;

    /**
     * Indicates whether the bean should be initialized lazily.
     * If {@code true}, the bean is created only when requested, not at startup.
     */
    private boolean lazy;

    /**
     * Indicates if this bean is the primary candidate for autowiring when multiple
     * beans of the same type exist.
     */
    private boolean primary;

    /**
     * A qualifier string to distinguish beans of the same type during injection.
     * Corresponds to the {@code @Qualifier} annotation.
     */
    private String qualifier;

    /**
     * The initialization order value.
     * Lower values have higher priority. Used for sorting beans.
     */
    private int order;

    /**
     * List of methods annotated with {@code @PostConstruct}.
     * Executed immediately after dependency injection is complete.
     */
    @Builder.Default
    private List<Method> postConstructMethods = new ArrayList<>();

    /**
     * List of methods annotated with {@code @PreDestroy}.
     * Executed just before the bean is removed from the container (usually at shutdown).
     */
    @Builder.Default
    private List<Method> preDestroyMethods = new ArrayList<>();

    /**
     * List of raw dependency classes required by this bean.
     * Used for dependency resolution analysis and cycle detection.
     */
    @Builder.Default
    private List<Class<?>> dependencies = new ArrayList<>();

    /**
     * List of conditions that must be evaluated to true for this bean to be created.
     * Used for conditional bean registration (e.g., {@code @ConditionalOnProperty}).
     */
    @Builder.Default
    private List<BeanCondition> conditions = new ArrayList<>();

    /**
     * Internal flag indicating if the bean has already been successfully instantiated.
     */
    private boolean instantiated;

    /**
     * Caches the singleton instance of the bean.
     * Only populated if {@link #scope} is {@link Component.Scope#SINGLETON}.
     */
    private Object singletonInstance;

    /**
     * Functional interface representing a condition for bean creation.
     */
    public interface BeanCondition {
        /**
         * Evaluates the condition.
         *
         * @return {@code true} if the condition is met and the bean should be created; {@code false} otherwise.
         */
        boolean matches();

        /**
         * Returns a description of the condition for logging or debugging purposes.
         *
         * @return a description string.
         */
        String description();
    }

    /**
     * Checks if all registered conditions for this bean are satisfied.
     *
     * @return {@code true} if all conditions define in {@link #conditions} return true, or if the list is empty.
     */
    public boolean conditionsMatch() {
        return conditions.stream().allMatch(BeanCondition::matches);
    }

    /**
     * Convenience check to determine if the bean is a Singleton.
     *
     * @return {@code true} if the scope is {@link Component.Scope#SINGLETON}.
     */
    public boolean isSingleton() {
        return scope == Component.Scope.SINGLETON;
    }

    /**
     * Convenience check to determine if the bean is a Prototype.
     *
     * @return {@code true} if the scope is {@link Component.Scope#PROTOTYPE}.
     */
    public boolean isPrototype() {
        return scope == Component.Scope.PROTOTYPE;
    }
}