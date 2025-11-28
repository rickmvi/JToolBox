package com.github.rickmvi.jtoolbox.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class JsonXMapper {

    public static Map<String, Object> toMap(JsonObject jsonObject) {
        return jsonObject.entrySet().stream()
                .collect(LinkedHashMap::new,
                        (map, entry) ->
                                map.put(entry.getKey(), toValue(entry.getValue())),
                        Map::putAll);
    }

    public static List<Object> toList(JsonArray jsonArray) {
        return jsonArray.asList()
                .stream()
                .map(JsonXMapper::toValue)
                .toList();
    }

    private static Object toValue(JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        }

        if (element.isJsonObject()) {
            return toMap(element.getAsJsonObject());
        }

        if (element.isJsonArray()) {
            return toList(element.getAsJsonArray());
        }

        if (element.isJsonPrimitive()) {
            return convertPrimitive(element.getAsJsonPrimitive());
        }

        return element.toString();
    }

    private static Object convertPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }
        
        if (primitive.isNumber()) {
            return convertNumber(primitive.getAsNumber());
        }
        
        if (primitive.isString()) {
            return primitive.getAsString();
        }
        
        return primitive.toString();
    }

    private static Object convertNumber(Number num) {
        double d = num.doubleValue();
        if (d == (long) d) {
            return num.longValue();
        }
        return d;
    }

    public static Optional<String> extractString(JsonObject json, String path) {
        return JsonXPath.of(json).get(path).asString();
    }

    public static Optional<Integer> extractInt(JsonObject json, String path) {
        return JsonXPath.of(json).get(path).asInt();
    }

    public static boolean contains(JsonObject json, String path) {
        return JsonXPath.of(json).get(path).asJsonElement().isPresent();
    }

    public static Set<String> getAllKeys(JsonObject jsonObject) {
        Set<String> keys = new LinkedHashSet<>();
        collectKeys(jsonObject, "", keys);
        return keys;
    }

    private static void collectKeys(JsonObject jsonObject, String prefix, Set<String> keys) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);
            if (entry.getValue().isJsonObject()) {
                collectKeys(entry.getValue().getAsJsonObject(), key, keys);
            }
        }
    }

    public static JsonObject merge(JsonObject base, JsonObject overlay) {
        JsonObject result = base.deepCopy();
        for (Map.Entry<String, JsonElement> entry : overlay.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!value.isJsonObject() || !result.has(key) || !result.get(key).isJsonObject()) {
                result.add(key, value);
                continue;
            }
            result.add(key, merge(result.getAsJsonObject(key), value.getAsJsonObject()));
        }
        return result;
    }

    public static boolean isEmpty(JsonObject jsonObject) {
        return jsonObject == null || jsonObject.isEmpty();
    }

    public static boolean isEmpty(JsonArray jsonArray) {
        return jsonArray == null || jsonArray.isEmpty();
    }
}
