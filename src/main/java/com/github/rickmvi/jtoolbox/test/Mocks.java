package com.github.rickmvi.jtoolbox.test;

import java.util.function.Supplier;

/**
 * A utility class designed to create mock suppliers that return predefined results
 * or execute custom logic as specified.
 */
public interface Mocks {

    /**
     * Creates a supplier that returns predefined results or performs customized logic
     * as defined by the provided {@link Supplier}.
     *
     * @param <T>       The type of the object returned by the supplier.
     * @param stubLogic The supplier specifying the custom logic or predefined responses.
     *                  It must not be {@code null}.
     * @return The same {@link Supplier} provided as input.
     * @throws NullPointerException If {@code stubLogic} is {@code null}.
     */
    static <T> Supplier<T> stub(Supplier<T> stubLogic) {
        return stubLogic;
    }
}
