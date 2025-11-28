package com.github.rickmvi.jtoolbox.yaml.util;

import java.util.Map;

public interface YamlMapper {

    @SuppressWarnings("unchecked")
    static Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

}
