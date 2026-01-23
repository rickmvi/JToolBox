package com.github.rickmvi.jtoolbox.ioc.lifecycle;

/**
 * Enum representing the different phases in the lifecycle of an application context.
 * Each phase denotes a specific stage in the startup or shutdown process.
 */
public enum LifecyclePhase {

    /**
     * Initial phase - context created but not configured
     */
    NOT_STARTED,

    /**
     * Preparing Environment and Settings
     */
    PREPARING,

    /**
     * Loading bean definitions
     */
    LOADING_BEAN_DEFINITIONS,

    /**
     * Processing Postprocessors of Definitions
     */
    PROCESSING_BEAN_DEFINITIONS,

    /**
     * Instantiating beans
     */
    INSTANTIATING_BEANS,

    /**
     * Initializing beans (@PostConstruct)
     */
    INITIALIZING_BEANS,

    /**
     * Finalizing startup
     */
    REFRESHING,

    /**
     * Context fully initialized and ready
     */
    READY,

    /**
     * Context in the process of shutdown
     */
    SHUTTING_DOWN,

    /**
     * Closed context
     */
    CLOSED;

    public boolean isActive() {
        return this == READY;
    }

    public boolean isStarting() {
        return ordinal() > NOT_STARTED.ordinal() && ordinal() < READY.ordinal();
    }

    public boolean isClosing() {
        return this == SHUTTING_DOWN || this == CLOSED;
    }
}