package com.github.rickmvi.jtoolbox.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NullSafety {

    public static boolean nonNull(@Nullable Object o) {
        return Objects.nonNull(o) && !o.toString().isEmpty();
    }
}
