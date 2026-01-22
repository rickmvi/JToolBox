package com.github.rickmvi.jtoolbox.concurrent;

import com.github.rickmvi.jtoolbox.control.Condition;
import com.github.rickmvi.jtoolbox.logger.Logger;
import com.github.rickmvi.jtoolbox.util.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.github.rickmvi.jtoolbox.control.Condition.when;

/**
 * A utility class for simplifying threading operations in Java. It provides
 * utility methods for running tasks asynchronously, managing threads, and
 * creating custom thread configurations through a fluent builder API.
 */
@UtilityClass
@SuppressWarnings("unused")
public class Threader {

    private static final ExecutorService DEFAULT_POOL;

    static {
        DEFAULT_POOL = Executors.newCachedThreadPool();
    }

    public static @NotNull ThreadBuilder<Void> of(@NotNull Runnable task) {
        Callable<Void> callable = Executors.callable(task, null);
        return new ThreadBuilder<>(callable);
    }

    public static <T> @NotNull ThreadBuilder<T> of(@NotNull Callable<T> task) {
        return new ThreadBuilder<>(task);
    }

    /* ================================== STATIC UTILITIES ==================================== */

    /**
     * Shuts down the default thread pool used by the application.
     * <p>
     * This method attempts to gracefully terminate all tasks in the default thread pool.
     * It first invokes a shutdown operation, disallowing new tasks from being submitted.
     * Then, it waits for existing tasks to terminate for up to 5 seconds. If the termination
     * does not occur within the specified timeout, the method forces an immediate shutdown
     * of the pool and interrupts any running tasks. If the current thread is interrupted
     * during the wait, the pool is also forcibly shut down, and the thread's interrupted
     * status is preserved.
     *
     * @throws SecurityException if the security manager does not allow the thread pool to shut down.
     */
    public static void shutdownDefaultPool() {
        DEFAULT_POOL.shutdown();
        try {
            if (!DEFAULT_POOL.awaitTermination(5, TimeUnit.SECONDS)) {
                DEFAULT_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            DEFAULT_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Causes the currently executing thread to sleep for the specified number of milliseconds,
     * encapsulating the operation in a {@code Try} instance for safe error handling.
     * <p>
     * If an {@link InterruptedException} occurs during the sleep, the thread's interrupted
     * status is restored.
     *
     * @param milliseconds the length of time to sleep in milliseconds.
     * @return a {@code Try<Void>} representing the result of the sleep operation. If the sleep
     *         is successful, the {@code Try} will be in a success state. Otherwise, it will
     *         contain the exception that caused the failure.
     * @throws IllegalArgumentException if {@code milliseconds} is negative.
     */
    public static @NotNull Try<Void> sleep(long milliseconds) {
        return Try.runThrowing(() -> Thread.sleep(milliseconds))
                .onFailure(t -> {
                    if (t instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    /**
     * Executes the given {@code Runnable} task asynchronously in a new thread.
     * An optional name can be assigned to the thread executing the task.
     *
     * @param task the {@code Runnable} task to be executed; must not be {@code null}.
     * @param name the name to assign to the thread executing the task; may be {@code null}.
     * @throws NullPointerException if {@code task} is {@code null}.
     */
    public static void runAsync(@NotNull Runnable task, @Nullable String name) {
        Thread thread = new Thread(task);
        if (name != null) {
            thread.setName(name);
        }
        thread.start();
    }

    /**
     * Executes the given {@code Runnable} task asynchronously in a new thread.
     * This method uses the default thread creation behavior, and no custom name
     * is assigned to the thread executing the task.
     *
     * @param task the {@code Runnable} task to be executed asynchronously; must not be {@code null}.
     * @throws NullPointerException if {@code task} is {@code null}.
     */
    public static void runAsync(@NotNull Runnable task) {
        runAsync(task, null);
    }

    /* ================================== FLUENT BUILDER ==================================== */

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class ThreadBuilder<T> {

        private final @NotNull Callable<T> task;
        private @Nullable String name;
        private @Nullable ExecutorService executor = DEFAULT_POOL;
        private @Nullable Consumer<T> onSuccess;
        private @Nullable Consumer<Throwable> onError;
        private boolean isDaemon = false;
        private boolean useDedicatedThread = false;

        public @NotNull ThreadBuilder<T> named(@NotNull String threadName) {
            this.name = threadName;
            return this;
        }

        public @NotNull ThreadBuilder<T> daemon() {
            this.isDaemon = true;
            return this;
        }

        public @NotNull ThreadBuilder<T> useSimpleThread() {
            this.useDedicatedThread = true;
            this.executor = null;
            return this;
        }

        public @NotNull ThreadBuilder<T> useExecutor(@NotNull ExecutorService customExecutor) {
            this.executor = customExecutor;
            this.useDedicatedThread = false;
            return this;
        }

        public @NotNull ThreadBuilder<T> onCompletion(@NotNull Consumer<T> action) {
            this.onSuccess = action;
            return this;
        }

        public @NotNull ThreadBuilder<T> onError(@NotNull Consumer<Throwable> action) {
            this.onError = action;
            return this;
        }

        public @Nullable Future<T> start() {
            validateThreadConfiguration();
            if (!useDedicatedThread) {
                return startUsingExecutor();
            }
            return startSimpleThread();
        }

        private void validateThreadConfiguration() {
            Condition.ThrowWhen(useDedicatedThread && isDaemon && name != null, () ->
                    new IllegalStateException(
                            "Cannot set name and daemon property when using Simple Thread and Custom Name in current implementation."
                    )
            );
        }

        private @NotNull Future<T> startUsingExecutor() {
            Condition.ThrowWhen(executor == null, () -> new IllegalStateException("Executor service is null."));
            Callable<T> namedTask = wrapTaskWithThreadRenaming(task, name);
            Future<T> future = executor.submit(namedTask);

            boolean hasCompletionHandlers = (onSuccess != null || onError != null);
            when(hasCompletionHandlers)
                    .apply(() -> monitorFuture(future))
                    .run();

            return future;
        }

        private Callable<T> wrapTaskWithThreadRenaming(Callable<T> originalTask, @Nullable String newName) {
            if (newName == null) {
                return originalTask;
            }
            return () -> {
                Thread currentThread = Thread.currentThread();
                String originalName = currentThread.getName();
                currentThread.setName(newName);
                try {
                    return originalTask.call();
                } finally {
                    currentThread.setName(originalName);
                }
            };
        }

        private @Nullable Future<T> startSimpleThread() {
            Runnable simpleThreadTask = () -> {
                Try<T> executionResult = Try.ofThrowing(task::call);
                executionResult
                        .onSuccess(result -> {
                            if (onSuccess != null) {
                                onSuccess.accept(result);
                            }
                        })
                        .onFailure(e -> {
                            if (onError == null) {
                                Logger.error("Uncaught exception in named thread '{}':", e, name);
                                return;
                            }
                            onError.accept(e);
                        });
            };

            Thread thread = new Thread(simpleThreadTask);
            if (name != null) {
                thread.setName(name);
            }
            if (isDaemon) {
                thread.setDaemon(true);
            }
            thread.start();
            return null;
        }

        private void monitorFuture(Future<T> future) {
            DEFAULT_POOL.execute(() -> {
                Try<T> result = Try.ofThrowing(() -> waitForFuture(future));

                boolean hasSuccessHandler = onSuccess != null;
                boolean hasErrorHandler = onError != null;

                result
                        .onSuccess(value -> {
                            if (hasSuccessHandler) {
                                onSuccess.accept(value);
                            }
                        })
                        .onFailure(error -> {
                            if (!hasErrorHandler) {
                                Logger.error("Uncaught exception in executor task for thread '{}':", error, name);
                                return;
                            }
                            onError.accept(error);
                        });
            });
        }

        private @Nullable T waitForFuture(@NotNull Future<T> future) throws InterruptedException {
            try {
                return future.get();
            } catch (CancellationException ignored) {
                return null;
            } catch (ExecutionException e) {
                throw (e.getCause() instanceof RuntimeException runtime)
                        ? runtime
                        : new RuntimeException(e.getCause());
            }
        }
    }
}