package com.github.rickmvi.jtoolbox.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

public class JsonXPath {

    private final JsonElement currentElement;

    private JsonXPath(JsonElement element) {
        this.currentElement = element;
    }

    public static JsonXPath of(JsonElement element) {
        return new JsonXPath(element);
    }

    public JsonXPath get(String path) {
        if (currentElement == null || currentElement.isJsonNull()) {
            return new JsonXPath(null);
        }

        if (path == null || path.isEmpty()) {
            return this;
        }

        String[] parts = path.split("\\.");
        JsonElement current = this.currentElement;

        for (String part : parts) {
            if (current == null || !current.isJsonObject()) {
                return new JsonXPath(null);
            }

            current = current.getAsJsonObject().get(part);
        }

        return new JsonXPath(current);
    }

    public JsonXPath at(int index) {
        if (currentElement == null || !currentElement.isJsonArray()) {
            return new JsonXPath(null);
        }

        JsonArray array = currentElement.getAsJsonArray();
        if (index >= 0 && index < array.size()) {
            return new JsonXPath(array.get(index));
        }
        return new JsonXPath(null);
    }

    public Optional<String> asString() {
        if (!isPresent() || !currentElement.isJsonPrimitive()) {
            return Optional.empty();
        }

        JsonPrimitive primitive = currentElement.getAsJsonPrimitive();
        if (primitive.isString()) {
            return Optional.of(primitive.getAsString());
        }

        if (primitive.isNumber()) {
            return Optional.of(primitive.getAsNumber().toString());
        }

        if (primitive.isBoolean()) {
            return Optional.of(Boolean.toString(primitive.getAsBoolean()));
        }

        return Optional.empty();
    }

    public Optional<Integer> asInt() {
        return asNumericValue(currentElement::getAsInt);
    }

    public Optional<Long> asLong() {
        return asNumericValue(currentElement::getAsLong);
    }

    public Optional<Double> asDouble() {
        return asNumericValue(currentElement::getAsDouble);
    }

    public Optional<Boolean> asBoolean() {
        if (isPresent() && currentElement.isJsonPrimitive() && currentElement.getAsJsonPrimitive().isBoolean()) {
            return Optional.of(currentElement.getAsBoolean());
        }
        return Optional.empty();
    }

    public Optional<JsonObject> asObject() {
        if (isPresent() && currentElement.isJsonObject()) {
            return Optional.of(currentElement.getAsJsonObject());
        }
        return Optional.empty();
    }

    public Optional<JsonArray> asArray() {
        if (isPresent() && currentElement.isJsonArray()) {
            return Optional.of(currentElement.getAsJsonArray());
        }
        return Optional.empty();
    }

    public Optional<JsonElement> asJsonElement() {
        return Optional.ofNullable(currentElement);
    }

    private boolean isPresent() {
        return currentElement != null && !currentElement.isJsonNull();
    }

    private <T> Optional<T> asNumericValue(NumericExtractor<T> extractor) {
        if (isPresent() && currentElement.isJsonPrimitive() && currentElement.getAsJsonPrimitive().isNumber()) {
            return Optional.of(extractor.fetchValue());
        }
        return Optional.empty();
    }

    @FunctionalInterface
    private interface NumericExtractor<T> {
        T fetchValue();
    }
}