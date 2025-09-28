/*
 * Console API - Utilitarian library for input, output and formatting on the console.
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
package com.github.rickmvi.jtoolbox.control.fors;

import org.jetbrains.annotations.ApiStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class ForDouble implements For<Double> {

    private final double  START;
    private final double  END;
    private final double  STEP;
    private final boolean REVERSED;

    @Override
    public void forEach(Consumer<Double> action) {
        if (REVERSED) {
            for (double i = START; i >= END; i -= STEP) {
                action.accept(i);
            }
        } else {
            for (double i = START; i <= END; i += STEP) {
                action.accept(i);
            }
        }
    }

    @Override
    public CompletableFuture<Void> forEachAsync(Consumer<Double> action) {
        return CompletableFuture.runAsync(() -> forEach(action));
    }

    @Override
    public boolean anyMatch(Predicate<Double> predicate) {
        final boolean[] found = {false};
        forEach(i -> { if (predicate.test(i)) found[0] = true; });
        return found[0];
    }

    @Override
    public Double findFirst(Predicate<Double> predicate) {
        if (REVERSED) {
            for (double i = START; i >= END; i -= STEP) {
                if (predicate.test(i)) return i;
            }
        } else {
            for (double i = START; i <= END; i += STEP) {
                if (predicate.test(i)) return i;
            }
        }
        return null;
    }

    @Override
    public Double findLast(Predicate<Double> predicate) {
        Double last = null;
        if (REVERSED) {
            for (double i = START; i >= END; i -= STEP) {
                if (predicate.test(i)) last = i;
            }
        } else {
            for (double i = START; i <= END; i += STEP) {
                if (predicate.test(i)) last = i;
            }
        }
        return last;
    }

    @Override
    public <R> R findFirstValue(Function<Double, R> mapper) {
        if (REVERSED) {
            for (double i = START; i >= END; i -= STEP) { 
                R res = mapper.apply(i); 
                if (res != null) return res; 
            }
        } else {
            for (double i = START; i <= END; i += STEP) { 
                R res = mapper.apply(i); 
                if (res != null) return res; 
            }
        }
        return null;
    }

    @Override
    public <R> List<R> collect(Function<Double, R> mapper) {
        List<R> results = new ArrayList<>();
        forEach(i -> {
            R res = mapper.apply(i);
            if (res != null) results.add(res);
        });
        return results;
    }
}
