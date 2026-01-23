package com.github.rickmvi.jtoolbox.test;

import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.test.annotations.*;
import com.github.rickmvi.jtoolbox.test.exceptions.AssertionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced test runner with lifecycle hooks, parameterized tests, and detailed reporting.
 * Supports @BeforeAll, @BeforeEach, @AfterEach, @AfterAll, @Disabled, and @DisplayName.
 *
 * @author Rick M. Viana
 * @version 2.0
 * @since 2025
 */
public class TestRunner {

    private static final Logger LOGGER = Logger.getLogger(TestRunner.class);

    private final Class<?> testClass;
    private final TestConfiguration config;

    public TestRunner(Class<?> testClass) {
        this(testClass, TestConfiguration.defaults());
    }

    public TestRunner(Class<?> testClass, TestConfiguration config) {
        this.testClass = testClass;
        this.config = config;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TestRunner run(Class<?> testClass) {
        return new TestRunner(testClass);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TestRunner run(Class<?> testClass, TestConfiguration config) {
        return new TestRunner(testClass, config);
    }

    /**
     * Executes all test methods with full lifecycle support.
     */
    public TestReport execute() {
        TestReport report = new TestReport(testClass.getSimpleName());
        Instant startTime = Instant.now();

        printHeader();

        try {
            Object instance = testClass.getDeclaredConstructor().newInstance();

            // Get all test methods
            List<Method> testMethods = getTestMethods();
            List<Method> beforeAllMethods = getAnnotatedMethods(BeforeAll.class);
            List<Method> afterAllMethods = getAnnotatedMethods(AfterAll.class);
            List<Method> beforeEachMethods = getAnnotatedMethods(BeforeEach.class);
            List<Method> afterEachMethods = getAnnotatedMethods(AfterEach.class);

            // Run @BeforeAll methods
            runLifecycleMethods(instance, beforeAllMethods, "@BeforeAll");

            // Run each test
            for (Method testMethod : testMethods) {
                executeTest(instance, testMethod, beforeEachMethods, afterEachMethods, report);
            }

            // Run @AfterAll methods
            runLifecycleMethods(instance, afterAllMethods, "@AfterAll");

        } catch (Exception e) {
            LOGGER.error("FATAL: Unable to instantiate test class", e);
            report.addError("Class instantiation failed: " + e.getMessage());
        }

        report.setDuration(Duration.between(startTime, Instant.now()));
        printReport(report);
        return report;
    }

    private void executeTest(Object instance,
                             Method testMethod,
                             List<Method> beforeEachMethods,
                             List<Method> afterEachMethods,
                             TestReport report) {

        String testName = getTestDisplayName(testMethod);

        // Check if test is disabled
        if (testMethod.isAnnotationPresent(Disabled.class)) {
            String reason = testMethod.getAnnotation(Disabled.class).value();
            report.addSkipped(testName, reason);
            printTestSkipped(testName, reason);
            return;
        }

        Instant testStart = Instant.now();
        printTestStart(testName);

        try {
            // Run @BeforeEach
            runLifecycleMethods(instance, beforeEachMethods, "@BeforeEach");

            // Run the actual test
            testMethod.setAccessible(true);
            testMethod.invoke(instance);

            // Test passed
            Duration duration = Duration.between(testStart, Instant.now());
            report.addSuccess(testName, duration);
            printTestSuccess(testName, duration);

        } catch (Throwable e) {
            Duration duration = Duration.between(testStart, Instant.now());
            Throwable cause = e.getCause() != null ? e.getCause() : e;

            if (cause instanceof AssertionException) {
                report.addFailure(testName, cause.getMessage(), duration);
                printTestFailure(testName, cause.getMessage());
            } else {
                report.addError(testName, cause, duration);
                printTestError(testName, cause);
            }
        } finally {
            // Run @AfterEach (even if test failed)
            try {
                runLifecycleMethods(instance, afterEachMethods, "@AfterEach");
            } catch (Exception e) {
                LOGGER.error("Error in @AfterEach", e);
            }
        }
    }

    private void runLifecycleMethods(Object instance, List<Method> methods, String phase)
            throws Exception {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (Exception e) {
                throw new Exception(phase + " method failed: " + method.getName(), e);
            }
        }
    }

    private List<Method> getTestMethods() {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(JTest.class))
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
    }

