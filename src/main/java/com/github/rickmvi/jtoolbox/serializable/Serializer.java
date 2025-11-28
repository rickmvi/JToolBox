/*
 * Console API - Utilitarian library for input, output and formatting on the console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.rickmvi.jtoolbox.serializable;

import com.github.rickmvi.jtoolbox.file.File;
import com.github.rickmvi.jtoolbox.json.JsonX;
import com.github.rickmvi.jtoolbox.text.Stringifier;
import com.github.rickmvi.jtoolbox.util.Try;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;
import java.util.Objects;

import static com.github.rickmvi.jtoolbox.text.StringFormatter.format;

/**
 * Utility class for managing object serialization and deserialization, supporting both
 * JSON (using Gson) and standard Java Binary Serialization (for {@link java.io.Serializable} objects).
 * <p>
 * This class provides a centralized, fluent API for converting objects to/from strings, byte arrays,
 * files, and Base64 encoded strings, simplifying data persistence and transfer operations.
 * </p>
 *
 * <h2>JSON Serialization (using Gson)</h2>
 * <p>Methods like {@link #serialize(Object)}, {@link #deserialize(String, Class)},
 * {@link #saveToFile(Object, String)}, and {@link #loadFromFile(String, Class)} use a
 * pre-configured Gson instance for human-readable data exchange, typically used for
 * configuration or API communication.</p>
 *
 * <h2>Binary e Base64 Serialization</h2>
 * <p>Methods like {@link #serializeToBytes(Object)} and {@link #deserializeFromBase64(String, Class)}
 * utilize standard Java object streams to efficiently handle objects implementing {@link java.io.Serializable}.
 * This is useful for deep cloning, transferring objects over sockets, or storing complex application state.</p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Example class for JSON (no special implementation required)
 * class UserData {
 * String name = "Gemini";
 * int level = 5;
 * }
 *
 * // Example class for Binary/Base64 (must implement Serializable)
 * class AppState implements java.io.Serializable {
 * private static final long serialVersionUID = 42L;
 * boolean initialized = true;
 * String currentTheme = "dark";
 * }
 *
 * // 1. JSON Serialization (String and File)
 * UserData userData = new UserData();
 * String json = Serializer.serialize(userData);
 * // json is now a pretty-printed JSON string:
 * // { "name": "Gemini", "level": 5 }
 *
 * // Save to file
 * Serializer.saveToFile(userData, "config.json");
 *
 * // Load from file and deserialize
 * UserData loadedUser = Serializer.loadFromFile("config.json", UserData.class);
 *
 * // 2. Binary / Base64 Serialization (for transfer/storage)
 * AppState appState = new AppState();
 *
 * // Serialize object to a Base64 string
 * String base64 = Serializer.serializeToBase64(appState);
 * // base64 is a compact, encoded string of the binary data
 *
 * // Deserialize back from Base64
 * AppState loadedState = Serializer.deserializeFromBase64(base64, AppState.class);
 * }</pre>
 *
 * @see com.google.gson.Gson
 * @see java.io.Serializable
 * @version 1.2
 * @since 2025
 * @author Rick M. Viana
 */
@UtilityClass
public final class Serializer {

    @Getter
    private static final Gson instance;

    static {
        instance = JsonX.builder()
                .serializeNulls()
                .prettyPrint()
                .build()
                .getGson();
    }

    public static @NotNull String serialize(Object object) {
        return instance.toJson(Objects.requireNonNull(object));
    }

    public static <T> T deserialize(String json, @NotNull Class<T> type) {
        String failureMessage = format(
                "Failed to deserialize JSON to type {}. Check the JSON format and structure.",
                type.getSimpleName());

        return Try.of(() -> getInstance().fromJson(json, type))
                .orElseThrow(throwable -> new JsonSyntaxException(failureMessage, throwable));
    }

    public static void saveToFile(@NotNull Object object, @NotNull String filePath) {
        String json = serialize(object);

        File.at(filePath)
                .createDirectoriesIfNeeded()
                .createIfNotExists()
                .write(json);
    }

    public static <T> T loadFromFile(String filePath, Class<T> type) {
        String json = File.read(filePath);
        return deserialize(json, type);
    }

    public static byte @NotNull [] serializeToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(object);
            return bos.toByteArray();
        }
    }

    public static <T> T deserializeFromBytes(byte @NotNull [] bytes, @NotNull Class<T> type)
            throws IOException, ClassNotFoundException {

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            Object obj = ois.readObject();

            return type.cast(obj);
        }
    }

    public static void saveToBinaryFile(@NotNull Serializable object, @NotNull String filePath) {
        byte[] bytes = Try.ofThrowing(() -> serializeToBytes(object)).orThrow();

        File.at(filePath)
                .createDirectoriesIfNeeded()
                .createIfNotExists()
                .write(Stringifier.toString(bytes));
    }

    public static <T> T loadFromBinaryFile(@NotNull String filePath, @NotNull Class<T> type) {
        byte[] bytes = File.readBytes(filePath);
        return Try.ofThrowing(() -> deserializeFromBytes(bytes, type)).orThrow();
    }

    public static @NotNull String serializeToBase64(@NotNull Serializable object) {
        byte[] bytes = Try.ofThrowing(() -> serializeToBytes(object)).orThrow();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static <T> T deserializeFromBase64(@NotNull String base64String, @NotNull Class<T> type) {
        byte[] bytes = Base64.getDecoder().decode(base64String);
        return Try.ofThrowing(() -> deserializeFromBytes(bytes, type)).orThrow();
    }

}
