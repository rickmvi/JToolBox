package com.github.rickmvi.jtoolbox.control;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@lombok.experimental.UtilityClass
public class DoWhile {
    /**
     * Executes the action once and continues as long as the condition returns true (do-while loop).
     *
     * @param condition the condition to evaluate after each iteration
     * @param action    the action to execute
     */
    public void doWhile(@NotNull BooleanSupplier condition, @NotNull Runnable action) {
        do action.run(); while (condition.getAsBoolean());
    }

    /**
     * Executes an action at least once and keeps repeating it while the
     * condition over the last result remains true. Returns the last result.
     *
     * <p>Typical usage: read input until it becomes valid and get the final value.</p>
     *
     * <pre>{@code
     * int option = Flow.doWhile(
     *     () -> {
     *         Out.printFormatted("Enter 0 for {} or 1 for {}: ", "exit", "continue");
     *         return ScannerUtils.nextInt();
     *     },
     *     opt -> opt != 0 && opt != 1 // continue while invalid
     * );
     * }</pre>
     *
     * @param action the action to execute each iteration, producing a result
     * @param continueCondition the condition that, given the last result, determines whether to continue
     * @param <T> the result type
     * @return the last result produced by {@code action}
     */
    public <T> T doWhile(@NotNull Supplier<T> action, @NotNull Predicate<T> continueCondition) {
        T result;
        do {
            result = action.get();
        } while (continueCondition.test(result));
        return result;
    }
}
