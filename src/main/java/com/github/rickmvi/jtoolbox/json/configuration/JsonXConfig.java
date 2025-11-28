package com.github.rickmvi.jtoolbox.json.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record JsonXConfig(boolean serializeNulls, Charset charset) {

    @Contract(" -> new")
    public static @NotNull JsonXConfig defaults() {
        return new JsonXConfig(false, StandardCharsets.UTF_8);
    }

}
