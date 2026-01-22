package com.github.rickmvi.jtoolbox.http.middleware;

import com.github.rickmvi.jtoolbox.http.HttpContext;
import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.github.rickmvi.jtoolbox.logger.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Coleção de middlewares prontos para uso comum
 */
public class Middlewares {

    private Middlewares() {}

    // ========== Logging ==========

    /**
     * Middleware que loga todas as requests
     */
    public static Middleware logging() {
        return (ctx, next) -> {
            Instant start = Instant.now();
            Logger.info("[{}] {}", ctx.method(), ctx.path());

            next.run();

            Duration duration = Duration.between(start, Instant.now());
            Logger.debug("Request completed in {}ms", duration.toMillis());
        };
    }

    /**
     * Middleware de logging detalhado
     */
    public static Middleware verboseLogging() {
        return (ctx, next) -> {
            Instant start = Instant.now();

            Logger.info("=== Request ===");
            Logger.info("Method: {}", ctx.method());
            Logger.info("Path: {}", ctx.path());
            Logger.info("Query: {}", ctx.queryParams());
            Logger.info("Headers: {}", ctx.requestHeaders());

            if (!ctx.body().isEmpty()) {
                Logger.debug("Body: {}", ctx.body().substring(0, Math.min(100, ctx.body().length())));
            }

            next.run();

            Duration duration = Duration.between(start, Instant.now());
            Logger.info("Completed in {}ms", duration.toMillis());
        };
    }

    // ========== Authentication ==========

    /**
     * Middleware que requer token Bearer
     */
    public static Middleware requireAuth() {
        return (ctx, next) -> {
            String auth = ctx.header("Authorization");

            if (auth == null || !auth.startsWith("Bearer ")) {
                ctx.status(StatusCode.UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            String token = auth.substring(7);
            ctx.set("token", token);

            next.run();
        };
    }

    /**
     * Middleware que valida token específico
     */
    public static Middleware requireToken(String expectedToken) {
        return (ctx, next) -> {
            String auth = ctx.header("Authorization");

            if (auth == null || !auth.startsWith("Bearer ")) {
                ctx.status(StatusCode.UNAUTHORIZED, "Missing Authorization header");
                return;
            }

            String token = auth.substring(7);

            if (!expectedToken.equals(token)) {
                ctx.status(StatusCode.FORBIDDEN, "Invalid token");
                return;
            }

            next.run();
        };
    }

    // ========== Content Type Validation ==========

    /**
     * Middleware que requer Content-Type específico
     */
    public static Middleware requireContentType(String... allowedTypes) {
        Set<String> allowed = Set.of(allowedTypes);

        return (ctx, next) -> {
            String contentType = ctx.contentType();

            if (contentType == null) {
                ctx.status(StatusCode.BAD_REQUEST, "Missing Content-Type header");
                return;
            }

            // Remove charset e outras opções
            String baseType = contentType.split(";")[0].trim();

            if (!allowed.contains(baseType)) {
                ctx.status(StatusCode.UNSUPPORTED_MEDIA_TYPE,
                        "Content-Type must be one of: " + String.join(", ", allowedTypes));
                return;
            }

            next.run();
        };
    }

    /**
     * Middleware que requer JSON
     */
    public static Middleware requireJson() {
        return requireContentType("application/json");
    }

    // ========== Method Validation ==========

    /**
     * Middleware que permite apenas métodos específicos
     */
    public static Middleware allowMethods(String... methods) {
        Set<String> allowed = new HashSet<>();
        for (String m : methods) {
            allowed.add(m.toUpperCase());
        }

        return (ctx, next) -> {
            if (!allowed.contains(ctx.method().toUpperCase())) {
                ctx.status(StatusCode.METHOD_NOT_ALLOWED,
                        "Method " + ctx.method() + " not allowed");
                return;
            }
            next.run();
        };
    }

    // ========== Request Validation ==========

    /**
     * Middleware que valida presença de query parameters
     */
    public static Middleware requireQueryParams(String... paramNames) {
        return (ctx, next) -> {
            for (String param : paramNames) {
                if (ctx.query(param) == null) {
                    ctx.status(StatusCode.BAD_REQUEST, "Missing required query parameter: " + param);
                    return;
                }
            }
            next.run();
        };
    }

    /**
     * Middleware que valida presença de headers
     */
    public static Middleware requireHeaders(String... headerNames) {
        return (ctx, next) -> {
            for (String header : headerNames) {
                if (ctx.header(header) == null) {
                    ctx.status(StatusCode.BAD_REQUEST, "Missing required header: " + header);
                    return;
                }
            }
            next.run();
        };
    }

    /**
     * Middleware que valida body não-vazio
     */
    public static Middleware requireBody() {
        return (ctx, next) -> {
            if (ctx.body().isEmpty()) {
                ctx.status(StatusCode.BAD_REQUEST, "Request body is required");
                return;
            }
            next.run();
        };
    }

    // ========== Response Headers ==========

    /**
     * Middleware que adiciona headers de segurança
     */
    public static Middleware securityHeaders() {
        return (ctx, next) -> {
            ctx.header("X-Content-Type-Options", "nosniff");
            ctx.header("X-Frame-Options", "DENY");
            ctx.header("X-XSS-Protection", "1; mode=block");
            ctx.header("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            next.run();
        };
    }

    /**
     * Middleware que adiciona header customizado
     */
    public static Middleware addHeader(String name, String value) {
        return (ctx, next) -> {
            ctx.header(name, value);
            next.run();
        };
    }

    // ========== Rate Limiting (simples, em memória) ==========

    /**
     * Rate limiter básico (não recomendado para produção)
     */
    public static Middleware rateLimit(int maxRequests, Duration window) {
        // Implementação simplificada - em produção use Redis ou similar
        return (ctx, next) -> {
            // TODO: implementar rate limiting robusto
            Logger.warn("Rate limiting not fully implemented");
            next.run();
        };
    }

    // ========== Timeout ==========

    /**
     * Middleware de timeout (experimental)
     */
    public static Middleware timeout(Duration maxDuration) {
        return (ctx, next) -> {
            // Java virtual threads fazem isso ser mais complexo
            // Para implementação completa, considere CompletableFuture
            next.run();
        };
    }

    // ========== Error Handling ==========

    /**
     * Middleware que captura exceções e retorna erro formatado
     */
    public static Middleware errorHandler() {
        return (ctx, next) -> {
            try {
                next.run();
            } catch (IllegalArgumentException e) {
                ctx.status(StatusCode.BAD_REQUEST, e.getMessage());
            } catch (SecurityException e) {
                ctx.status(StatusCode.FORBIDDEN, e.getMessage());
            } catch (Exception e) {
                Logger.error("Unhandled error: {}", e.getMessage(), e);
                ctx.status(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error");
            }
        };
    }
}