package com.github.rickmvi.jtoolbox.config.core;

import com.github.rickmvi.jtoolbox.config.annotations.Profile;
import com.github.rickmvi.jtoolbox.config.exceptions.ConfigException;
import com.github.rickmvi.jtoolbox.dotenv.Dotenv;
import com.github.rickmvi.jtoolbox.dotenv.DotenvBuilder;
import com.github.rickmvi.jtoolbox.dotenv.DotenvEntry;

import java.lang.reflect.Method;
import java.util.*;
public class EnvEngine {

    private static Dotenv      dotenv;
    private static EnvInjector injector;
    private static String      activeProfile;

    public static void initialize() {
        initialize(".env", null);
    }

    public static void initialize(String filename) {
        initialize(filename, null);
    }

    public static void initialize(String filename, String profile) {
        activeProfile = profile != null ? profile : System.getProperty("env.profile", "dev");

        String envFile = filename;
        if (activeProfile != null && !activeProfile.isEmpty()) {
            envFile = "." + activeProfile + ".env";
        }

        try {
            dotenv = new DotenvBuilder()
                    .filename(envFile)
                    .ignoreIfMissing()
                    .load();

            injector = new EnvInjector(dotenv);

        } catch (Exception e) {
            throw new ConfigException("Failed to initialize EnvEngine", e);
        }
    }

    public static void inject(Object target) {
        ensureInitialized();

        Class<?> clazz = target.getClass();
        if (clazz.isAnnotationPresent(Profile.class)) {
            Profile profile = clazz.getAnnotation(Profile.class);
            if (!profile.value().equals(activeProfile)) {
                return;
            }
        }

        injector.inject(target);
    }

    public static void inject(Object target, Dotenv customDotenv) {
        EnvInjector customInjector = new EnvInjector(customDotenv);
        customInjector.inject(target);
    }

    public static Object invoke(Object target, String methodName, Object... additionalArgs) {
        ensureInitialized();

        try {
            Method method = findMethod(target.getClass(), methodName);
            if (method == null) {
                throw new ConfigException("Method not found: " + methodName);
            }

            Object[] args = resolveMethodParameters(method, additionalArgs);
            method.setAccessible(true);
            return method.invoke(target, args);

        } catch (Exception e) {
            throw new ConfigException("Failed to invoke method: " + methodName, e);
        }
    }

    private static Object[] resolveMethodParameters(Method method, Object[] additionalArgs) {
        // Implementação simplificada - pode ser expandida
        return additionalArgs;
    }

    private static Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static void reload() {
        ensureInitialized();

        String envFile = activeProfile != null ? "." + activeProfile + ".env" : ".env";
        dotenv = new DotenvBuilder()
                .filename(envFile)
                .ignoreIfMissing()
                .load();

        injector = new EnvInjector(dotenv);

        injector.reload();
    }

    public static String dump(Object target) {
        ensureInitialized();

        StringBuilder dump = new StringBuilder();
        dump.append("=== Configuration Dump ===\n");

        Class<?> clazz = target.getClass();

        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(com.github.rickmvi.jtoolbox.config.annotations.Env.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(target);
                        com.github.rickmvi.jtoolbox.config.annotations.Env env =
                                field.getAnnotation(com.github.rickmvi.jtoolbox.config.annotations.Env.class);
                        dump.append(env.value())
                                .append("=")
                                .append(value)
                                .append("\n");
                    } catch (IllegalAccessException e) {
                        // Ignora
                    }
                });

        return dump.toString();
    }

    public static Set<DotenvEntry> entries() {
        ensureInitialized();
        return dotenv.entries();
    }

    public static String get(String key) {
        ensureInitialized();
        return dotenv.get(key);
    }

    public static String get(String key, String defaultValue) {
        ensureInitialized();
        return dotenv.get(key, defaultValue);
    }

    public static String getActiveProfile() {
        return activeProfile;
    }

    public static void setActiveProfile(String profile) {
        activeProfile = profile;
        initialize(".env", profile);
    }

    private static void ensureInitialized() {
        if (dotenv == null) {
            initialize();
        }
    }

    public static Dotenv getDotenv() {
        ensureInitialized();
        return dotenv;
    }

    public static void reset() {
        dotenv = null;
        injector = null;
        activeProfile = null;
    }
}