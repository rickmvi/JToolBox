package com.github.rickmvi.jtoolbox.ioc.environment;

import com.github.rickmvi.jtoolbox.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Gerencia configurações, propriedades, profiles e variáveis de ambiente.
 * Centraliza toda a configuração externa da aplicação.
 */
public class Environment {

    private final Properties properties = new Properties();
    private final Set<String> activeProfiles = new LinkedHashSet<>();
    private final String[] commandLineArgs;

    public Environment(String[] args) {
        this.commandLineArgs = args != null ? args : new String[0];
    }

    // ========== Lifecycle ==========

    public void prepare() {
        Logger.info("[ENVIRONMENT] Preparing environment...");

        // 1. Carrega application.properties padrão
        loadDefaultProperties();

        // 2. Detecta profiles ativos
        detectActiveProfiles();

        // 3. Carrega properties específicos do profile
        loadProfileProperties();

        // 4. Sobrescreve com variáveis de ambiente
        loadEnvironmentVariables();

        // 5. Sobrescreve com argumentos da linha de comando
        loadCommandLineArguments();

        Logger.info("[ENVIRONMENT] Active profiles: {}", activeProfiles.isEmpty() ? "default" : activeProfiles);
        Logger.debug("[ENVIRONMENT] Loaded {} properties", properties.size());
    }

    // ========== Property Loading ==========

    private void loadDefaultProperties() {
        loadPropertiesFromResource("application.properties");
    }

    private void detectActiveProfiles() {
        // Profile da propriedade
        String profilesProp = getProperty("jtoolbox.profiles.active");
        if (profilesProp != null) {
            activeProfiles.addAll(Arrays.asList(profilesProp.split(",")));
        }

        // Profile da variável de ambiente
        String profilesEnv = System.getenv("JTOOLBOX_PROFILES_ACTIVE");
        if (profilesEnv != null) {
            activeProfiles.addAll(Arrays.asList(profilesEnv.split(",")));
        }

        // Se nenhum profile, usa 'default'
        if (activeProfiles.isEmpty()) {
            activeProfiles.add("default");
        }
    }

    private void loadProfileProperties() {
        for (String profile : activeProfiles) {
            if (!"default".equals(profile)) {
                loadPropertiesFromResource("application-" + profile + ".properties");
            }
        }
    }

    private void loadPropertiesFromResource(String resourceName) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (is != null) {
                properties.load(is);
                Logger.debug("[ENVIRONMENT] Loaded: {}", resourceName);
            }
        } catch (IOException e) {
            Logger.debug("[ENVIRONMENT] Could not load: {}", resourceName);
        }
    }

    private void loadEnvironmentVariables() {
        // Converte variáveis de ambiente para properties
        // Ex: JTOOLBOX_SERVER_PORT -> jtoolbox.server.port
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("JTOOLBOX_")) {
                String propKey = key.toLowerCase().replace("_", ".");
                properties.setProperty(propKey, value);
            }
        });
    }

    private void loadCommandLineArguments() {
        for (String arg : commandLineArgs) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    properties.setProperty(parts[0], parts[1]);
                    Logger.debug("[ENVIRONMENT] Command line override: {}={}", parts[0], parts[1]);
                }
            }
        }
    }

    // ========== Property Access ==========

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Required property not found: " + key);
        }
        return value;
    }

    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getPropertyAsInt(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            Logger.warn("[ENVIRONMENT] Invalid integer value for {}: {}", key, value);
            return defaultValue;
        }
    }

    public long getPropertyAsLong(String key, long defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            Logger.warn("[ENVIRONMENT] Invalid long value for {}: {}", key, value);
            return defaultValue;
        }
    }

    /**
     * Resolve placeholders ${property.name:defaultValue}
     */
    public String resolvePlaceholders(String text) {
        if (text == null || !text.contains("${")) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int start = 0;

        while (true) {
            int placeholderStart = text.indexOf("${", start);
            if (placeholderStart == -1) {
                result.append(text.substring(start));
                break;
            }

            int placeholderEnd = text.indexOf("}", placeholderStart);
            if (placeholderEnd == -1) {
                result.append(text.substring(start));
                break;
            }

            result.append(text, start, placeholderStart);

            String placeholder = text.substring(placeholderStart + 2, placeholderEnd);
            String[] parts = placeholder.split(":", 2);
            String key = parts[0];
            String defaultValue = parts.length > 1 ? parts[1] : "";

            String value = getProperty(key, defaultValue);
            result.append(value);

            start = placeholderEnd + 1;
        }

        return result.toString();
    }

    // ========== Profiles ==========

    public boolean acceptsProfiles(String... profiles) {
        for (String profile : profiles) {
            if (activeProfiles.contains(profile)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getActiveProfiles() {
        return Collections.unmodifiableSet(activeProfiles);
    }

    // ========== System Properties ==========

    public String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public String getSystemProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    public String getEnv(String key) {
        return System.getenv(key);
    }

    public String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    // ========== Utility ==========

    public Properties getAllProperties() {
        return new Properties(properties);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}