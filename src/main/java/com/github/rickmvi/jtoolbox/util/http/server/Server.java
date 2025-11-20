package com.github.rickmvi.jtoolbox.util.http.server;

import com.github.rickmvi.jtoolbox.collections.Dynamic;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.Try;
import com.github.rickmvi.jtoolbox.util.http.HttpContext;
import com.github.rickmvi.jtoolbox.util.http.Method;
import com.github.rickmvi.jtoolbox.util.http.handler.Handler;
import com.github.rickmvi.jtoolbox.util.http.route.Route;
import com.github.rickmvi.jtoolbox.util.http.status.HttpStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {

    private int port = 8080;
    private final Dynamic<Route> routes = Dynamic.empty();

    private HttpServer httpServer;

    public static @NotNull Server port(int port) {
        Server server = new Server();
        server.port = port;
        return server;
    }

    private void configureRoute(Method method, String path, Handler handler) {
        routes.add(new Route(method, path, handler));
    }

    public Server get(String path, Handler handler) {
        configureRoute(Method.GET, path, handler);
        return this;
    }

    public Server post(String path, Handler handler) {
        configureRoute(Method.POST, path, handler);
        return this;
    }

    public Server put(String path, Handler handler) {
        configureRoute(Method.PUT, path, handler);
        return this;
    }

    public Server delete(String path, Handler handler) {
        configureRoute(Method.DELETE, path, handler);
        return this;
    }

    public Server head(String path, Handler handler) {
        configureRoute(Method.HEAD, path, handler);
        return this;
    }

    public Server options(String path, Handler handler) {
        configureRoute(Method.OPTIONS, path, handler);
        return this;
    }

    public Server trace(String path, Handler handler) {
        configureRoute(Method.TRACE, path, handler);
        return this;
    }

    public Server connect(String path, Handler handler) {
        configureRoute(Method.CONNECT, path, handler);
        return this;
    }

    public Server patch(String path, Handler handler) {
        configureRoute(Method.PATCH, path, handler);
        return this;
    }

    public void start() {
        Logger.info("Starting Server...");
        Logger.debug("Initializing embedded JDK HTTP server...");

        this.httpServer = Try.ofThrowing(() -> HttpServer.create(new InetSocketAddress(port), 0)).orThrow();

        Logger.debug("Binding HTTP server to port {}", port);

        Logger.info("Configuring executor service (virtual threads)...");

        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        Logger.debug("Registering global request handler at '/'");
        httpServer.createContext("/", this::routeRequest);

        Logger.info("Starting HTTP server...");
        httpServer.start();

        Logger.info("Server started on **http://localhost:{}** (port: {})", port, port);

        Logger.info("Ready to serve requests.");

        Logger.info("Registered routes:");
        routes.forEach(r ->
                Logger.info("  -> [{}] {}", r.method(), r.path())
        );
    }

    public void stop() {
        if (httpServer != null) {
            Logger.info("Stopping **JToolbox** HTTP server...");

            httpServer.stop(0);

            Logger.info("HTTP server stopped.");
        }
    }

    private void routeRequest(@NotNull HttpExchange exchange) {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Logger.debug("Incoming request: {} {}", method, path);

        Route matchedRoute = routes.stream()
                .filter(r -> r.method().name().equalsIgnoreCase(method) && r.path().equals(path))
                .findFirst()
                .orElse(null);

        HttpContext ctx = new HttpContext(exchange);

        if (matchedRoute == null) {
            Logger.warn("No route found for {} {}", method, path);
            ctx.status(404, "Not Found: " + method + " " + path);
            Logger.debug("Responded with HTTP 404 for {} {}", method, path);
            return;
        }

        Logger.trace("Route matched: [{}] {}", matchedRoute.method(), matchedRoute.path());

        Try.run(() -> {
            matchedRoute.handler().handle(ctx);
            Logger.debug("Request handled successfully: {} {}", method, path);
        }).onFailure(e -> {
            Logger.error("Unhandled exception in route handler for {} {}: {}", e, method, path, e.getMessage());
            ctx.status(500, HttpStatus.INTERNAL_SERVER_ERROR);
            Logger.warn("Responded with HTTP 500 for {} {}", method, path);
        }).orThrow();
    }
}
