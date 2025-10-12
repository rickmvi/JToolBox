package com.github.rickmvi.jtoolbox.collections.map;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;

public class Mapping {

    public static <T, R> R returning(T value, @NotNull Map<T, Supplier<R>> cases, Supplier<R> defaultAction) {
        return cases.getOrDefault(value, defaultAction).get();
    }

    public static @NotNull String applyReplacements(
            @NotNull String target,
            @NotNull Map<String, Object> replacements
    ) {
        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            target = target.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return target;
    }

}
