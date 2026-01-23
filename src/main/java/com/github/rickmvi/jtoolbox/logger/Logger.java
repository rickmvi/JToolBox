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
package com.github.rickmvi.jtoolbox.logger;

import com.github.rickmvi.jtoolbox.control.If;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.logger.log.LogLevel;
import com.github.rickmvi.jtoolbox.datetime.DateTime;
import com.github.rickmvi.jtoolbox.console.IO;
import com.github.rickmvi.jtoolbox.control.Condition;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.EnumSet;

/**
 * A simple logging utility class that provides colored console output with timestamp and log level information.
 * <p>
 * This logger supports different log levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL) and allows enabling/disabling
 * specific levels. It can format messages with ANSI colors (when enabled) and supports parameterized messages
 * and exception logging.
 * </p>
 *
 * @author Rick M. Viana
 * @version 1.2
 * @since 2025
 */
@UtilityClass
@SuppressWarnings("unused")
public class Logger {

    private static final String TIME;
    private static final Set<LogLevel> ENABLED_LEVELS;

    static {
        TIME = DateTime.now().format(DateTime.Format.DASH_DATETIME);
        ENABLED_LEVELS = EnumSet.complementOf(EnumSet.of(LogLevel.OFF));
    }

    @Getter(value = AccessLevel.PUBLIC)
    @Setter(value = AccessLevel.PUBLIC)
    private static boolean useAnsiColor = true;

    @ApiStatus.Internal
    @Contract(pure = true)
    private static String colorize(String message, String color) {
        return useAnsiColor ? color + message + AnsiColor.RESET.getCode() : message;
    }

    public static void log(LogLevel level, String message) {
        if (!ENABLED_LEVELS.contains(level) || level == LogLevel.OFF) return;

        String time        = DateTime.now().format(DateTime.Format.DASH_DATETIME);

        String coloredLevel = colorize(level.name(), AnsiColor.getColor(level));

        IO.format("[{}] {} [{}] {}$n", time, coloredLevel, Thread.currentThread().getName(), message);
    }

    public static void log(LogLevel level, String template, Object... args) {
        if (!ENABLED_LEVELS.contains(level) || level == LogLevel.OFF) return;

        String time        = DateTime.now().format(DateTime.Format.DASH_DATETIME);

        String coloredLevel = colorize(level.name(), AnsiColor.getColor(level));
        String message      = StringFormatter.format(template, args);

        IO.format("[{}] {} [{}] {}$n", time, coloredLevel, Thread.currentThread().getName(), message);
    }

    public static void log(LogLevel level, String message, @NotNull Throwable t) {
        log(level, message);
        If.when(ENABLED_LEVELS.contains(level))
                .and(level != LogLevel.OFF)
                .then(t::printStackTrace)
                .execute();
    }

    public static void log(LogLevel level, String template, @NotNull Throwable t, Object... args) {
        log(level, StringFormatter.format(template, args));
        If.when(ENABLED_LEVELS.contains(level))
                .and(level != LogLevel.OFF)
                .then(t::printStackTrace)
                .execute();
    }

    public static void trace(String message)   { log(LogLevel.TRACE, message); }

    public static void debug(String message)   { log(LogLevel.DEBUG, message); }

    public static void info(String message)    { log(LogLevel.INFO, message); }

    public static void warn(String message)    { log(LogLevel.WARN, message); }

    public static void error(String message)   { log(LogLevel.ERROR, message); }

    public static void fatal(String message)   { log(LogLevel.FATAL, message); }

    public static void trace(String template, Object... args)  { log(LogLevel.TRACE, template, args); }

    public static void debug(String template, Object... args)  { log(LogLevel.DEBUG, template, args); }

    public static void info(String template,  Object... args)  { log(LogLevel.INFO, template, args); }

    public static void warn(String template,  Object... args)  { log(LogLevel.WARN, template, args); }

    public static void error(String template, Object... args)  { log(LogLevel.ERROR, template, args); }

    public static void fatal(String template, Object... args)  { log(LogLevel.FATAL, template, args); }

    public static void warn(String message,  Throwable t)  { log(LogLevel.WARN, message, t); }

    public static void error(String message, Throwable t)  { log(LogLevel.ERROR, message, t); }

    public static void fatal(String message, Throwable t)  { log(LogLevel.FATAL, message, t); }

    public static void warn(String template,  Throwable t, Object... args) { log(LogLevel.WARN, template, t, args); }

    public static void error(String template, Throwable t, Object... args) { log(LogLevel.ERROR, template, t, args); }

    public static void fatal(String template, Throwable t, Object... args) { log(LogLevel.FATAL, template, t, args); }

    public static void enable(LogLevel level)  { ENABLED_LEVELS.add(level); }

    public static void disable(LogLevel level) { ENABLED_LEVELS.remove(level); }

    public static void enableAll()  { ENABLED_LEVELS.clear(); ENABLED_LEVELS.addAll(EnumSet.complementOf(EnumSet.of(LogLevel.OFF))); }

    public static void disableAll() { ENABLED_LEVELS.clear(); }
}