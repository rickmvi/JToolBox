package com.github.rickmvi.jtoolbox.util.timer;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class Timer {

    private final Mode mode;
    private final long initialSeconds;
    private final long tickMillis;

    private final List<TimerObserver> observers = new CopyOnWriteArrayList<>();

    private Instant start;
    private Instant end;

    private final long remainingSeconds;
    private volatile long elapsedSeconds;

    private ScheduledFuture<?> scheduledTask;
    private final AtomicBoolean running = new AtomicBoolean(false);

    Timer(Mode mode, long initialSeconds, long tickMillis, List<TimerObserver> initialObservers) {
        this.mode = mode;
        this.initialSeconds = initialSeconds;
        this.tickMillis = tickMillis;

        if (initialObservers != null)
            observers.addAll(initialObservers);

        this.remainingSeconds = initialSeconds;
    }

    public synchronized Timer start() {
        if (running.get()) return this;

        start = Instant.now();
        running.set(true);

        notifyStart();
        scheduleInternalTick();

        return this;
    }

    public synchronized Timer restart() {
        reset();
        notifyRestart();
        return start();
    }

    public synchronized Timer stop() {
        running.set(false);
        if (scheduledTask != null) scheduledTask.cancel(false);
        end = Instant.now();
        notifyEnd();
        return this;
    }

    public synchronized Timer reset() {
        stop();
        start = null;
        end = null;
        elapsedSeconds = 0;
        return this;
    }

    private void scheduleInternalTick() {
        scheduledTask = Scheduler.scheduleAtFixedRate(this::tick, tickMillis);
    }

    private void tick() {
        if (!running.get() || start == null) return;

        long currentElapsed = Duration.between(start, Instant.now()).getSeconds();

        if (currentElapsed > elapsedSeconds) {
            elapsedSeconds = currentElapsed;

            if (mode != Mode.CHRONOMETER) {
                long currentRemaining = initialSeconds - elapsedSeconds;
                notifyTick(currentRemaining);

                if (currentRemaining <= 0) {
                    elapsedSeconds = initialSeconds;
                    notifyZero();
                    stop();
                }

                return;
            }

            notifyTick(elapsedSeconds);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public Duration getDuration() {
        if (start == null) return Duration.ZERO;
        Instant effectiveEnd = (end != null ? end : Instant.now());
        return Duration.between(start, effectiveEnd);
    }

    public Timer addObserver(TimerObserver observer) {
        observers.add(observer);
        return this;
    }

    private void notifyStart()   {
        observers.forEach(o -> Scheduler.executeNotification(() -> o.onStart(this)));
    }

    private void notifyEnd()     {
        observers.forEach(o -> Scheduler.executeNotification(() -> o.onEnd(this)));
    }

    private void notifyRestart() {
        observers.forEach(o -> Scheduler.executeNotification(() -> o.onRestart(this)));
    }

    private void notifyZero()    {
        observers.forEach(o -> Scheduler.executeNotification(() -> o.onZero(this)));
    }

    private void notifyTick(long time) {
        observers.forEach(o -> Scheduler.executeNotification(() -> o.onTick(this, time, remainingSeconds)));
    }

    public CompletableFuture<Void> awaitZeroAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        addObserver(new TimerObserver() {
            @Override
            public void onZero(Timer timer) {
                future.complete(null);
            }
        });

        return future;
    }

}
