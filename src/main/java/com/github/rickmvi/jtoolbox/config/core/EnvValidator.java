package com.github.rickmvi.jtoolbox.config.core;

import com.github.rickmvi.jtoolbox.config.exceptions.ValidationException;
import com.github.rickmvi.jtoolbox.config.validation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.regex.Pattern;

public class EnvValidator {


    public static void validate(Field field, Object value) {
        Annotation[] annotations = field.getAnnotations();

        for (Annotation annotation : annotations) {
            validateAnnotation(field, value, annotation);
        }
    }

    private static void validateAnnotation(Field field, Object value, Annotation annotation) {
        if (annotation instanceof NotNull) {
            validateNotNull(field, value, (NotNull) annotation);
        } else if (annotation instanceof NotEmpty) {
            validateNotEmpty(field, value, (NotEmpty) annotation);
        } else if (annotation instanceof Min) {
            validateMin(field, value, (Min) annotation);
        } else if (annotation instanceof Max) {
            validateMax(field, value, (Max) annotation);
        } else if (annotation instanceof Regex) {
            validateRegex(field, value, (Regex) annotation);
        } else if (annotation instanceof MinLength) {
            validateMinLength(field, value, (MinLength) annotation);
        } else if (annotation instanceof MaxLength) {
            validateMaxLength(field, value, (MaxLength) annotation);
        }
    }

    private static void validateNotNull(Field field, Object value, NotNull annotation) {
        if (value == null) {
            String message = annotation.message().isEmpty()
                    ? "Value cannot be null for field: " + field.getName()
                    : annotation.message();
            throw new ValidationException(message);
        }
    }

    private static void validateNotEmpty(Field field, Object value, NotEmpty annotation) {
        if (value == null) {
            throw new ValidationException("Value cannot be null for field: " + field.getName());
        }

        boolean isEmpty = false;

        if (value instanceof String) {
            isEmpty = ((String) value).isEmpty();
        } else if (value instanceof Collection) {
            isEmpty = ((Collection<?>) value).isEmpty();
        }

        if (isEmpty) {
            String message = annotation.message().isEmpty()
                    ? "Value cannot be empty for field: " + field.getName()
                    : annotation.message();
            throw new ValidationException(message);
        }
    }

    private static void validateMin(Field field, Object value, Min annotation) {
        if (value == null) {
            return;
        }

        long numValue;
        if (value instanceof Number) {
            numValue = ((Number) value).longValue();
        } else {
            throw new ValidationException("@Min can only be applied to numeric types");
        }

        if (numValue < annotation.value()) {
            String message = annotation.message()
                    .replace("{value}", String.valueOf(annotation.value()));
            throw new ValidationException(message + " for field: " + field.getName());
        }
    }

    private static void validateMax(Field field, Object value, Max annotation) {
        if (value == null) {
            return;
        }

        long numValue;
        if (value instanceof Number) {
            numValue = ((Number) value).longValue();
        } else {
            throw new ValidationException("@Max can only be applied to numeric types");
        }

        if (numValue > annotation.value()) {
            String message = annotation.message()
                    .replace("{value}", String.valueOf(annotation.value()));
            throw new ValidationException(message + " for field: " + field.getName());
        }
    }

    private static void validateRegex(Field field, Object value, Regex annotation) {
        if (value == null) {
            return;
        }

        String strValue = value.toString();
        Pattern pattern = Pattern.compile(annotation.value());

        if (!pattern.matcher(strValue).matches()) {
            String message = annotation.message()
                    .replace("{value}", annotation.value());
            throw new ValidationException(message + " for field: " + field.getName());
        }
    }

    private static void validateMinLength(Field field, Object value, MinLength annotation) {
        if (value == null) {
            return;
        }

        if (!(value instanceof String)) {
            throw new ValidationException("@MinLength can only be applied to String types");
        }

        String strValue = (String) value;
        if (strValue.length() < annotation.value()) {
            String message = annotation.message()
                    .replace("{value}", String.valueOf(annotation.value()));
            throw new ValidationException(message + " for field: " + field.getName());
        }
    }

    private static void validateMaxLength(Field field, Object value, MaxLength annotation) {
        if (value == null) {
            return;
        }

        if (!(value instanceof String)) {
            throw new ValidationException("@MaxLength can only be applied to String types");
        }

        String strValue = (String) value;
        if (strValue.length() > annotation.value()) {
            String message = annotation.message()
                    .replace("{value}", String.valueOf(annotation.value()));
            throw new ValidationException(message + " for field: " + field.getName());
        }
    }
}