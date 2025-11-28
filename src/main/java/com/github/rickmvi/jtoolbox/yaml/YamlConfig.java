package com.github.rickmvi.jtoolbox.yaml;

import com.github.rickmvi.jtoolbox.util.Numbers;
import com.github.rickmvi.jtoolbox.util.convert.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class YamlConfig {
    private final String path;
    private Map<String, Object> data;

    public YamlConfig(String path) {
        this.path = path;
        this.data = loadInternal(path);
    }

    private @NotNull Map<String, Object> loadInternal(String path) {
        try {
            InputStream input = YamlConfig.class.getClassLoader().getResourceAsStream(path);
            if (input == null) {
                input = Files.newInputStream(Paths.get(path));
            }

            Yaml yaml = new Yaml();
            Map<String, Object> loaded = yaml.load(input);
            return loaded != null ? loaded : new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("Error loading YAML: " + path, e);
        }
    }

    public void reload() {
        this.data = loadInternal(this.path);
    }

    private @Nullable Object resolveValue(@NotNull String path) {
        String[] parts = path.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current instanceof Map<?, ?> map) {
                current = map.get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    public boolean contains(String path) {
        return resolveValue(path) != null;
    }

    public String getString(String path) {
        Object value = resolveValue(path);
        return value != null ? String.valueOf(value) : null;
    }

    public int getInt(String path) {
        Object value = resolveValue(path);
        return Numbers.toInt(value);
    }

    public boolean getBoolean(String path) {
        Object value = resolveValue(path);
        return TypeAdapter.toBoolean(value);
    }

    public double getDouble(String path) {
        Object value = resolveValue(path);
        return Numbers.toDouble(value);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object value = resolveValue(path);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return Collections.emptyList();
    }

    public List<String> getStringList(String path) {
        Object value = resolveValue(path);
        if (value instanceof List<?> rawList) {
            return rawList.stream()
                    .map(String::valueOf)
                    .toList();
        }
        return Collections.emptyList();
    }
}