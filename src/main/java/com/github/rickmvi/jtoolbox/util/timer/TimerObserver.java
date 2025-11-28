package com.github.rickmvi.jtoolbox.util.timer;

public interface TimerObserver {

    default void onStart(Timer timer) {}
    default void onEnd(Timer timer) {}
    default void onTick(Timer timer, long elapsedSeconds, long remainingSeconds) {}
    default void onZero(Timer timer) {}
    default void onRestart(Timer timer) {}

}
