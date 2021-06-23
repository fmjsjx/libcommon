package com.github.fmjsjx.libcommon.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * Utility class for JDK8 date time API.
 */
public class DateTimeUtil {

    /**
     * Returns the UNIX time (seconds since {@code 1970-01-01T00:00:00.000Z}).
     * 
     * @return the UNIX time
     */
    public static final long unixTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Returns the UNIX time with millisecond part.
     * 
     * @return the UNIX time with millisecond part
     */
    public static final double unixTimeWithMs() {
        return System.currentTimeMillis() / 1000.0;
    }

    /**
     * Returns the {@link LocalDate} at tomorrow.
     * 
     * @return the {@code LocalDate} at tomorrow
     */
    public static final LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }

    /**
     * Returns the {@link LocalDate} at yesterday.
     * 
     * @return the {@code LocalDate} at yesterday
     */
    public static final LocalDate yesterday() {
        return LocalDate.now().minusDays(1);
    }

    /**
     * Obtains an instance of {@link LocalDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z and the specified zone ID.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @param zone        the time-zone, which may be an offset, not null
     * @return the local date-time
     */
    public static final LocalDateTime local(long epochSecond, ZoneId zone) {
        if (zone instanceof ZoneOffset) {
            return LocalDateTime.ofEpochSecond(epochSecond, 0, (ZoneOffset) zone);
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zone);
    }

    /**
     * Obtains an instance of {@link LocalDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @return the local date-time
     */
    public static final LocalDateTime local(long epochSecond) {
        return local(epochSecond, zone());
    }

    /**
     * Obtains an instance of {@link ZonedDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z and the specified zone ID.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @param zone        the time-zone, which may be an offset, not null
     * @return the zoned date-time
     */
    public static final ZonedDateTime zoned(long epochSecond, ZoneId zone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zone);
    }

    /**
     * Obtains an instance of {@link ZonedDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @return the zoned date-time
     */
    public static final ZonedDateTime zoned(long epochSecond) {
        return zoned(epochSecond, zone());
    }

    /**
     * Obtains an instance of {@link OffsetDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z and the specified zone ID.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @param zone        the time-zone, which may be an offset, not null
     * @return the offset date-time
     */
    public static final OffsetDateTime offset(long epochSecond, ZoneId zone) {
        if (zone instanceof ZoneOffset) {
            var offset = (ZoneOffset) zone;
            return OffsetDateTime.of(local(epochSecond, offset), offset);
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), zone);
    }

    /**
     * Obtains an instance of {@link OffsetDateTime} using seconds from the epoch of
     * 1970-01-01T00:00:00Z.
     * 
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @return the offset date-time
     */
    public static final OffsetDateTime offset(long epochSecond) {
        return offset(epochSecond, zone());
    }

    /**
     * Returns the system default time-zone.
     * 
     * @return the system default time-zone
     */
    public static final ZoneId zone() {
        return ZoneId.systemDefault();
    }

    /**
     * Converts and returns the specified date-time to the number of seconds from
     * the epoch of 1970-01-01T00:00:00Z in the default time-zone.
     * 
     * @param dateTime the date-time to be converted
     * @return the specified date-time to the number of seconds from the epoch of
     *         1970-01-01T00:00:00Z in the default time-zone
     */
    public static final long toEpochSecond(LocalDateTime dateTime) {
        return toEpochSecond(dateTime, zone());
    }

    /**
     * Converts and returns the specified date-time to the number of seconds from
     * the epoch of 1970-01-01T00:00:00Z in the specified time-zone.
     * 
     * @param dateTime the date-time to be converted
     * @param zone     the time-zone
     * @return the specified date-time to the number of seconds from the epoch of
     *         1970-01-01T00:00:00Z in the specified time-zone
     */
    public static final long toEpochSecond(LocalDateTime dateTime, ZoneId zone) {
        return dateTime.atZone(zone).toEpochSecond();
    }

    /**
     * Converts and returns the specified date-time to the number of seconds from
     * the epoch of 1970-01-01T00:00:00Z in the specified time-zone offset.
     * 
     * @param dateTime the date-time to be converted
     * @param offset   the time-zone offset
     * @return the specified date-time to the number of seconds from the epoch of
     *         1970-01-01T00:00:00Z in the specified time-zone offset
     */
    public static final long toEpochSecond(LocalDateTime dateTime, ZoneOffset offset) {
        return dateTime.atOffset(offset).toEpochSecond();
    }

    /**
     * Returns {@code true} if the specified to dates is in same week(Monday start),
     * {@code false} otherwise.
     * 
     * @param date1 the first date
     * @param date2 the second date
     * @return {@code true} if the specified to dates is in same week(Monday start),
     *         {@code false} otherwise
     */
    public static final boolean isSameWeek(LocalDate date1, LocalDate date2) {
        var d1 = date1.with(ChronoField.DAY_OF_WEEK, 1);
        var d2 = date2.with(ChronoField.DAY_OF_WEEK, 1);
        return d1.isEqual(d2);
    }

    /**
     * Returns the number of the specified date with format {@code yyyyMMdd} as
     * {@code int} type.
     * 
     * @param date the date
     * @return the number of the specified date with format {@code yyyyMMdd} as
     *         {@code int} type
     */
    public static final int toNumber(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    /**
     * Returns the number of the specified time with format {@code HHmmss} as
     * {@code int} type.
     * 
     * @param time the time
     * @return the number of the specified time with format {@code HHmmss} as
     *         {@code int} type
     */
    public static final int toNumber(LocalTime time) {
        return time.getHour() * 10000 + time.getMinute() * 100 + time.getSecond();
    }

    /**
     * Parses and returns the date from an {@code integer} number with format
     * {@code yyyyMMdd}.
     * 
     * @param n the number
     * @return the local date
     */
    public static final LocalDate toDate(int n) {
        var year = n / 10000;
        var month = n / 100 % 100;
        var dayOfMonth = n % 100;
        return LocalDate.of(year, month, dayOfMonth);
    }

    /**
     * Parse and returns the time from an {@code integer} number with format
     * {@code HHmmss}.
     * 
     * @param n the number
     * @return the local time
     */
    public static final LocalTime toTime(int n) {
        var hour = n / 10000;
        var minute = n / 100 % 100;
        var second = n % 100;
        return LocalTime.of(hour, minute, second);
    }

    /**
     * Converts the time from {@link LocalDateTime} to legacy {@link Date}.
     * 
     * @param time the time
     * @return a {@code java.util.Date}
     * @since 2.0
     */
    public static final Date toLegacyDate(LocalDateTime time) {
        return toLegacyDate(time, zone());
    }

    /**
     * Converts the time from {@link LocalDateTime} to legacy {@link Date} at
     * specified time-zone given.
     * 
     * @param time the time
     * @param zone the time-zone
     * @return a {@code java.util.Date}
     * @since 2.0
     */
    public static final Date toLegacyDate(LocalDateTime time, ZoneId zone) {
        return toLegacyDate(time.atZone(zone));
    }

    /**
     * Converts the time from {@link LocalDateTime} to legacy {@link Date}.
     * 
     * @param time the time
     * @return a {@code java.util.Date}
     * @since 2.0
     */
    public static final Date toLegacyDate(ZonedDateTime time) {
        return Date.from(time.toInstant());
    }

    private DateTimeUtil() {
    }

}
