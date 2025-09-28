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
package com.github.rickmvi.jtoolbox.collections.queue;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Utility class for performing common operations on {@link Queue} collections.
 * Provides methods for filtering, mapping, checking, and safe access of queue elements.
 *
 * <p>Part of the Console API - JToolbox.</p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@UtilityClass
public class Queuing {

    /**
     * Filters the given queue using a predicate and returns a new queue with the matched elements.
     *
     * @param queue     the source queue to filter
     * @param predicate the condition to apply to each element
     * @param <T>       the type of elements in the queue
     * @return a new queue containing only the elements that match the predicate
     */
    public static <T> Queue<T> filterQueue(@NotNull Queue<T> queue, Predicate<T> predicate) {
        return queue.stream().filter(predicate).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Transforms the elements of a queue into another type using a mapping function.
     *
     * @param queue  the source queue
     * @param mapper the function to apply to each element
     * @param <T>    the type of input elements
     * @param <R>    the type of output elements
     * @return a new queue with the mapped elements
     */
    public static <T, R> Queue<R> mapQueue(@NotNull Queue<T> queue, Function<T, R> mapper) {
        return queue.stream().map(mapper).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Safely retrieves the head of the queue without removing it, or null if the queue is empty.
     *
     * @param queue the queue to peek
     * @param <T>   the type of elements
     * @return the head element or null if the queue is empty
     */
    @Contract(pure = true)
    public static <T> T peekOrNull(@NotNull Queue<T> queue) {
        return queue.peek();
    }

    /**
     * Retrieves and removes the head of the queue, or returns the fallback if empty.
     *
     * @param queue    the queue to poll
     * @param fallback the value to return if the queue is empty
     * @param <T>      the type of elements
     * @return the head of the queue or fallback
     */
    public static <T> T pollOrDefault(@NotNull Queue<T> queue, T fallback) {
        return queue.isEmpty() ? fallback : queue.poll();
    }

    /**
     * Checks whether the queue contains at least one element that matches the given predicate.
     *
     * @param queue     the queue to check
     * @param predicate the condition to apply
     * @param <T>       the type of elements
     * @return true if any element matches the predicate, false otherwise
     */
    public static <T> boolean containsInQueue(@NotNull Queue<T> queue, Predicate<T> predicate) {
        return queue.stream().anyMatch(predicate);
    }

    /**
     * Applies an action to each element in the queue.
     *
     * @param queue  the queue to iterate
     * @param action the action to perform on each element
     * @param <T>    the type of elements
     */
    public static <T> void forEachQueue(@NotNull Queue<T> queue, Consumer<T> action) {
        queue.forEach(action);
    }
}
