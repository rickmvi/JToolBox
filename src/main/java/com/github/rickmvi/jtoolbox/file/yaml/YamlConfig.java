package com.github.rickmvi.jtoolbox.file.yaml;

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

    private final String        path;
    private Map<String, Object> data;

    public YamlConfig(String path) {
        this.path = path;
        this.data = loadInternal(path);
    }

    private @NotNull Map<String, Object> loadInternal(String path) {
        try {
            InputStream input;

            input = YamlConfig.class.getClassLoader().getResourceAsStream(path);

            if (input == null) input = Files.newInputStream(Paths.get(path));

            Yaml yaml = new Yaml();
            Map<String, Object> loaded = yaml.load(input);

            return loaded != null ? loaded : new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("Error loaded YAML: " + path, e);
        }
    }

    public void reload() {
        this.data = loadInternal(this.path);
    }

    private @Nullable Optional<Object> resolvePath(@NotNull String path) {
        String[] parts = path.split("\\.");
        Optional<Object> current = Optional.ofNullable(data);

        for (String p : parts) {
            current.ifPresent(o -> ((Map<?, ?>) current.get()).get(p));
        }
        return current;
    }

    public boolean contains(String path) {
        return Objects.requireNonNull(resolvePath(path)).isPresent();
    }

    public String getString(String path) {
        Object v = resolvePath(path);
        return v != null ? String.valueOf(v) : null;
    }

    public int getInt(String path) {
        Object v = resolvePath(path);
        return Numbers.toInt(v);
    }

    public boolean getBoolean(String path) {
        Object v = resolvePath(path);
        return TypeAdapter.toBoolean(v);
    }

    public double getDouble(String path) {
        Object v = resolvePath(path);
        return Numbers.toDouble(v);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object v = resolvePath(path);
        if (v instanceof List) return (List<Object>) v;
        return Collections.emptyList();
    }

    public List<String> getStringList(String path) {
        Object v = resolvePath(path);
        if (v instanceof List<?> raw) {
            List<String> list = new ArrayList<>();
            raw.forEach(i -> list.add(String.valueOf(i)));
            return list;
        }
        return Collections.emptyList();
    }

}
