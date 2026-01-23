package com.github.rickmvi.jtoolbox.ioc.web;

import com.github.rickmvi.jtoolbox.http.HttpContext;
import com.github.rickmvi.jtoolbox.http.Method;
import com.github.rickmvi.jtoolbox.http.server.Server;
import com.github.rickmvi.jtoolbox.http.status.StatusCode;
import com.github.rickmvi.jtoolbox.ioc.annotations.*;
import com.github.rickmvi.jtoolbox.ioc.context.ApplicationContext;
import com.github.rickmvi.jtoolbox.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

/**
 * A configuration class for setting up a Web MVC framework. It scans the application context for controllers
 * annotated with {@code @RestController} and registers them along with their respective endpoints on the server.
 * This class processes annotations like {@code @GetMapping}, {@code @PostMapping}, {@code @PutMapping},
 * {@code @DeleteMapping}, and {@code @RequestMapping} to configure routing and HTTP methods.
 * <p>
 * The class resolves method parameters for controllers using annotations such as {@code @PathVariable},
 * {@code @RequestParam}, {@code @RequestBody}, and {@code @RequestHeader}. It also integrates dependency
 * injection for parameters by retrieving beans from the application context.
 * <p>
 * Additionally, this class handles automatic route registration and type conversion for method arguments
 * and request parameters, providing flexibility to controllers for handling HTTP requests effectively.
 */
public class WebMvcConfigurer {

    private final ApplicationContext context;
    private final Server server;

    public WebMvcConfigurer(ApplicationContext context, Server server) {
        this.context = context;
        this.server = server;
    }

    public void configure() {
        Logger.info("Configuring Web MVC...");

        List<Object> controllers = context.getBeans(Object.class).stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(RestController.class))
                .toList();

        for (Object controller : controllers) {
            registerController(controller);
        }

