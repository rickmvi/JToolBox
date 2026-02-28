package com.github.rickmvi.jtoolbox.config.examples;

import com.github.rickmvi.jtoolbox.config.annotations.*;
import com.github.rickmvi.jtoolbox.config.core.EnvEngine;
import com.github.rickmvi.jtoolbox.config.core.EnvProxy;
import com.github.rickmvi.jtoolbox.config.validation.*;

import java.util.List;

/**
 * Exemplos de uso do sistema de configuração
 */
public class UsageExamples {

    // ========================================================================
    // EXEMPLO 1: Injeção Simples em Classe
    // ========================================================================

    public static class SimpleConfig {

        @Env("DB_HOST")
        String host;

        @Env(value = "DB_PORT", defaultValue = "3306")
        int port;

        @Env("DEBUG")
        boolean debug;

        public void print() {
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            System.out.println("Debug: " + debug);
        }
    }

    public static void simpleExample() {
        EnvEngine.initialize();

        SimpleConfig config = new SimpleConfig();
        EnvEngine.inject(config);

        config.print();
    }

    // ========================================================================
    // EXEMPLO 2: Usando @EnvGroup
    // ========================================================================

    @EnvGroup("REDIS")
    public static class RedisConfig {
        String HOST;
        int    PORT;
        int    TIMEOUT;
    }

    public static void groupExample() {
        // .env deve conter:
        // REDIS_HOST=localhost
        // REDIS_PORT=6379
        // REDIS_TIMEOUT=5000

        EnvEngine.initialize();
        RedisConfig config = new RedisConfig();
        EnvEngine.inject(config);
    }

    // ========================================================================
    // EXEMPLO 3: Usando @EnvPrefix
    // ========================================================================

    @EnvPrefix("DB_")
    public static class DatabaseConfig {

        @Env("HOST")
        String host;

        @Env("PORT")
        int port;

        @Env("USER")
        @Required
        String user;

        @Env("PASS")
        @Required
        String password;
    }

    public static void prefixExample() {
        // .env deve conter:
        // DB_HOST=localhost
        // DB_PORT=5432
        // DB_USER=admin
        // DB_PASS=secret

        EnvEngine.initialize();
        DatabaseConfig config = new DatabaseConfig();
        EnvEngine.inject(config);
    }

    // ========================================================================
    // EXEMPLO 4: Validações
    // ========================================================================

    public static class ValidatedConfig {

        @Env("JWT_SECRET")
        @NotEmpty
        @MinLength(32)
        String jwtSecret;

        @Env("MAX_CONNECTIONS")
        @Min(1)
        @Max(1000)
        int maxConnections;

        @Env("EMAIL")
        @Regex("^[A-Za-z0-9+_.-]+@(.+)$")
        String email;
    }

