package com.github.rickmvi.jtoolbox.util;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Fluent and flexible API for working with LocalDateTime and ZonedDateTime.
 * <p>
 * Allows fluent chaining of date/time operations, formatting, and timezone handling.
 *
 * <pre>{@code
 * String formatted = FluentDate.now()
 *      .plusDays(3)
 *      .plusHours(5)
 *      .zone(ZoneId.of("America/Sao_Paulo"))
 *      .format(DatePattern.DD_MM_YYYY_HH_MM_SS);
 * }</pre>
 */
public class DateBuilder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter(value = AccessLevel.PUBLIC)
    private LocalDateTime dateTime;

    @Getter(value = AccessLevel.PUBLIC)
    private ZoneId zoneId;

    private DateBuilder() {
        this.dateTime = LocalDateTime.now();
        this.zoneId = ZoneId.systemDefault();
    }

    private DateBuilder(String pattern) {
        this();
    }

    @Contract(" -> new")
    public static @NotNull DateBuilder now() {
        return getBuilder();
    }

    @Contract(" -> new")
    private static @NotNull DateBuilder getBuilder() {
        return new DateBuilder();
    }

    @Contract("_ -> new")
    public static @NotNull DateBuilder of(@NotNull LocalDateTime dateTime) {
        DateBuilder fd = getBuilder();
        fd.dateTime = dateTime;
        return fd;
    }

    @Contract("_ -> new")
    public static @NotNull DateBuilder of(@NotNull ZonedDateTime zonedDateTime) {
        DateBuilder fd = getBuilder();
        fd.dateTime = zonedDateTime.toLocalDateTime();
        fd.zoneId = zonedDateTime.getZone();
        return fd;
    }

    public DateBuilder plusYears(long years) {
        dateTime = dateTime.plusYears(years);
        return this;
    }

    public DateBuilder plusMonths(long months) {
        dateTime = dateTime.plusMonths(months);
        return this;
    }

    public DateBuilder plusWeeks(long weeks) {
        dateTime = dateTime.plusWeeks(weeks);
        return this;
    }

    public DateBuilder plusDays(long days) {
        dateTime = dateTime.plusDays(days);
        return this;
    }

    public DateBuilder plusHours(long hours) {
        dateTime = dateTime.plusHours(hours);
        return this;
    }

    public DateBuilder plusMinutes(long minutes) {
        dateTime = dateTime.plusMinutes(minutes);
        return this;
    }

    public DateBuilder plusSeconds(long seconds) {
        dateTime = dateTime.plusSeconds(seconds);
        return this;
    }

    public DateBuilder minusYears(long years) {
        dateTime = dateTime.minusYears(years);
        return this;
    }

    public DateBuilder minusMonths(long months) {
        dateTime = dateTime.minusMonths(months);
        return this;
    }

    public DateBuilder minusWeeks(long weeks) {
        dateTime = dateTime.minusWeeks(weeks);
        return this;
    }

    public DateBuilder minusDays(long days) {
        dateTime = dateTime.minusDays(days);
        return this;
    }

    public DateBuilder minusHours(long hours) {
        dateTime = dateTime.minusHours(hours);
        return this;
    }

    public DateBuilder minusMinutes(long minutes) {
        dateTime = dateTime.minusMinutes(minutes);
        return this;
    }

    public DateBuilder minusSeconds(long seconds) {
        dateTime = dateTime.minusSeconds(seconds);
        return this;
    }

    public DateBuilder zone(@NotNull ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public ZonedDateTime toZonedDateTime() {
        return dateTime.atZone(zoneId);
    }

    public String format(@NotNull String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return toZonedDateTime().format(formatter);
    }

    public String format(@NotNull DatePattern pattern) {
        return toZonedDateTime().format(pattern.getFormatter());
    }

    @Getter
    public enum DatePattern {
        DD_MM_YYYY("dd/MM/yyyy"),
        DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"),
        YYYY_MM_DD("yyyy-MM-dd"),
        ISO_LOCAL("yyyy-MM-dd'T'HH:mm:ss"),
        HH_MM_SS("HH:mm:ss");

        private final DateTimeFormatter formatter;

        DatePattern(String pattern) {
            this.formatter = DateTimeFormatter.ofPattern(pattern);
        }
    }

}