    private List<Method> getAnnotatedMethods(Class<? extends java.lang.annotation.Annotation> annotation) {
        return Arrays.stream(testClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private String getTestDisplayName(Method method) {
        if (method.isAnnotationPresent(DisplayName.class)) {
            return method.getAnnotation(DisplayName.class).value();
        }
        return method.getName();
    }

    // ==================== Console Output Methods ====================

    private void printHeader() {
        if (config.isVerbose()) {
            System.out.printf("╔══════════════════════════════════════════════════════════════╗%n");
            System.out.printf("║  JToolBox TestRunner: %-38s ║%n", testClass.getSimpleName());
            System.out.printf("╚══════════════════════════════════════════════════════════════╝%n");
        }
    }

    private void printTestStart(String testName) {
        if (config.isVerbose()) {
            System.out.printf("  ➤ Running: %s%n", testName);
        }
    }

    private void printTestSuccess(String testName, Duration duration) {
        String timeStr = formatDuration(duration);
        if (config.isVerbose()) {
            System.out.printf("    ✅ SUCCESS (%s)%n", timeStr);
        } else {
            System.out.print(".");
        }
    }

    private void printTestFailure(String testName, String message) {
        if (config.isVerbose()) {
            System.out.printf("    ❌ FAILURE%n");
            System.out.printf("       %s%n", message);
        } else {
            System.out.print("F");
        }
    }

    private void printTestError(String testName, Throwable error) {
        if (config.isVerbose()) {
            System.out.printf("    ❌ ERROR%n");
            System.out.printf("       %s: %s%n", error.getClass().getSimpleName(), error.getMessage());
            if (config.isPrintStackTraces()) {
                error.printStackTrace(System.out);
            }
        } else {
            System.out.print("E");
        }
    }

    private void printTestSkipped(String testName, String reason) {
        if (config.isVerbose()) {
            System.out.printf("    ⊘ SKIPPED: %s%n", reason.isEmpty() ? "No reason provided" : reason);
        } else {
            System.out.print("S");
        }
    }

    private void printReport(TestReport report) {
        if (!config.isVerbose()) {
            System.out.println(); // New line after dots
        }

        System.out.println("\n" + "=".repeat(64));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(64));
        System.out.printf("Tests run: %d | Passed: %d | Failed: %d | Errors: %d | Skipped: %d%n",
                report.getTotal(), report.getPassed(), report.getFailed(),
                report.getErrors(), report.getSkipped());
        System.out.printf("Duration: %s%n", formatDuration(report.getDuration()));

        if (report.getFailureMessages().size() > 0) {
            System.out.println("\n" + "-".repeat(64));
            System.out.println("FAILURES:");
            report.getFailureMessages().forEach((name, msg) -> {
                System.out.printf("  • %s%n    → %s%n", name, msg);
            });
        }

        if (report.getErrorMessages().size() > 0) {
            System.out.println("\n" + "-".repeat(64));
            System.out.println("ERRORS:");
            report.getErrorMessages().forEach((name, error) -> {
                System.out.printf("  • %s%n    → %s: %s%n",
                        name, error.getClass().getSimpleName(), error.getMessage());
            });
        }

        System.out.println("=".repeat(64) + "\n");
    }

    private String formatDuration(Duration duration) {
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else {
            return String.format("%.2fs", millis / 1000.0);
        }
    }

    // ==================== Configuration ====================

    public static class TestConfiguration {
        private final boolean verbose;
        private final boolean printStackTraces;
        private final boolean stopOnFailure;

        private TestConfiguration(Builder builder) {
            this.verbose = builder.verbose;
            this.printStackTraces = builder.printStackTraces;
            this.stopOnFailure = builder.stopOnFailure;
        }

        public static TestConfiguration defaults() {
            return builder().build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public boolean isVerbose() { return verbose; }
        public boolean isPrintStackTraces() { return printStackTraces; }
        public boolean isStopOnFailure() { return stopOnFailure; }

        public static class Builder {
            private boolean verbose = true;
            private boolean printStackTraces = false;
            private boolean stopOnFailure = false;

            public Builder verbose(boolean verbose) {
                this.verbose = verbose;
                return this;
            }

            public Builder printStackTraces(boolean print) {
                this.printStackTraces = print;
                return this;
            }

            public Builder stopOnFailure(boolean stop) {
                this.stopOnFailure = stop;
                return this;
            }

            public TestConfiguration build() {
                return new TestConfiguration(this);
            }
        }
    }

    // ==================== Test Report ====================

    public static class TestReport {
        private final String className;
        private final List<TestResult> results = new ArrayList<>();
        private final Map<String, String> failureMessages = new LinkedHashMap<>();
        private final Map<String, Throwable> errorMessages = new LinkedHashMap<>();
        private final Set<String> skippedTests = new LinkedHashSet<>();
        private Duration duration = Duration.ZERO;

        public TestReport(String className) {
            this.className = className;
        }

        public void addSuccess(String testName, Duration duration) {
            results.add(new TestResult(testName, TestStatus.PASSED, duration));
        }

        public void addFailure(String testName, String message, Duration duration) {
            results.add(new TestResult(testName, TestStatus.FAILED, duration));
            failureMessages.put(testName, message);
        }

        public void addError(String testName, Throwable error, Duration duration) {
            results.add(new TestResult(testName, TestStatus.ERROR, duration));
            errorMessages.put(testName, error);
        }

        public void addError(String message) {
            errorMessages.put("Initialization", new RuntimeException(message));
        }

        public void addSkipped(String testName, String reason) {
            results.add(new TestResult(testName, TestStatus.SKIPPED, Duration.ZERO));
            skippedTests.add(testName + (reason.isEmpty() ? "" : ": " + reason));
        }

        public void setDuration(Duration duration) { this.duration = duration; }

        public int getTotal() { return results.size(); }
        public int getPassed() { return (int) results.stream().filter(r -> r.status == TestStatus.PASSED).count(); }
        public int getFailed() { return (int) results.stream().filter(r -> r.status == TestStatus.FAILED).count(); }
        public int getErrors() { return (int) results.stream().filter(r -> r.status == TestStatus.ERROR).count(); }
        public int getSkipped() { return (int) results.stream().filter(r -> r.status == TestStatus.SKIPPED).count(); }
        public Duration getDuration() { return duration; }
        public Map<String, String> getFailureMessages() { return failureMessages; }
        public Map<String, Throwable> getErrorMessages() { return errorMessages; }
        public boolean allTestsPassed() { return getFailed() == 0 && getErrors() == 0; }

        private static class TestResult {
            String name;
            TestStatus status;
            Duration duration;

            TestResult(String name, TestStatus status, Duration duration) {
                this.name = name;
                this.status = status;
                this.duration = duration;
            }
        }

        private enum TestStatus {
            PASSED, FAILED, ERROR, SKIPPED
        }
    }
}