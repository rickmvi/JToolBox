package com.github.rickmvi.jtoolbox.util.timer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TimerBuilder {

    private Mode mode = Mode.CHRONOMETER;
    private long initialSeconds = 0;
    private long tickMillis = 1000;

    private final List<TimerObserver> observers = new ArrayList<>();

    @Contract(" -> new")
    public static @NotNull TimerBuilder create() {
        return new TimerBuilder();
    }

    public TimerBuilder stopwatch(long seconds) {
        this.mode = Mode.STOPWATCH;
        this.initialSeconds = seconds;
        return this;
    }

    public TimerBuilder chronometer() {
        this.mode = Mode.CHRONOMETER;
        return this;
    }

    public TimerBuilder tickEvery(long millis) {
        this.tickMillis = millis;
        return this;
    }

    public TimerBuilder addObserver(TimerObserver observer) {
        observers.add(observer);
        return this;
    }

    public Timer build() {
        return new Timer(mode, initialSeconds, tickMillis, observers);
    }

}
