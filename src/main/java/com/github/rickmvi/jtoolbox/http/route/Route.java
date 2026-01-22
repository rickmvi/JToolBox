package com.github.rickmvi.jtoolbox.http.route;

import com.github.rickmvi.jtoolbox.http.Method;
import com.github.rickmvi.jtoolbox.http.handler.Handler;
import com.github.rickmvi.jtoolbox.http.middleware.Middleware;

import java.util.Collections;
import java.util.List;

public record Route(
        Method method,
        RoutePattern pattern,
        Handler handler,
        List<Middleware> middlewares
) {
    public Route {
        middlewares = middlewares != null ? List.copyOf(middlewares) : Collections.emptyList();
    }

    public Route(Method method, RoutePattern pattern, Handler handler) {
        this(method, pattern, handler, Collections.emptyList());
    }
}