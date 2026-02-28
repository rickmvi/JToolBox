package com.github.rickmvi.jtoolbox.config.core;

import com.github.rickmvi.jtoolbox.config.annotations.*;
import com.github.rickmvi.jtoolbox.config.exceptions.InjectionException;
import com.github.rickmvi.jtoolbox.dotenv.Dotenv;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EnvInjector {

    private final Dotenv dotenv;
    private final Map<Field, Object> reloadableFields = new HashMap<>();

    public EnvInjector(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public void inject(Object target) {
        Class<?> clazz = target.getClass();

        String classPrefix = getClassPrefix(clazz);
        EnvGroup envGroup = clazz.getAnnotation(EnvGroup.class);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Env.class)) {
                injectField(target, field, classPrefix, envGroup);
            }
        }
    }

    private void injectField(Object target, Field field, String classPrefix, EnvGroup envGroup) {
        field.setAccessible(true);

        Env envAnnotation = field.getAnnotation(Env.class);
        Required required = field.getAnnotation(Required.class);

        String varName = resolveVarName(envAnnotation.value(), classPrefix, envGroup);

        String rawValue = dotenv.get(varName);

        if (rawValue == null && !envAnnotation.defaultValue().isEmpty()) {
            rawValue = envAnnotation.defaultValue();
        }

        if (rawValue == null && required != null) {
            String message = required.message().isEmpty()
                    ? "Missing required env var: " + varName
                    : required.message();
            throw new InjectionException(message);
        }

        try {
            Object convertedValue = TypeConverter.convert(rawValue, field.getType());

            EnvValidator.validate(field, convertedValue);

            if (field.isAnnotationPresent(Decrypt.class)) {
                convertedValue = applyDecryption(convertedValue, field.getAnnotation(Decrypt.class));
            }

            field.set(target, convertedValue);

            if (field.isAnnotationPresent(Reloadable.class)) {
                reloadableFields.put(field, target);
            }

        } catch (IllegalAccessException e) {
            throw new InjectionException("Failed to inject field: " + field.getName(), e);
        }
    }

    private String resolveVarName(String varName, String classPrefix, EnvGroup envGroup) {
        StringBuilder fullName = new StringBuilder();

        if (envGroup != null) {
            fullName.append(envGroup.value())
                    .append(envGroup.separator());
        } else if (classPrefix != null && !classPrefix.isEmpty()) {
            fullName.append(classPrefix);
        }

        fullName.append(varName);

        return fullName.toString();
    }

    private String getClassPrefix(Class<?> clazz) {
        EnvPrefix prefix = clazz.getAnnotation(EnvPrefix.class);
        return prefix != null ? prefix.value() : "";
    }

    private Object applyDecryption(Object value, Decrypt decrypt) {
        if (value == null || !(value instanceof String)) {
            return value;
        }

        try {
            Decrypt.Decryptor decryptor = decrypt.value().getDeclaredConstructor().newInstance();
            return decryptor.decrypt((String) value, decrypt.algorithm());
        } catch (Exception e) {
            throw new InjectionException("Failed to decrypt value", e);
        }
    }

    public void reload() {
        reloadableFields.forEach((field, target) -> {
            try {
                Env envAnnotation = field.getAnnotation(Env.class);
                String varName = resolveVarName(
                        envAnnotation.value(),
                        getClassPrefix(target.getClass()),
                        target.getClass().getAnnotation(EnvGroup.class)
                );

                String rawValue = dotenv.get(varName);
                if (rawValue != null) {
                    Object convertedValue = TypeConverter.convert(rawValue, field.getType());
                    field.set(target, convertedValue);
                }
            } catch (Exception e) {
                throw new InjectionException("Failed to reload field: " + field.getName(), e);
            }
        });
    }

    public Map<Field, Object> getReloadableFields() {
        return new HashMap<>(reloadableFields);
    }
}