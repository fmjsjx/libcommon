package com.github.fmjsjx.libcommon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

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
     * Returns the {@link LocalDate} at today.
     * 
     * @return the {@code LocalDate} at today
     */
    public static final LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns the {@link LocalDate} at tomorrow.
     * 
     * @return the {@code LocalDate} at tomorrow
     */
    public static final LocalDate tomorrow() {
        return today().plusDays(1);
    }

    /**
     * Returns the {@link LocalDate} at yesterday.
     * 
     * @return the {@code LocalDate} at yesterday
     */
    public static final LocalDate yesterday() {
        return today().minusDays(1);
    }

    /**
     * Returns the {@link LocalDateTime} at now.
     * 
     * @return the {@code LocalDateTime} at now
     */
    public static final LocalDateTime localNow() {
        return LocalDateTime.now();
    }

    /**
     * Returns the {@link ZonedDateTime} with the default time-zone at now.
     * 
     * @return the {@code ZonedDateTime} with the default time-zone at now
     */
    public static final ZonedDateTime zonedNow() {
        return ZonedDateTime.now();
    }

    /**
     * Returns the {@link OffsetDateTime} with the default time-zone at now.
     * 
     * @return the {@code OffsetDateTime} with the default time-zone at now
     */
    public static final OffsetDateTime offsetNow() {
        return OffsetDateTime.now();
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
     * Parses and returns the date from {@code integer} number with format
     * {@code yyyyMMdd}.
     * 
     * @param n the number
     * @return the date from {@code integer} number with format {@code yyyyMMdd}
     */
    public static final LocalDate fromNumber(int n) {
        var year = n / 10000;
        var month = n / 100 % 100;
        var dayOfMonth = n % 100;
        return LocalDate.of(year, month, dayOfMonth);
    }

    private DateTimeUtil() {
    }

}
