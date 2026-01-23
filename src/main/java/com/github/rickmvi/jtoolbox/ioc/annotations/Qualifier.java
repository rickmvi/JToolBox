package com.github.rickmvi.jtoolbox.ioc.annotations;

import java.lang.annotation.*;

/**
 * Annotation used to resolve ambiguity when multiple beans of the same type
 * are present in the IoC container.
 *
 * <p>While {@link Primary} defines a default candidate, {@code @Qualifier}
 * provides specific control at the injection point. It can be used on fields,
 * constructor parameters, or method parameters to pinpoint the exact bean name
 * to be wired.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * public interface MessageService { ... }
 *
 * &#64;Component("emailService")
 * public class EmailService implements MessageService { ... }
 *
 * &#64;Component("smsService")
 * public class SmsService implements MessageService { ... }
 *
 * &#64;Component
 * public class NotificationManager {
 * &#64;Autowired
 * &#64;Qualifier("smsService") // Specifically requests the bean named "smsService"
 * private MessageService service;
 * }
 * </pre>
 *
 * @author rickmvi
 * @since 1.0.0
 * @see Autowired
 * @see Inject
 * @see Primary
 * @see Component
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

    /**
     * The name of the specific bean to be injected.
     * <p>This value must match the name defined in the {@link Component#value()}
     * or the {@link Bean#value()}.</p>
     *
     * @return the name of the target bean.
     */
    String value();
}