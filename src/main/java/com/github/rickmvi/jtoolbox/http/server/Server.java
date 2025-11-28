package com.github.rickmvi.jtoolbox.http.server;

import com.github.rickmvi.jtoolbox.collections.Dynamic;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.Try;
import com.github.rickmvi.jtoolbox.http.HttpContext;
import com.github.rickmvi.jtoolbox.http.Method;
import com.github.rickmvi.jtoolbox.http.handler.Handler;
import com.github.rickmvi.jtoolbox.http.route.Route;
import com.github.rickmvi.jtoolbox.http.status.StatusCode;
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

    public Server route(Method method, String path, Handler handler) {
        routes.add(new Route(method, path, handler));
        return this;
    }

    public Server get(String path, Handler handler) {
        return route(Method.GET, path, handler);
    }

    public Server post(String path, Handler handler) {
        return route(Method.POST, path, handler);
    }

    public Server put(String path, Handler handler) {
        return route(Method.PUT, path, handler);
    }

    public Server delete(String path, Handler handler) {
        return route(Method.DELETE, path, handler);
    }

    public void start() {
        Logger.info("Starting JToolbox HTTP Server on port " + port);

        this.httpServer = Try.ofThrowing(() -> HttpServer.create(new InetSocketAddress(port), 0)).orThrow();

        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        httpServer.createContext("/", this::dispatch);

        httpServer.start();

        Logger.info("Server is running at **http://localhost:{}**", port);
        Logger.debug("Registered {} routes.", routes.size());
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(1);
            Logger.info("Server stopped.");
        }
    }

    private void dispatch(@NotNull HttpExchange exchange) {
        try (HttpContext ctx = new HttpContext(exchange)) {
            String path = exchange.getRequestURI().getPath();
            String methodStr = exchange.getRequestMethod();

            Logger.trace("[{}] {} via {}", methodStr, path, ctx.protocol());

            Route match = routes.stream()
                    .filter(r -> r.path().equals(path) && r.method().method().equalsIgnoreCase(methodStr))
                    .findFirst()
                    .orElse(null);

            if (match == null) {
                ctx.status(StatusCode.NOT_FOUND, "Not Found");
                return;
            }

            Try.run(() -> match.handler().handle(ctx))
                    .onFailure(e -> {
                        Logger.error("Handler Error: " + e.getMessage(), e);
                        ctx.status(StatusCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
                    });
        }
    }
}
