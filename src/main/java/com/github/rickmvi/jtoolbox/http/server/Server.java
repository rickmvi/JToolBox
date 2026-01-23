package com.github.rickmvi.jtoolbox.http.server;

import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.Try;
import com.github.rickmvi.jtoolbox.http.HttpContext;
import com.github.rickmvi.jtoolbox.http.Method;
import com.github.rickmvi.jtoolbox.http.handler.Handler;
import com.github.rickmvi.jtoolbox.http.middleware.Middleware;
import com.github.rickmvi.jtoolbox.http.route.Route;
import com.github.rickmvi.jtoolbox.http.route.RoutePattern;
import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * The {@code Server} class provides an abstraction for creating and managing an HTTP server.
 * It supports request routing, middleware usage, CORS configuration, and custom error handlers.
 * The server can be customized through builder-like methods and used to register routes for different HTTP methods.
 */
public class Server {

    private int port = 8080;
    private String host = "0.0.0.0";
    private final List<Route> routes = new ArrayList<>();
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private HttpServer httpServer;

    // Configurações
    private Handler notFoundHandler = this::defaultNotFoundHandler;
    private Handler errorHandler = this::defaultErrorHandler;
    private boolean corsEnabled = false;
    private String corsOrigin = "*";
    private int backlog = 0;

    // ========== Builder Methods ==========

    public static @NotNull Server create() {
        return new Server();
    }

    public static @NotNull Server port(int port) {
        Server server = new Server();
        server.port = port;
        return server;
    }

    public Server host(String host) {
        this.host = host;
        return this;
    }

    public Server backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    // ========== Middleware ==========

    public Server use(Middleware middleware) {
        globalMiddlewares.add(middleware);
        return this;
    }

    public Server use(Handler handler) {
        return use((ctx, next) -> {
            handler.handle(ctx);
            next.run();
        });
    }

    // ========== CORS ==========

    public Server enableCors() {
        return enableCors("*");
    }

    public Server enableCors(String origin) {
        this.corsEnabled = true;
        this.corsOrigin = origin;
        return this;
    }

    // ========== Error Handlers ==========

    public Server onNotFound(Handler handler) {
        this.notFoundHandler = handler;
        return this;
    }

    public Server onError(Handler handler) {
        this.errorHandler = handler;
        return this;
    }

    // ========== Route Registration ==========

    public Server route(Method method, String path, Handler handler, Middleware... middlewares) {
        RoutePattern pattern = new RoutePattern(path);
        Route route = new Route(method, pattern, handler, List.of(middlewares));
        routes.add(route);
        Logger.debug("Registered route: {} {}", method, path);
        return this;
    }

    public Server get(String path, Handler handler, Middleware... middlewares) {
        return route(Method.GET, path, handler, middlewares);
    }

    public Server post(String path, Handler handler, Middleware... middlewares) {
        return route(Method.POST, path, handler, middlewares);
    }

    public Server put(String path, Handler handler, Middleware... middlewares) {
        return route(Method.PUT, path, handler, middlewares);
    }

    public Server delete(String path, Handler handler, Middleware... middlewares) {
        return route(Method.DELETE, path, handler, middlewares);
    }

    public Server patch(String path, Handler handler, Middleware... middlewares) {
        return route(Method.PATCH, path, handler, middlewares);
    }

    public Server options(String path, Handler handler, Middleware... middlewares) {
        return route(Method.OPTIONS, path, handler, middlewares);
    }

    // ========== Route Groups ==========

    public Server group(String prefix, Consumer<RouteGroup> configurer) {
        RouteGroup group = new RouteGroup(this, prefix);
        configurer.accept(group);
        return this;
    }

    // ========== Static Files (placeholder para futuro) ==========

    public Server staticFiles(String urlPath, String localPath) {
        Logger.warn("Static file serving not yet implemented");
        // TODO: implementar servidor de arquivos estáticos
        return this;
    }

    // ========== Server Lifecycle ==========

    public void start() {
        Logger.info("Starting JToolbox HTTP Server on {}:{}", host, port);

        this.httpServer = Try.ofThrowing(() ->
                HttpServer.create(new InetSocketAddress(host, port), backlog)
        ).orThrow();

        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        httpServer.createContext("/", this::dispatch);

        httpServer.start();

        Logger.info("Server is running at **http://{}:{}**", host, port);
        Logger.debug("Registered {} routes, {} global middlewares", routes.size(), globalMiddlewares.size());
    }

