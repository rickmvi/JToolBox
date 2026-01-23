package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Stereotype annotation indicating that an annotated class is a "Service".
 *
 * <p>Originally defined by Domain-Driven Design (DDD) as an operation offered as
 * an interface that stands alone in the model, with no encapsulated state.</p>
 *
 * <p>In the JToolbox framework, {@code @Service} is a specialized form of
 * {@link Component}. It serves as a marker for the business logic layer, where
 * complex processing, calculations, and orchestration between multiple
 * repositories take place.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;Service("userService")
 * public class UserServiceImpl implements UserService {
 *
 * &#64;Autowired
 * private UserRepository repository;
 *
 * public void registerUser(User user) {
 * // Business validation and logic
 * repository.save(user);
 * }
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Component
 * @see Repository
 * @see RestController
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a bean in case of an autodetected component.
     *
     * <p>If left empty, the JToolbox container will generate a default name
     * based on the class name (e.g., "UserService" becomes "userService").</p>
     *
     * @return the suggested bean name, if any.
     */
    String value() default "";
}