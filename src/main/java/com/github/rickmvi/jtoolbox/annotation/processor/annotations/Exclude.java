package com.github.rickmvi.jtoolbox.annotation.processor.annotations;

import java.lang.annotation.*;

/**
 * Marks a field to be excluded from all automatically generated methods.
 * <p>When a field is annotated with {@code @Exclude}, it will be ignored by:
 * <ul>
 * <li>{@link Getter} and {@link Setter} (when applied at class level).</li>
 * <li>{@link ToString} (field won't appear in the string).</li>
 * <li>{@link EqualsAndHashCode} (field won't be part of the identity).</li>
 * <li>{@link Builder} (no setter will be created in the builder).</li>
 * </ul>
 * </p>
 *
 * <p><strong>Key Use Cases:</strong></p>
 * <ul>
 * <li><strong>Security:</strong> Excluding password or token fields from {@code toString()}.</li>
 * <li><strong>Performance:</strong> Excluding heavy objects or buffers from {@code equals/hashCode}.</li>
 * <li><strong>Circular References:</strong> Preventing {@code StackOverflowError} in {@code toString()}.</li>
 * </ul>
 *
 * <pre>
 * &#64;Data
 * public class User {
 * private Long id;
 * private String username;
 * &#64;Exclude
 * private String password; // No getter, no setter, not in toString
 * }
 * </pre>
 *
 * @author Rick M. Viana
 * @version 1.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Exclude {
}