package com.github.rickmvi.jtoolbox.annotation.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code AccessLevel} enumeration defines various levels of access control
 * that can be applied to entities within the context of Java. Each level
 * corresponds to a specific modifier or accessibility rule.
 * <p>
 * This enumeration can be used to represent and translate access modifiers
 * into their respective string values for further processing or analysis.
 */
public enum AccessLevel {

    /**
     * Represents the public access level in the {@link AccessLevel} enumeration.
     * This level signifies that the associated entity is accessible from any other class.
     */
    PUBLIC,

    /**
     * Represents the protected access level in the {@link AccessLevel} enumeration.
     * This level signifies that the associated entity is accessible within its own package
     * and by subclasses.
     */
    PROTECTED,

    /**
     * Represents the package-private access level in the {@link AccessLevel} enumeration.
     * This level signifies that the associated entity is accessible only within its own package.
     */
    PACKAGE,

    /**
     * Represents the private access level in the {@link AccessLevel} enumeration.
     * This level signifies that the associated entity is accessible only within its
     * own class.
     */
    PRIVATE,

    /**
     * Represents the module access level in the {@link AccessLevel} enumeration.
     * This level signifies that the associated entity is accessible only within a specific module,
     * adhering to the module boundaries defined in the Java Platform Module System (JPMS).
     */
    MODULE,

    /**
     * Represents the absence of any access level in the {@link AccessLevel} enumeration.
     * This level signifies that no access modifier is explicitly defined for the associated entity.
     */
    NONE;

    /**
     * Converts the current {@code AccessLevel} enum instance to its corresponding
     * Java access modifier string representation.
     *
     * @return the Java modifier string representing the current access level.
     *         For {@code PUBLIC}, {@code PROTECTED}, and {@code PRIVATE}, the corresponding
     *         strings "public", "protected", and "private" are returned. For {@code PACKAGE},
     *         {@code MODULE}, and {@code NONE}, an empty string is returned.
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
