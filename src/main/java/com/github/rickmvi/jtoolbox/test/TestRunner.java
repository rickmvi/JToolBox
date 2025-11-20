package com.github.rickmvi.jtoolbox.test;

import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.test.annotations.JTest;
import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import com.github.rickmvi.jtoolbox.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.rickmvi.jtoolbox.console.IO.*;

/**
 * A utility class designed to execute test methods annotated with {@code @JTest}
 * within a specified test class. The {@code TestRunner} provides functionality
 * to initialize the test class, invoke its test methods, and generate a summary
 * of the test execution results.
 */
public class TestRunner {

    private static final String HEADER_TEMPLATE;
    private static final String TEST_EXECUTION_TEMPLATE;
    private static final String SUCCESS_MESSAGE;
    private static final String ASSERTION_FAILURE_MESSAGE;
    private static final String NON_ASSERTION_ERROR_MESSAGE;
    private static final String INDENT_PREFIX;
    private static final String SUMMARY_SEPARATOR;
    private static final String SUMMARY_TEMPLATE;
    private static final String INSTANTIATION_FATAL_ERROR_MESSAGE;

    static {
        HEADER_TEMPLATE = "--- JToolBox TestRunner: {} ---$n";
        TEST_EXECUTION_TEMPLATE = "  -> Running {}...$n";
        SUCCESS_MESSAGE = " ✅ SUCCESS";
        ASSERTION_FAILURE_MESSAGE = " ❌ FAILURE\n";
        NON_ASSERTION_ERROR_MESSAGE = " ❌ ERROR (Non-assertive exception)";
        INDENT_PREFIX = "     ";
        SUMMARY_SEPARATOR = "----------------------------------------";
        SUMMARY_TEMPLATE = "TOTAL: {} | SUCCESS: {} | FAILURE: {}$n";
        INSTANTIATION_FATAL_ERROR_MESSAGE = "FATAL ERROR: Unable to instantiate test class.";
    }

    private final Class<?> testClass;

    public TestRunner(Class<?> testClass) {
        this.testClass = testClass;
    }

    /**
     * Creates a new instance of {@code TestRunner} for the provided test class.
     * This method is designed to facilitate the execution of all test methods
     * annotated with {@code @JTest} within the specified test class.
     *
     * @param testClass The {@link Class} object representing the test class whose
     *                  test methods will be executed. This class must have a
     *                  default no-argument constructor.
     * @return An instance of {@code TestRunner} initialized with the specified
     *         test class.
     * @throws NullPointerException If {@code testClass} is {@code null}.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TestRunner run(Class<?> testClass) {
        return new TestRunner(testClass);
    }

    /**
     * Executes all methods in the provided test class that are annotated with {@code @JTest}.
     * Tracks the total number of tests executed, the number of tests that passed,
     * and the number of tests that failed. Returns a summary of the test run.
     *
     * @return A {@code RunSummary} object containing the total number of tests,
     *         number of successful tests, and number of failed tests.
     * @throws RuntimeException if the test class cannot be instantiated or
     *         if any other unexpected error occurs during the execution of test methods.
     */
    public AssessmentSummary execute() {
        AtomicInteger totalTests  = new AtomicInteger();
        AtomicInteger passedTests = new AtomicInteger();
        AtomicInteger failedTests = new AtomicInteger();

        printHeader();

        Try.runThrowing(() ->
                runAnnotatedTests(totalTests, passedTests, failedTests)
        ).onFailure(this::logInstantiationFailure);

        AssessmentSummary summary = new AssessmentSummary(totalTests.get(), passedTests.get(), failedTests.get());
        printSummary(summary);
        return summary;
    }

    private void runAnnotatedTests(AtomicInteger totalTests,
                                   AtomicInteger passedTests,
                                   AtomicInteger failedTests) throws ReflectiveOperationException {
        Object instance = testClass.getDeclaredConstructor().newInstance();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(JTest.class)) {
                executeTestMethod(instance, method, totalTests, passedTests, failedTests);
            }
        }
    }

    private void executeTestMethod(Object instance,
                                   @NotNull Method method,
                                   @NotNull AtomicInteger totalTests,
                                   AtomicInteger passedTests,
                                   AtomicInteger failedTests) {
        totalTests.incrementAndGet();
        format(TEST_EXECUTION_TEMPLATE, method.getName());
        try {
            method.invoke(instance);
            passedTests.incrementAndGet();
            println(SUCCESS_MESSAGE);
        } catch (AssertionException e) {
            failedTests.incrementAndGet();
            err(ASSERTION_FAILURE_MESSAGE);
            err(INDENT_PREFIX + e.getMessage());
        } catch (Throwable e) {
            failedTests.incrementAndGet();
            err(NON_ASSERTION_ERROR_MESSAGE);
            err(INDENT_PREFIX + e);
        }
    }

    private void printHeader() {
        format((HEADER_TEMPLATE), testClass.getSimpleName());
    }

    private void printSummary(@NotNull AssessmentSummary summary) {
        println(SUMMARY_SEPARATOR);
        format(SUMMARY_TEMPLATE, summary.total, summary.passed, summary.failed);
        println(SUMMARY_SEPARATOR + "\n");
    }

    private void logInstantiationFailure(Throwable throwable) {
        Logger.error(INSTANTIATION_FATAL_ERROR_MESSAGE, throwable, throwable.getMessage());
    }

    record AssessmentSummary(int total, int passed, int failed) {
    }

}