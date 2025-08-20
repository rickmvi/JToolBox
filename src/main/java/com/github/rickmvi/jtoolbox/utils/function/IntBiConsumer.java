package com.github.rickmvi.jtoolbox.utils.function;

@FunctionalInterface
public interface IntBiConsumer {
    void accept(int i, int j);

    default IntBiConsumer andThen(IntBiConsumer after) {
        return (i, j) -> {
            accept(i, j);
            after.accept(i, j);
        };
    }
}