    public static void validationExample() {
        EnvEngine.initialize();
        ValidatedConfig config = new ValidatedConfig();

        try {
            EnvEngine.inject(config);
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }

    // ========================================================================
    // EXEMPLO 5: Interface com EnvProxy (Mais Elegante)
    // ========================================================================

    public interface AppConfig {

        @Env("DB_HOST")
        String dbHost();

        @Env(value = "DB_PORT", defaultValue = "3306")
        int dbPort();

        @Env("DEBUG")
        boolean debugMode();

        @Env("ALLOWED_IPS")
        List<String> allowedIps();
    }

    public static void proxyExample() {
        EnvEngine.initialize();

        // Cria proxy
        AppConfig config = EnvProxy.create(AppConfig.class);

        // Usa como interface normal
        System.out.println("Host: " + config.dbHost());
        System.out.println("Port: " + config.dbPort());
        System.out.println("Debug: " + config.debugMode());
        System.out.println("IPs: " + config.allowedIps());
    }

    // ========================================================================
    // EXEMPLO 6: Profiles
    // ========================================================================

    @Profile("prod")
    public static class ProductionConfig {

        @Env("DB_HOST")
        String host;

        @Env("DB_SSL")
        boolean ssl;
    }

    public static void profileExample() {
        // Inicializa com perfil
        EnvEngine.initialize(".env", "prod");

        // Ou via propriedade do sistema
        System.setProperty("env.profile", "prod");
        EnvEngine.initialize();

        ProductionConfig config = new ProductionConfig();
        EnvEngine.inject(config);
    }

    // ========================================================================
    // EXEMPLO 7: Reload Dinâmico
    // ========================================================================

    public static class ReloadableConfig {

        @Env("FEATURE_FLAG")
        @Reloadable
        boolean featureEnabled;

        @Env("CACHE_TTL")
        @Reloadable
        int cacheTtl;
    }

    public static void reloadExample() {
        EnvEngine.initialize();

        ReloadableConfig config = new ReloadableConfig();
        EnvEngine.inject(config);

        System.out.println("Before: " + config.featureEnabled);

        // ... arquivo .env é modificado externamente ...

        // Recarrega
        EnvEngine.reload();

        System.out.println("After: " + config.featureEnabled);
    }

    // ========================================================================
    // EXEMPLO 8: Tipos Complexos
    // ========================================================================

    public static class ComplexTypesConfig {

        @Env("ALLOWED_IPS")
        List<String> allowedIps;  // "127.0.0.1,192.168.0.1"

        @Env("LOG_LEVEL")
        LogLevel logLevel;  // Enum

        @Env("TIMEOUT")
        java.time.Duration timeout;  // "PT30S"

        @Env("DATA_DIR")
        java.nio.file.Path dataDir;
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    public static void complexTypesExample() {
        EnvEngine.initialize();

        ComplexTypesConfig config = new ComplexTypesConfig();
        EnvEngine.inject(config);

        System.out.println("IPs: " + config.allowedIps);
        System.out.println("Log Level: " + config.logLevel);
        System.out.println("Timeout: " + config.timeout);
        System.out.println("Data Dir: " + config.dataDir);
    }

    // ========================================================================
    // EXEMPLO 9: Dump de Configuração
    // ========================================================================

    public static void dumpExample() {
        EnvEngine.initialize();

        SimpleConfig config = new SimpleConfig();
        EnvEngine.inject(config);

        // Imprime todas as configurações
        String dump = EnvEngine.dump(config);
        System.out.println(dump);
    }

    // ========================================================================
    // EXEMPLO 10: Uso Completo
    // ========================================================================

    @EnvPrefix("APP_")
    public static class FullConfig {

        @Env("NAME")
        @NotEmpty
        String name;

        @Env(value = "PORT", defaultValue = "8080")
        @Min(1024)
        @Max(65535)
        int port;

        @Env("ENABLE_CORS")
        @Reloadable
        boolean enableCors;

        @Env("ALLOWED_ORIGINS")
        List<String> allowedOrigins;

        @Env("JWT_SECRET")
        @Required
        @MinLength(32)
        @Decrypt(algorithm = "AES")
        String jwtSecret;
    }

    public static void fullExample() {
        // .env deve conter:
        // APP_NAME=MyApp
        // APP_PORT=8080
        // APP_ENABLE_CORS=true
        // APP_ALLOWED_ORIGINS=http://localhost:3000,http://example.com
        // APP_JWT_SECRET=encrypted_secret_here

        EnvEngine.initialize();

        FullConfig config = new FullConfig();
        EnvEngine.inject(config);

        System.out.println(EnvEngine.dump(config));
    }

    // ========================================================================
    // MAIN - Executa todos os exemplos
    // ========================================================================

    public static void main(String[] args) {
        System.out.println("=== Example 1: Simple ===");
        simpleExample();

        System.out.println("\n=== Example 2: Group ===");
        groupExample();

        System.out.println("\n=== Example 3: Prefix ===");
        prefixExample();

        System.out.println("\n=== Example 5: Proxy ===");
        proxyExample();

        System.out.println("\n=== Example 8: Complex Types ===");
        complexTypesExample();

        System.out.println("\n=== Example 9: Dump ===");
        dumpExample();
    }
}