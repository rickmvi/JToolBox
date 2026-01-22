package com.github.rickmvi.jtoolbox.json;

import com.github.rickmvi.jtoolbox.json.configuration.JsonXConfig;
import com.github.rickmvi.jtoolbox.json.exception.JsonXException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonX {

    private final Gson        gson;

    private final JsonXConfig config;

    @Contract(value = "_, _ -> new", pure = true)
    @ApiStatus.Internal
    protected static @NotNull JsonX initialize(Gson gson, JsonXConfig config) {
        return new JsonX(gson, config);
    }

    @Contract(" -> new")
    public static @NotNull JsonX create() {
        return new JsonX(new Gson(), JsonXConfig.defaults());
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull JsonXBuilder builder() {
        return new JsonXBuilder();
    }

    public String marshal(Object obj) {
        if (obj == null) {
            return config.serializeNulls() ? "null" : "{}";
        }
        return gson.toJson(obj);
    }

    public static String toJson(Object obj) {
        return create().marshal(obj);
    }

    public JsonElement marshalToTree(Object obj) {
        return gson.toJsonTree(obj);
    }

    public void marshal(Object obj, Writer writer) throws JsonXException {
        try {
            gson.toJson(obj, writer);
        } catch (Exception e) {
            throw new JsonXException("Failed to marshal object to writer", e);
        }
    }

    public void marshalToFile(Object obj, Path filePath) throws JsonXException {
        try (Writer writer = Files.newBufferedWriter(filePath, config.charset())) {
            marshal(obj, writer);
        } catch (IOException e) {
            throw new JsonXException("Failed to marshal object to file: " + filePath, e);
        }
    }

    public void marshalToFile(Object obj, String filePath) throws JsonXException {
        marshalToFile(obj, Path.of(filePath));
    }

    public <T> T unmarshal(String json, Class<T> classOfT) throws JsonXException {
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            throw new JsonXException("Failed to unmarshal JSON to " + classOfT.getSimpleName(), e);
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return create().unmarshal(json, classOfT);
    }

    public <T> T unmarshal(String json, Type typeOfT) throws JsonXException {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            throw new JsonXException("Failed to unmarshal JSON to " + typeOfT.getTypeName(), e);
        }
    }

    public <T> T unmarshal(String json, TypeToken<T> typeToken) throws JsonXException {
        return unmarshal(json, typeToken.getType());
    }

    public <T> T unmarshal(JsonElement json, Class<T> classOfT) throws JsonXException {
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            throw new JsonXException("Failed to unmarshal JsonElement to " + classOfT.getSimpleName(), e);
        }
    }

    public <T> T unmarshal(Reader reader, Class<T> classOfT) throws JsonXException {
        try {
            return gson.fromJson(reader, classOfT);
        } catch (Exception e) {
            throw new JsonXException("Failed to unmarshal from reader to " + classOfT.getSimpleName(), e);
        }
    }

    public <T> T unmarshalFromFile(Path filePath, Class<T> classOfT) throws JsonXException {
        try (Reader reader = Files.newBufferedReader(filePath, config.charset())) {
            return unmarshal(reader, classOfT);
        } catch (IOException e) {
            throw new JsonXException("Failed to unmarshal from file: " + filePath, e);
        }
    }

    public <T> T unmarshalFromFile(String filePath, Class<T> classOfT) throws JsonXException {
        return unmarshalFromFile(Path.of(filePath), classOfT);
    }

    public <T> T unmarshalFromFile(Path filePath, Type typeOfT) throws JsonXException {
        try (Reader reader = Files.newBufferedReader(filePath, config.charset())) {
            return gson.fromJson(reader, typeOfT);
        } catch (IOException e) {
            throw new JsonXException("Failed to unmarshal from file: " + filePath, e);
        }
    }

    public <T> T unmarshalFromFile(Path filePath, TypeToken<T> typeToken) throws JsonXException {
        return unmarshalFromFile(filePath, typeToken.getType());
    }

    public <T> T unmarshalFromFile(String filePath, TypeToken<T> typeToken) throws JsonXException {
        return unmarshalFromFile(Path.of(filePath), typeToken);
    }

    public JsonElement parse(String json) throws JsonXException {
        try {
            return JsonParser.parseString(json);
        } catch (Exception e) {
            throw new JsonXException("Failed to parse JSON string", e);
        }
    }

    public JsonElement parse(Reader reader) throws JsonXException {
        try {
            return JsonParser.parseReader(reader);
        } catch (Exception e) {
            throw new JsonXException("Failed to parse JSON from reader", e);
        }
    }

    public JsonElement parseFile(Path filePath) throws JsonXException {
        try (Reader reader = Files.newBufferedReader(filePath, config.charset())) {
            return parse(reader);
        } catch (IOException e) {
            throw new JsonXException("Failed to parse JSON from file: " + filePath, e);
        }
    }

    public JsonElement parseFile(String filePath) throws JsonXException {
        return parseFile(Path.of(filePath));
    }

    public boolean isValid(String json) {
        try {
            JsonParser.parseString(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String prettify(String json) throws JsonXException {
        JsonElement element = parse(json);
        return builder()
                .prettyPrint()
                .build()
                .marshal(element);
    }

    public String minify(String json) throws JsonXException {
        JsonElement element = parse(json);
        return marshal(element);
    }

    public static <T> T parse(String json, Class<T> classOfT) {
        return create().unmarshal(json, classOfT);
    }

    public static <T> T parse(String json, TypeToken<T> typeToken) {
        return create().unmarshal(json, typeToken);
    }

    public static <T> T parseFile(String filePath, Class<T> classOfT) {
        return create().unmarshalFromFile(filePath, classOfT);
    }

    public static <T> T parseFile(String filePath, TypeToken<T> typeToken) {
        return create().unmarshalFromFile(filePath, typeToken);
    }

    public static boolean validate(String json) {
        return create().isValid(json);
    }

}
