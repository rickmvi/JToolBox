package com.github.rickmvi.jtoolbox.yaml;

import org.jetbrains.annotations.NotNull;

public interface Yaml {

    static @NotNull YamlConfig load(String path) {
        return new YamlConfig(path);
    }

}
