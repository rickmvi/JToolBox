package com.github.rickmvi.jtoolbox.config.core;

import com.github.rickmvi.jtoolbox.config.annotations.*;
import com.github.rickmvi.jtoolbox.config.exceptions.ProxyException;
import com.github.rickmvi.jtoolbox.dotenv.Dotenv;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class EnvProxy {

    public static <T> T create(Class<T> configInterface) {
        return create(configInterface, EnvEngine.getDotenv());
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> configInterface, Dotenv dotenv) {
        if (!configInterface.isInterface()) {
            throw new ProxyException("Only interfaces are supported: " + configInterface.getName());
        }

        ConfigInvocationHandler handler = new ConfigInvocationHandler(configInterface, dotenv);

        return (T) Proxy.newProxyInstance(
                configInterface.getClassLoader(),
                new Class<?>[]{configInterface},
                handler
        );
    }

    private static class ConfigInvocationHandler implements InvocationHandler {

        private final Class<?> configInterface;
        private final Dotenv dotenv;
        private final Map<Method, Object> cache = new HashMap<>();

        public ConfigInvocationHandler(Class<?> configInterface, Dotenv dotenv) {
            this.configInterface = configInterface;
            this.dotenv = dotenv;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if (method.getDeclaringClass() == Object.class) {
                return handleObjectMethod(proxy, method, args);
            }

            if (!method.isAnnotationPresent(Reloadable.class) && cache.containsKey(method)) {
                return cache.get(method);
            }

            Object value = resolveMethodValue(method);

            if (!method.isAnnotationPresent(Reloadable.class)) {
                cache.put(method, value);
            }

            return value;
        }

        private Object resolveMethodValue(Method method) {
            Env envAnnotation = method.getAnnotation(Env.class);

            if (envAnnotation == null) {
                throw new ProxyException("Method must be annotated with @Env: " + method.getName());
            }

            String varName = resolveVarName(envAnnotation.value());

            String rawValue = dotenv.get(varName);

            if (rawValue == null && !envAnnotation.defaultValue().isEmpty()) {
                rawValue = envAnnotation.defaultValue();
            }

            if (rawValue == null && method.isAnnotationPresent(Required.class)) {
                Required required = method.getAnnotation(Required.class);
                String message = required.message().isEmpty()
                        ? "Missing required env var: " + varName
                        : required.message();
                throw new ProxyException(message);
            }

            Class<?> returnType = method.getReturnType();
            Object convertedValue = TypeConverter.convert(rawValue, returnType);

            validateMethodValue(method, convertedValue);

            if (method.isAnnotationPresent(Decrypt.class)) {
                convertedValue = applyDecryption(convertedValue, method.getAnnotation(Decrypt.class));
            }

            return convertedValue;
        }

        private String resolveVarName(String varName) {
            StringBuilder fullName = new StringBuilder();

            if (configInterface.isAnnotationPresent(EnvPrefix.class)) {
                EnvPrefix prefix = configInterface.getAnnotation(EnvPrefix.class);
                fullName.append(prefix.value());
            }

            if (configInterface.isAnnotationPresent(EnvGroup.class)) {
                EnvGroup group = configInterface.getAnnotation(EnvGroup.class);
                fullName.append(group.value())
                        .append(group.separator());
            }

            fullName.append(varName);

            return fullName.toString();
        }

        private void validateMethodValue(Method method, Object value) {
            if (method.isAnnotationPresent(com.github.rickmvi.jtoolbox.config.validation.NotNull.class) && value == null) {
                throw new ProxyException("Value cannot be null for method: " + method.getName());
            }
        }

        private Object applyDecryption(Object value, Decrypt decrypt) {
            if (value == null || !(value instanceof String)) {
                return value;
            }

            try {
                Decrypt.Decryptor decryptor = decrypt.value().getDeclaredConstructor().newInstance();
                return decryptor.decrypt((String) value, decrypt.algorithm());
            } catch (Exception e) {
                throw new ProxyException("Failed to decrypt value", e);
            }
        }

        private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();

            return switch (methodName) {
                case "toString" -> configInterface.getName() + "@EnvProxy";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Method not supported: " + methodName);
            };
        }
    }
}