package com.github.rickmvi.console;

import org.jetbrains.annotations.NotNull;

public interface InputScanner {

    void init();

    void init(@NotNull java.util.Scanner scanner);

    void locale(@NotNull Location location);

    boolean hasNext();

    boolean hasNextLine();

    String next();

    String next(@NotNull String pattern);

    String nextLine();

    int nextInt();

    long nextLong();

    float nextFloat();

    double nextDouble();

    boolean nextBoolean();

    String nextSafe();

    void close();
}
