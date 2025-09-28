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

import com.github.rickmvi.jtoolbox.control.ifs.If;
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
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ForIterable<T> implements For<T> {

    private final Iterable<T> iterable;

    @Override
    public void forEach(Consumer<T> action) {
        for (T item: iterable) {
            action.accept(item);
        }
    }

    @Override
    public CompletableFuture<Void> forEachAsync(Consumer<T> action) {
        return CompletableFuture.runAsync(() -> forEach(action));
    }

    @Override
    public boolean anyMatch(Predicate<T> predicate) {
        for (T item: iterable) {
            if (predicate.test(item)) return true;
        }
        return false;
    }

    @Override
    public T findFirst(Predicate<T> predicate) {
        for (T item: iterable) {
            if (predicate.test(item)) return item;
        }
        return null;
    }

    @Override
    public T findLast(Predicate<T> predicate) {
        T last = null;
        for (T item: iterable) {
            T finalLast = last;
            last = If.supplyTrue(predicate.test(item), () -> item).orElse(() -> finalLast);
        }
        return last;
    }

    @Override
    public <R> R findFirstValue(Function<T, R> mapper) {
        for (T item: iterable) {
            R result = mapper.apply(item);
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public <R> List<R> collect(Function<T, R> mapper) {
        List<R> results = new ArrayList<>();
        for (T item: iterable) {
            R mapped = mapper.apply(item);
            If.runTrue(mapped != null, () -> results.add(mapped)).run();
        }
        return results;
    }

}
