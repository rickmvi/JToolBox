package com.github.rickmvi.jtoolbox.ioc;

import com.github.rickmvi.jtoolbox.ioc.annotations.core.ComponentScan;
import com.github.rickmvi.jtoolbox.ioc.annotations.web.EnableWebServer;
import com.github.rickmvi.jtoolbox.ioc.context.ApplicationContext;
import com.github.rickmvi.jtoolbox.ioc.web.WebServerInitializer;
import com.github.rickmvi.jtoolbox.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the main application context for launching and managing the lifecycle
 * of a JToolbox-based application. This class is responsible for initializing
 * components, scanning for configurations, managing modules, and handling the
 * application startup and shutdown process.
 */
public class JToolboxApplication {

    private final Class<?>     primarySource;
    private final String[]     args;
    private ApplicationContext context;

    private JToolboxApplication(Class<?> primarySource, String[] args) {
        this.primarySource = primarySource;
        this.args          = args;
    }

    public static ApplicationContext run(Class<?> primarySource, String... args) {
        printBanner();

        JToolboxApplication runner = new JToolboxApplication(primarySource, args);

        try {
            return runner.run();
        } catch (Exception e) {
            Logger.error("[BOOTSTRAP] Application startup failed: {}", e.getMessage(), e);
            System.exit(1);
            return null;
        }
    }

    private ApplicationContext run() {
        Logger.info("[BOOTSTRAP] Starting JToolbox Application...");
        long startTime = System.currentTimeMillis();

        try {
            context = new ApplicationContext(args);

            scanComponents();

            context.refresh();

            initializeModules();

            registerShutdownHook();

            long duration = System.currentTimeMillis() - startTime;
            Logger.info("[BOOTSTRAP] Application started successfully in {}ms", duration);

            return context;

        } catch (Exception e) {
            Logger.error("[BOOTSTRAP] Startup failed: {}", e.getMessage(), e);

            if (context != null) {
                try {
                    context.close();
                } catch (Exception closeEx) {
                    Logger.error("[BOOTSTRAP] Error during cleanup: {}", closeEx.getMessage());
                }
            }

            throw new ApplicationStartupException("Application startup failed", e);
        }
    }

    private void scanComponents() {
        Logger.info("[BOOTSTRAP] Scanning components...");

        List<String> packagesToScan = new ArrayList<>();

        packagesToScan.add(primarySource.getPackageName());

        if (primarySource.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan scan = primarySource.getAnnotation(ComponentScan.class);

            if (scan.value().length > 0) {
                packagesToScan.clear();
                packagesToScan.addAll(Arrays.asList(scan.value()));
            } else if (scan.basePackages().length > 0) {
                packagesToScan.clear();
                packagesToScan.addAll(Arrays.asList(scan.basePackages()));
            }
        }

        context.scan(packagesToScan.toArray(new String[0]));
    }

    private void initializeModules() {
        Logger.info("[BOOTSTRAP] Initializing optional modules...");

        if (primarySource.isAnnotationPresent(EnableWebServer.class)) {
            initializeWebModule();
        }

        // Futuros módulos: Scheduling, Messaging, etc
    }

    private void initializeWebModule() {
        Logger.info("[WEB] Initializing web server...");

        try {
            EnableWebServer config = primarySource.getAnnotation(EnableWebServer.class);
            WebServerInitializer initializer = new WebServerInitializer(context, config);
            initializer.initialize();

            Logger.info("[WEB] Web server started on {}:{}", config.host(), config.port());
        } catch (Exception e) {
            Logger.error("[WEB] Failed to start web server: {}", e.getMessage(), e);
            throw new WebServerInitializationException("Web server initialization failed", e);
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.info("[BOOTSTRAP] Shutdown signal received");

            try {
                if (context != null) {
                    context.close();
                }
                Logger.info("[BOOTSTRAP] Application shutdown complete");
            } catch (Exception e) {
                Logger.error("[BOOTSTRAP] Error during shutdown: {}", e.getMessage(), e);
            }
        }, "shutdown-hook"));
    }

    private static void printBanner() {
        String banner = """
   
        __ ______               __  ____          
      /  //_  __/ ___  ____    / / / __ )____   _  __
 __  /  /  / /  / __ \\/ __ \\/ / / /_/ / __ \\| |/ /
/ /_/  /  / /  / /_/  / /_/  / / / /_/ / /_/  / > <  
\\____/  /_/  \\____/\\____/_/  /_____/\\____//_/|_|  
                                                 
   JToolbox Framework v1.0.0
   """;

        System.out.println(banner);
    }

    // ========== Exceptions ==========

    public static class ApplicationStartupException extends RuntimeException {
        public ApplicationStartupException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class WebServerInitializationException extends RuntimeException {
        public WebServerInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}