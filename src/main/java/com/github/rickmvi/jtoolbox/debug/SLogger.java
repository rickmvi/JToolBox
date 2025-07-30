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
package com.github.rickmvi.jtoolbox.console.debug;

import com.github.rickmvi.jtoolbox.console.formatter.Formatted;
import com.github.rickmvi.jtoolbox.console.Out;

import org.jetbrains.annotations.Contract;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@lombok.experimental.UtilityClass
public class SLogger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<LogLevel> ENABLED_LEVELS = EnumSet.complementOf(EnumSet.of(LogLevel.OFF));

    @lombok.Getter(value = lombok.AccessLevel.PUBLIC, onMethod_ = @Contract(pure = true))
    @lombok.Setter(value = lombok.AccessLevel.PUBLIC)
    private static boolean useAnsiColor = false;

    @Contract(pure = true)
    private String colorize(String message, String color) {
        return useAnsiColor ? color + message + AnsiColor.RESET.getAnsiCode() : message;
    }

    public void log(LogLevel level, String message) {
        if (!ENABLED_LEVELS.contains(level) || level == LogLevel.OFF) return;

        String time = FORMATTER.format(LocalDateTime.now());
        String coloredLevel = colorize(level.name(), AnsiColor.getColor(level));
        String output = Formatted.format("[{}] [{}] {}", time, coloredLevel, message);
        Out.printLine(output);
    }

    public void log(LogLevel level, String message, Throwable t) {
        log(level, message);
        if (ENABLED_LEVELS.contains(level) && level != LogLevel.OFF && t != null)
            t.printStackTrace(System.out);
    }

    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    public void warn(String message, Throwable t)  { log(LogLevel.WARN, message, t); }

    public void error(String message, Throwable t) { log(LogLevel.ERROR, message, t); }

    public void fatal(String message, Throwable t) { log(LogLevel.FATAL, message, t); }

    public void enable(LogLevel level)   { ENABLED_LEVELS.add(level); }

    public void disable(LogLevel level)  { ENABLED_LEVELS.remove(level); }

    public void enableAll()              { ENABLED_LEVELS.clear(); ENABLED_LEVELS.addAll(EnumSet.complementOf(EnumSet.of(LogLevel.OFF))); }

    public void disableAll()             { ENABLED_LEVELS.clear(); }
}
