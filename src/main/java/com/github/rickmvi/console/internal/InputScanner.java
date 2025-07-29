/*
 * Console API - Biblioteca utilitária para entrada, saída e formatação no console.
 * Copyright (C) 2025  Rick M. Viana
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 */
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
