package com.github.rickmvi.jtoolbox.util.timer;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class Scheduler {

    private static final ScheduledExecutorService EXECUTOR =
            Executors.newScheduledThreadPool(1, r -> {
                Thread thread = new Thread(r, "TimerScheduler");
                thread.setDaemon(true);
                return thread;
            });

    private static final ExecutorService NOTIFICATION_EXECUTOR =
            Executors.newCachedThreadPool(r -> {
                Thread thread = new Thread(r, "TimerNotifications");
                thread.setDaemon(true);
                return thread;
            });

    public static @NotNull ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodMillis) {
        return EXECUTOR.scheduleAtFixedRate(task, 0, periodMillis, TimeUnit.MILLISECONDS);
    }

    public static void executeNotification(Runnable task) {
        NOTIFICATION_EXECUTOR.execute(task);
    }

    public static void shutdown() {
        EXECUTOR.shutdown();
        NOTIFICATION_EXECUTOR.shutdown();
    }

}
