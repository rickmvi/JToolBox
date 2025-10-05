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
package com.github.rickmvi.jtoolbox.control.internal;

import com.github.rickmvi.jtoolbox.control.For;
import com.github.rickmvi.jtoolbox.control.If;
import org.jetbrains.annotations.ApiStatus;
import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ForInt implements For<Integer> {

    private final int     START;
    private final int     END;
    private final int     STEP;
    private final boolean REVERSED;

    @Override
    public void forEach(Consumer<Integer> action) {
        If.isTrue(REVERSED, () -> {
            for (int i = START; i >= END; i -= STEP) {
                action.accept(i);
            }
        }).orElse(() -> {
            for (int i = START; i <= END; i += STEP) {
                action.accept(i);
            }
        });
    }

    @Override
    public CompletableFuture<Void> forEachAsync(Consumer<Integer> action) {
        return CompletableFuture.runAsync(() -> forEach(action));
    }

    @Override
    public boolean filter(Predicate<Integer> predicate) {
        final boolean[] found = {false};
        forEach(i -> {
            if (predicate.test(i)) found[0] = true;
        });
        return found[0];
    }

    @Override
    public Integer findFirst(Predicate<Integer> predicate) {
        if (REVERSED) {
            for (int i = START; i >= END; i -= STEP) {
                if (predicate.test(i)) return i;
            }
        } else {
            for (int i = START; i <= END; i += STEP) {
                if (predicate.test(i)) return i;
            }
        }
        return null;
    }

    public Integer findLast(Predicate<Integer> predicate) {
        Integer last = null;
        if (REVERSED) {
            for (int i = START; i >= END; i -= STEP) {
                if (predicate.test(i)) last = i;
            }
        } else {
            for (int i = START; i <= END; i += STEP) {
                if (predicate.test(i)) last = i;
            }
        }
        return last;
    }

    @Override
    public <R> R findFirstValue(Function<Integer, R> mapper) {
        if (REVERSED) {
            for (int i = START; i >= END; i -= STEP) {
                R result = mapper.apply(i);
                if (result != null) return result;
            }
        } else {
            for (int i = START; i <= END; i += STEP) {
                R result = mapper.apply(i);
                if (result != null) return result;
            }
        }
        return null;
    }

    @Override
    public <R> List<R> collect(Function<Integer, R> mapper) {
        List<R> results = new ArrayList<>();
        forEach(i -> {
            R result = mapper.apply(i);
            if (result != null) results.add(result);
        });
        return results;
    }

}
