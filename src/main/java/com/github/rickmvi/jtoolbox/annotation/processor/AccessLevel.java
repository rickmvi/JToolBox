package com.github.rickmvi.jtoolbox.annotation.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the accessibility of generated members in the JToolbox framework.
 *
 * <p>This enum is used by the Annotation Processor to determine which
 * access modifier keyword (if any) should be prepended to generated
 * methods, fields, and classes.</p>
 *
 * <p><strong>Visibility Mapping:</strong></p>
 * <ul>
 * <li>{@code PUBLIC} -> {@code "public"}</li>
 * <li>{@code PROTECTED} -> {@code "protected"}</li>
 * <li>{@code PRIVATE} -> {@code "private"}</li>
 * <li>{@code PACKAGE} -> Default (no modifier)</li>
 * </ul>
 *
 *
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
public enum AccessLevel {

    /** Fully accessible from any package. */
    PUBLIC,

    /** Accessible within the package and by subclasses. */
    PROTECTED,

    /**
     * Package-private visibility (Java's default).
     * No keyword is generated.
     */
    PACKAGE,

    /** Accessible only within the declaring class. */
    PRIVATE,

    /**
     * Internal to the Java Module (JPMS).
     * Note: In source generation, this often translates to package-private.
     */
    MODULE,

    /**
     * Represents the absence of a modifier or a signal to skip generation.
     */
    NONE;

    /**
     * Translates the enum constant into a valid Java source code modifier.
     *
     * <p>This method uses the modern switch expression (Java 12+) to provide
     * a clean mapping for the code generator.</p>
     *
     * @return the string keyword to be used in source generation.
     */
    @Contract(pure = true)
    public @NotNull String toJavaModifier() {
        return switch (this) {
            case PUBLIC    -> "public";
            case PROTECTED -> "protected";
            case PRIVATE   -> "private";
            case PACKAGE, MODULE, NONE -> "";
        };
    }
}