        Logger.info("Registered {} controllers", controllers.size());
    }

    private void registerController(@NotNull Object controller) {
        Class<?> clazz = controller.getClass();
        RestController annotation = clazz.getAnnotation(RestController.class);

        String basePath = annotation.value();
        if (!basePath.isEmpty() && !basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }

        Logger.debug("Registering controller: {} [{}]", clazz.getSimpleName(), basePath);

        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            registerEndpoint(controller, basePath, method);
        }
    }

    private void registerEndpoint(Object controller, String basePath, java.lang.reflect.Method method) {
        registerIfPresent(method, GetMapping.class,    GetMapping::value,    Method.GET,    controller, basePath);
        registerIfPresent(method, PostMapping.class,   PostMapping::value,   Method.POST,   controller, basePath);
        registerIfPresent(method, PutMapping.class,    PutMapping::value,    Method.PUT,    controller, basePath);
        registerIfPresent(method, DeleteMapping.class, DeleteMapping::value, Method.DELETE, controller, basePath);

        if (method.isAnnotationPresent(RequestMapping.class)) {
            handleGenericRequestMapping(controller, basePath, method);
        }
    }

    private <A extends Annotation> void registerIfPresent(
            java.lang.reflect.@NotNull Method javaMethod,
            Class<A> annotationType,
            Function<A, String> pathExtractor,
            Method httpMethod,
            Object controller,
            String basePath) {

        if (javaMethod.isAnnotationPresent(annotationType)) {
            A annotation = javaMethod.getAnnotation(annotationType);
            String relativePath = pathExtractor.apply(annotation);
            String fullPath = joinPaths(basePath, relativePath);
            registerRoute(httpMethod, fullPath, controller, javaMethod);
        }
    }

    private void handleGenericRequestMapping(Object controller, String basePath, java.lang.reflect.@NotNull Method method) {
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        String fullPath = joinPaths(basePath, mapping.value());

        if (mapping.method().length == 0) {
            registerRoute(Method.GET, fullPath, controller, method);
        } else {
            for (String methodName : mapping.method()) {
                Method httpMethod = Method.fromString(methodName);
                if (httpMethod != null) {
                    registerRoute(httpMethod, fullPath, controller, method);
                }
            }
        }
    }

    private String joinPaths(String base, String path) {
        if (path == null || path.isEmpty()) return base;
        String cleanBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String cleanPath = path.startsWith("/") ? path : "/" + path;
        return cleanBase + cleanPath;
    }

    private void registerRoute(Method httpMethod, String path, Object controller, java.lang.reflect.Method method) {
        Logger.debug("  {} {} -> {}", httpMethod, path, method.getName());

        server.route(httpMethod, path, ctx -> {
            try {
                Object[] args = resolveMethodParameters(ctx, method);

                method.setAccessible(true);
                Object result = method.invoke(controller, args);

                handleResponse(ctx, method, result);

            } catch (Exception e) {
                Logger.error("Error in controller method {}: {}", method.getName(), e.getMessage(), e);
                ctx.status(StatusCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        });
    }

    private Object @NotNull [] resolveMethodParameters(HttpContext ctx, java.lang.reflect.@NotNull Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveParameter(ctx, parameters[i]);
        }

        return args;
    }

    private Object resolveParameter(HttpContext ctx, @NotNull Parameter parameter) {
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            return resolvePathVariable(ctx, parameter);
        }

        if (parameter.isAnnotationPresent(RequestParam.class)) {
            return resolveRequestParam(ctx, parameter);
        }

        if (parameter.isAnnotationPresent(RequestBody.class)) {
            return ctx.body(parameter.getType());
        }

        if (parameter.isAnnotationPresent(RequestHeader.class)) {
            return resolveRequestHeader(ctx, parameter);
        }

        if (parameter.getType() == HttpContext.class) {
            return ctx;
        }

        return resolveBeanDependency(parameter);
    }

    private Object resolvePathVariable(@NotNull HttpContext ctx, @NotNull Parameter parameter) {
        PathVariable annotation = parameter.getAnnotation(PathVariable.class);
        String name = getEffectiveName(annotation.value(), parameter.getName());
        String value = ctx.pathParam(name);
        return convertParameter(value, parameter.getType());
    }

    private Object resolveRequestParam(@NotNull HttpContext ctx, @NotNull Parameter parameter) {
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        String name = getEffectiveName(annotation.value(), parameter.getName());
        String value = ctx.query(name, annotation.defaultValue());

        if (value == null && annotation.required()) {
            throw new IllegalArgumentException("Missing required query parameter: " + name);
        }
        return convertParameter(value, parameter.getType());
    }

    private Object resolveRequestHeader(@NotNull HttpContext ctx, @NotNull Parameter parameter) {
        RequestHeader annotation = parameter.getAnnotation(RequestHeader.class);
        String value = ctx.header(annotation.value());

        if (value == null) {
            if (annotation.required()) {
                throw new IllegalArgumentException("Missing required header: " + annotation.value());
            }
            value = annotation.defaultValue();
        }
        return convertParameter(value, parameter.getType());
    }

    private @Nullable Object resolveBeanDependency(Parameter parameter) {
        try {
            return context.getBean(parameter.getType());
        } catch (Exception e) {
            return null;
        }
    }

    // Utilitário para evitar repetição de lógica (nome da anotação vs nome do parâmetro)
    private String getEffectiveName(String annotationValue, String paramName) {
        return (annotationValue == null || annotationValue.isEmpty()) ? paramName : annotationValue;
    }

    private Object convertParameter(String value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class)                                 return value;
        if (targetType == int.class     || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class    || targetType == Long.class)    return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class  || targetType == Double.class)  return Double.parseDouble(value);

        return value;
    }

    private void handleResponse(HttpContext ctx, java.lang.reflect.Method method, Object result) {
        int statusCode = 200;
        if (method.isAnnotationPresent(ResponseStatus.class)) {
            statusCode = method.getAnnotation(ResponseStatus.class).value();
        }

        if (result == null || method.getReturnType() == void.class) {
            ctx.status(statusCode);
            return;
        }

        if (result instanceof String) {
            ctx.status(StatusCode.fromCode(statusCode), (String) result);
            return;
        }

        ctx.status(StatusCode.fromCode(statusCode));
        ctx.json(result);
    }
}