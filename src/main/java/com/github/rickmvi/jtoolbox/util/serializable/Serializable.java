package com.github.rickmvi.jtoolbox.util.serializable;

import org.jetbrains.annotations.NotNull;

public interface Serializable extends java.io.Serializable {

    default <T extends Serializable> T deserialize(String json, Class<T> clazz) {
        return Serializer.deserialize(json, clazz);
    }

    default @NotNull String serialize(Object serializable) {
        return Serializer.serialize(serializable);
    }

    default void save(Object serializable, String path) {
        Serializer.saveToFile(serializable, path);
    }

    default <T> T load(String path, Class<T> clazz) {
        return Serializer.loadFromFile(path, clazz);
    }

}
