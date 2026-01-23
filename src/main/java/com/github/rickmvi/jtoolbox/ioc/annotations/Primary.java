package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a bean should be given preference when multiple candidates
 * are qualified to be autowired to a single-valued dependency.
 * <p>If exactly one 'primary' bean exists among the candidates, it will
 * be the autowired value. This is particularly useful when you have a
 * default implementation and multiple specialized ones.</p>
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * public interface StorageService { ... }
 * &#64;Primary
 * &#64;Component
 * public class S3StorageService implements StorageService { ... }
 * &#64;Component
 * public class LocalStorageService implements StorageService { ... }
 * // The S3StorageService will be injected here because it is marked as @Primary
 * &#64;Autowired
 * private StorageService storageService;
 * </pre>
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Autowired
 * @see Inject
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {
}