    public void stop() {
        stop(1);
    }

    public void stop(int delay) {
        if (httpServer != null) {
            httpServer.stop(delay);
            Logger.info("Server stopped.");
        }
    }

    // ========== Request Dispatcher ==========

    private void dispatch(@NotNull HttpExchange exchange) {
        try (HttpContext ctx = new HttpContext(exchange)) {
            String path = exchange.getRequestURI().getPath();
            String methodStr = exchange.getRequestMethod();

            Logger.trace("[{}] {} via {}", methodStr, path, ctx.protocol());

            // CORS preflight
            if (corsEnabled && "OPTIONS".equals(methodStr)) {
                handleCorsPreFlight(ctx);
                return;
            }

            // Apply CORS headers
            if (corsEnabled) {
                ctx.cors(corsOrigin);
            }

            // Find matching route
            Route matchedRoute = findRoute(methodStr, path);

            if (matchedRoute == null) {
                executeHandler(ctx, notFoundHandler);
                return;
            }

            // Extract path parameters
            Map<String, String> pathParams = matchedRoute.pattern().match(path);
            ctx.setPathParams(pathParams);

            // Execute middleware chain
            executeMiddlewareChain(ctx, matchedRoute);

        } catch (Exception e) {
            Logger.error("Unexpected error in dispatcher: {}", e.getMessage(), e);
        }
    }

    private Route findRoute(String method, String path) {
        for (Route route : routes) {
            if (route.method().method().equalsIgnoreCase(method) && route.pattern().matches(path)) {
                return route;
            }
        }
        return null;
    }

    private void executeMiddlewareChain(HttpContext ctx, Route route) {
        List<Middleware> allMiddlewares = new ArrayList<>(globalMiddlewares);
        allMiddlewares.addAll(route.middlewares());

        executeMiddleware(ctx, allMiddlewares, 0, () -> {
            // Após todos middlewares, executa o handler
            executeHandler(ctx, route.handler());
        });
    }

    private void executeMiddleware(HttpContext ctx, List<Middleware> middlewares,
                                   int index, Runnable finalHandler) {
        if (index >= middlewares.size()) {
            finalHandler.run();
            return;
        }

        Middleware current = middlewares.get(index);

        Try.runThrowing(() -> current.handle(ctx, () -> {
            executeMiddleware(ctx, middlewares, index + 1, finalHandler);
        })).onFailure(e -> {
            Logger.error("Middleware error: {}", e.getMessage(), e);
            ctx.set("error", e);
            executeHandler(ctx, errorHandler);
        });
    }

    private void executeHandler(HttpContext ctx, Handler handler) {
        Try.run(() -> handler.handle(ctx))
                .onFailure(e -> {
                    Logger.error("Handler error: {}", e.getMessage(), e);
                    ctx.set("error", e);
                    executeHandler(ctx, errorHandler);
                });
    }

    private void handleCorsPreFlight(HttpContext ctx) {
        ctx.cors(corsOrigin);
        ctx.noContent();
    }

    private void defaultNotFoundHandler(HttpContext ctx) {
        ctx.status(StatusCode.NOT_FOUND, "Not Found: " + ctx.path());
    }

    private void defaultErrorHandler(HttpContext ctx) {
        Exception error = ctx.get("error");
        String message = error != null ? error.getMessage() : "Internal Server Error";
        ctx.status(StatusCode.INTERNAL_SERVER_ERROR, message);
    }

    // ========== Route Group Helper ==========

    public static class RouteGroup {
        private final Server server;
        private final String prefix;
        private final List<Middleware> groupMiddlewares = new ArrayList<>();

        RouteGroup(Server server, String prefix) {
            this.server = server;
            this.prefix = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
        }

        public RouteGroup use(Middleware middleware) {
            groupMiddlewares.add(middleware);
            return this;
        }

        public RouteGroup get(String path, Handler handler) {
            return route(Method.GET, path, handler);
        }

        public RouteGroup post(String path, Handler handler) {
            return route(Method.POST, path, handler);
        }

        public RouteGroup put(String path, Handler handler) {
            return route(Method.PUT, path, handler);
        }

        public RouteGroup delete(String path, Handler handler) {
            return route(Method.DELETE, path, handler);
        }

        public RouteGroup route(Method method, String path, Handler handler) {
            String fullPath = prefix + path;
            Middleware[] mws = groupMiddlewares.toArray(new Middleware[0]);
            server.route(method, fullPath, handler, mws);
            return this;
        }
    }
}