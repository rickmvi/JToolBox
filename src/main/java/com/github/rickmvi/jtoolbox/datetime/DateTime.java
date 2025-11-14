package com.github.rickmvi.jtoolbox.datetime;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

/**
 * A fluent and modern API for working with date and time in Java.
 * Supports chaining, timezone manipulation, and flexible formatting.
 *
 * <pre>{@code
 * String formatted = DateTime.now()
 *      .plusDays(3)
 *      .plusHours(5)
 *      .zone(ZoneId.of("America/Sao_Paulo"))
 *      .format(DatePattern.DD_MM_YYYY_HH_MM_SS);
 * }</pre>
 * @author Rick M. Viana
 * @version 1.1
 * @since 2025
 */
@SuppressWarnings("unused")
public final class DateTime implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter(value = AccessLevel.PUBLIC)
    private LocalDateTime dateTime;

    @Getter(value = AccessLevel.PUBLIC)
    private ZoneId zoneId;

    private DateTime(LocalDateTime dateTime, ZoneId zoneId) {
        this.dateTime = dateTime;
        this.zoneId = zoneId;
    }

    private DateTime() {
        this(LocalDateTime.now(), ZoneId.systemDefault());
    }

    @Contract(" -> new")
    public static @NotNull DateTime now() {
        return new DateTime();
    }

    public static @NotNull DateTime of(@NotNull LocalDateTime dateTime) {
        return new DateTime(dateTime, ZoneId.systemDefault());
    }

    public static @NotNull DateTime of(@NotNull ZonedDateTime zonedDateTime) {
        return new DateTime(zonedDateTime.toLocalDateTime(), zonedDateTime.getZone());
    }

    public static @NotNull DateTime of(@NotNull Instant instant) {
        return new DateTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()), ZoneId.systemDefault());
    }

    public static @NotNull DateTime of(LocalDate date, LocalTime time) {
        return new DateTime(LocalDateTime.of(date, time), ZoneId.systemDefault());
    }

    public static @NotNull DateTime of(LocalDate date, LocalTime time, ZoneId zoneId) {
        return new DateTime(LocalDateTime.of(date, time), zoneId);
    }

    @Contract("_, _ -> new")
    public static @NotNull DateTime of(String text, @NotNull DatePattern pattern) {
        return new DateTime(LocalDateTime.parse(Objects.requireNonNull(text), pattern.formatter()), ZoneId.systemDefault());
    }

    public DateTime plus(long amount, ChronoUnit unit) {
        dateTime = dateTime.plus(amount, unit);
        return this;
    }

    public DateTime minus(long amount, ChronoUnit unit) {
        dateTime = dateTime.minus(amount, unit);
        return this;
    }

    public DateTime plusYears(long years) { return plus(years, ChronoUnit.YEARS); }
    public DateTime plusMonths(long months) { return plus(months, ChronoUnit.MONTHS); }
    public DateTime plusWeeks(long weeks) { return plus(weeks * 7, ChronoUnit.DAYS); }
    public DateTime plusDays(long days) { return plus(days, ChronoUnit.DAYS); }
    public DateTime plusHours(long hours) { return plus(hours, ChronoUnit.HOURS); }
    public DateTime plusMinutes(long minutes) { return plus(minutes, ChronoUnit.MINUTES); }
    public DateTime plusSeconds(long seconds) { return plus(seconds, ChronoUnit.SECONDS); }

    public DateTime minusYears(long years) { return minus(years, ChronoUnit.YEARS); }
    public DateTime minusMonths(long months) { return minus(months, ChronoUnit.MONTHS); }
    public DateTime minusWeeks(long weeks) { return minus(weeks * 7, ChronoUnit.DAYS); }
    public DateTime minusDays(long days) { return minus(days, ChronoUnit.DAYS); }
    public DateTime minusHours(long hours) { return minus(hours, ChronoUnit.HOURS); }
    public DateTime minusMinutes(long minutes) { return minus(minutes, ChronoUnit.MINUTES); }
    public DateTime minusSeconds(long seconds) { return minus(seconds, ChronoUnit.SECONDS); }

    public DateTime zone(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    @Contract(" -> new")
    public @NotNull ZonedDateTime toZonedDateTime() {
        return dateTime.atZone(zoneId);
    }

    public Instant toInstant() {
        return toZonedDateTime().toInstant();
    }

    @Contract(" -> new")
    public @NotNull Date toDate() {
        return Date.from(toInstant());
    }

    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }

    public boolean isBefore(@NotNull DateTime other) {
        return this.toZonedDateTime().isBefore(other.toZonedDateTime());
    }

    public boolean isAfter(@NotNull DateTime other) {
        return this.toZonedDateTime().isAfter(other.toZonedDateTime());
    }

    public long between(@NotNull DateTime other, @NotNull ChronoUnit unit) {
        return unit.between(this.toZonedDateTime(), other.toZonedDateTime());
    }

    public @NotNull String format(@NotNull String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public @NotNull String format(@NotNull DatePattern pattern) {
        return pattern.formatter().format(toZonedDateTime());
    }

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

        public @NotNull DateTimeFormatter formatter() {
            return formatter;
        }
    }
}
