package com.github.rickmvi.jtoolbox.ioc.processor;

/**
 * Extension point para processar beans durante o ciclo de vida.
 * Permite interceptar beans antes e depois da inicialização.
 *
 * Base para futuras features como AOP, proxies, validação, etc.
 */
public interface BeanPostProcessor {

    /**
     * Processes the bean instance before its initialization. This provides an extension point
     * to intercept and apply custom logic before the initialization of a bean by the container.
     *
     * @param bean the bean instance that is about to be initialized
     * @param beanName the name of the bean in the container
     * @return the processed bean instance (can be the original instance or a modified one)
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Processes the bean instance after its initialization. This provides an extension point
     * to intercept and apply custom logic after the initialization of a bean by the container.
     *
     * @param bean the fully initialized bean instance to be processed
     * @param beanName the name of the bean in the container
     * @return the processed bean instance (can be the original instance or a wrapped/proxied one)
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Determines the order of execution for the processor.
     *
     * @return the order value, where a lower value indicates higher priority
     */
    default int getOrder() {
        return 0;
    }
}

class LoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Logger.trace("Bean initialized: {} [{}]", beanName, bean.getClass().getSimpleName());
        return bean;
    }
}

class ValidationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // It could validate annotations @NotNull, @Valid, etc
        return bean;
    }
}