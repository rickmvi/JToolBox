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
package com.github.rickmvi.jtoolbox.debug;

import com.github.rickmvi.jtoolbox.console.Out;
import com.github.rickmvi.jtoolbox.debug.log.LogLevel;
import com.github.rickmvi.jtoolbox.text.StringFormatter;
import com.github.rickmvi.jtoolbox.utils.constants.Constants;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

/**
 * A simple logging utility class that provides colored console output with timestamp and log level information.
 * <p>
 * This logger supports different log levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL) and allows enabling/disabling
 * specific levels. It can format messages with ANSI colors (when enabled) and supports parameterized messages
 * and exception logging.
 * </p>
 *
 * @author Rick M. Viana
 * @since 1.0
 */
@lombok.experimental.UtilityClass
public class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATA_TIME_FORMAT);
    private static final Set<LogLevel> ENABLED_LEVELS = EnumSet.complementOf(EnumSet.of(LogLevel.OFF));

    /**
     * Gets whether ANSI color output is enabled for log messages.
     */
    @lombok.Getter(value = lombok.AccessLevel.PUBLIC)
    @lombok.Setter(value = lombok.AccessLevel.PUBLIC)
    private static boolean useAnsiColor = false;

    /**
     * Applies ANSI color to the given message if color output is enabled.
     *
     * @param message the message to colorize
     * @param color the ANSI color code to apply
     * @return the colorized message if enabled, or the original message otherwise
     */
    @ApiStatus.Internal
    @Contract(pure = true)
    private static String colorize(String message, String color) {
        return useAnsiColor ? color + message + AnsiColor.RESET.getAnsiCode() : message;
    }

    /**
     * Logs a message at the specified log level.
     *
     * @param level the log level for this message
     * @param message the message to log
     */
    public static void log(LogLevel level, String message) {
        if (!ENABLED_LEVELS.contains(level) || level == LogLevel.OFF) return;

        String time = FORMATTER.format(LocalDateTime.now());
        String coloredLevel = colorize(level.name(), AnsiColor.getColor(level));

        Out.formatted("[{}] [{}] {}%n", time, coloredLevel, message);
    }

    /**
     * Logs a formatted message at the specified log level.
     *
     * @param level the log level for this message
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void log(LogLevel level, String template, Object... args) {
        if (!ENABLED_LEVELS.contains(level) || level == LogLevel.OFF) return;

        String time = FORMATTER.format(LocalDateTime.now());
        String coloredLevel = colorize(level.name(), AnsiColor.getColor(level));
        String message = StringFormatter.format(template, args);

        Out.formatted("[{}] [{}] {}%n", time, coloredLevel, message);
    }

    /**
     * Logs a message and throwable at the specified log level.
     *
     * @param level the log level for this message
     * @param message the message to log
     * @param t the throwable to log (stack trace will be printed)
     */
    public static void log(LogLevel level, String message, Throwable t) {
        log(level, message);
        if (ENABLED_LEVELS.contains(level) && level != LogLevel.OFF && t != null)
            t.printStackTrace(System.out);
    }

    /**
     * Logs a formatted message and throwable at the specified log level.
     *
     * @param level the log level for this message
     * @param template the message template with placeholders
     * @param t the throwable to log (stack trace will be printed)
     * @param args the arguments to substitute into the template
     */
    public static void log(LogLevel level, String template, Throwable t, Object... args) {
        log(level, StringFormatter.format(template, args));
        if (ENABLED_LEVELS.contains(level) && level != LogLevel.OFF && t != null)
            t.printStackTrace(System.out);
    }

    /**
     * Logs a message at TRACE level.
     *
     * @param message the message to log
     */
    public static void trace(String message)   { log(LogLevel.TRACE, message); }

    /**
     * Logs a message at DEBUG level.
     *
     * @param message the message to log
     */
    public static void debug(String message)   { log(LogLevel.DEBUG, message); }

    /**
     * Logs a message at INFO level.
     *
     * @param message the message to log
     */
    public static void info(String message)    { log(LogLevel.INFO, message); }

    /**
     * Logs a message at WARN level.
     *
     * @param message the message to log
     */
    public static void warn(String message)    { log(LogLevel.WARN, message); }

    /**
     * Logs a message at ERROR level.
     *
     * @param message the message to log
     */
    public static void error(String message)   { log(LogLevel.ERROR, message); }

    /**
     * Logs a message at FATAL level.
     *
     * @param message the message to log
     */
    public static void fatal(String message)   { log(LogLevel.FATAL, message); }

    /**
     * Logs a formatted message at TRACE level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void trace(String template, Object... args)  { log(LogLevel.TRACE, template, args); }

    /**
     * Logs a formatted message at DEBUG level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void debug(String template, Object... args)  { log(LogLevel.DEBUG, template, args); }

    /**
     * Logs a formatted message at INFO level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void info(String template,  Object... args)  { log(LogLevel.INFO, template, args); }

    /**
     * Logs a formatted message at WARN level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void warn(String template,  Object... args)  { log(LogLevel.WARN, template, args); }

    /**
     * Logs a formatted message at ERROR level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void error(String template, Object... args)  { log(LogLevel.ERROR, template, args); }

    /**
     * Logs a formatted message at FATAL level.
     *
     * @param template the message template with placeholders
     * @param args the arguments to substitute into the template
     */
    public static void fatal(String template, Object... args)  { log(LogLevel.FATAL, template, args); }

    /**
     * Logs a message and throwable at WARN level.
     *
     * @param message the message to log
     * @param t the throwable to log (stack trace will be printed)
     */
    public static void warn(String message,  Throwable t)  { log(LogLevel.WARN, message, t); }

    /**
     * Logs a message and throwable at ERROR level.
     *
     * @param message the message to log
     * @param t the throwable to log (stack trace will be printed)
     */
    public static void error(String message, Throwable t)  { log(LogLevel.ERROR, message, t); }

    /**
     * Logs a message and throwable at FATAL level.
     *
     * @param message the message to log
     * @param t the throwable to log (stack trace will be printed)
     */
    public static void fatal(String message, Throwable t)  { log(LogLevel.FATAL, message, t); }

    /**
     * Logs a formatted message and throwable at WARN level.
     *
     * @param template the message template with placeholders
     * @param t the throwable to log (stack trace will be printed)
     * @param args the arguments to substitute into the template
     */
    public static void warn(String template,  Throwable t, Object... args) { log(LogLevel.WARN, template, t, args); }

    /**
     * Logs a formatted message and throwable at ERROR level.
     *
     * @param template the message template with placeholders
     * @param t the throwable to log (stack trace will be printed)
     * @param args the arguments to substitute into the template
     */
    public static void error(String template, Throwable t, Object... args) { log(LogLevel.ERROR, template, t, args); }

    /**
     * Logs a formatted message and throwable at FATAL level.
     *
     * @param template the message template with placeholders
     * @param t the throwable to log (stack trace will be printed)
     * @param args the arguments to substitute into the template
     */
    public static void fatal(String template, Throwable t, Object... args) { log(LogLevel.FATAL, template, t, args); }

    /**
     * Enables logging for the specified log level.
     *
     * @param level the log level to enable
     */
    public static void enable(LogLevel level)  { ENABLED_LEVELS.add(level); }

    /**
     * Disables logging for the specified log level.
     *
     * @param level the log level to disable
     */
    public static void disable(LogLevel level) { ENABLED_LEVELS.remove(level); }

    /**
     * Enables logging for all levels (except OFF).
     */
    public static void enableAll()  { ENABLED_LEVELS.clear(); ENABLED_LEVELS.addAll(EnumSet.complementOf(EnumSet.of(LogLevel.OFF))); }

    /**
     * Disables logging for all levels.
     */
    public static void disableAll() { ENABLED_LEVELS.clear(); }
}