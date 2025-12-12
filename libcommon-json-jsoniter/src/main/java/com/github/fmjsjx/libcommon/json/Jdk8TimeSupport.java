package com.github.fmjsjx.libcommon.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

/**
 * JDK8 time APIs support.
 */
public class Jdk8TimeSupport {

    /**
     * Enables all JDK8 time supports.
     */
    public static final void enableAll() {
        try {
            LocalDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            LocalDateSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            LocalTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            OffsetDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            ZonedDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
    }

    private Jdk8TimeSupport() {
    }

    /**
     * {@code LocalDateTime} support.
     */
    public static final class LocalDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code LocalDateTimeSupport} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code LocalDateTimeSupport} is enabled,
         * {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code LocalDateTimeSupport}.
         */
        public static final void enable() {
            var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj != null) {
                            stream.writeVal(formatter.format((LocalDateTime) obj));
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        if (obj == null) {
                            return Any.wrapNull();
                        }
                        return Any.wrap(formatter.format((LocalDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalDateTime.class, iter -> {
                    String value = iter.readString();
                    return value == null ? null : LocalDateTime.parse(value, formatter);
                });
            } else {
                throw new IllegalStateException("LocalDateTimeSupport.enable can only be called once");
            }
        }

        private LocalDateTimeSupport() {
        }

    }

    /**
     * {@code LocalDate} support.
     */
    public static final class LocalDateSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code LocalDateSupport} is enabled, {@code false}
         * otherwise.
         *
         * @return {@code true} if {@code LocalDateSupport} is enabled, {@code false}
         * otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code LocalDateSupport}.
         */
        public static final void enable() {
            var formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalDate.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj != null) {
                            stream.writeVal(formatter.format((LocalDate) obj));
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        if (obj == null) {
                            return Any.wrapNull();
                        }
                        return Any.wrap(formatter.format((LocalDate) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalDate.class, iter -> {
                    String value = iter.readString();
                    return value == null ? null : LocalDate.parse(value, formatter);
                });
            } else {
                throw new IllegalStateException("LocalDateSupport.enable can only be called once");
            }
        }

        private LocalDateSupport() {
        }

    }

    /**
     * {@code LocalTime} support.
     */
    public static final class LocalTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code LocalTimeSupport} is enabled, {@code false}
         * otherwise.
         *
         * @return {@code true} if {@code LocalTimeSupport} is enabled, {@code false}
         * otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code LocalTimeSupport}.
         */
        public static final void enable() {
            var formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj != null) {
                            stream.writeVal(formatter.format((LocalTime) obj));
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        if (obj == null) {
                            return Any.wrapNull();
                        }
                        return Any.wrap(formatter.format((LocalTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalTime.class, iter -> {
                    String value = iter.readString();
                    return value == null ? null : LocalTime.parse(value, formatter);
                });
            } else {
                throw new IllegalStateException("LocalTimeSupport.enable can only be called once");
            }
        }

        private LocalTimeSupport() {
        }

    }

    /**
     * {@code OffsetDateTime} support.
     */
    public static final class OffsetDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code OffsetDateTimeSupport} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code OffsetDateTimeSupport} is enabled,
         * {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code OffsetDateTimeSupport}.
         */
        public static final void enable() {
            var formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(OffsetDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj != null) {
                            stream.writeVal(formatter.format((OffsetDateTime) obj));
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        if (obj == null) {
                            return Any.wrapNull();
                        }
                        return Any.wrap(formatter.format((OffsetDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(OffsetDateTime.class, iter -> {
                    String value = iter.readString();
                    return value == null ? null : OffsetDateTime.parse(value, formatter);
                });
            } else {
                throw new IllegalStateException("OffsetDateTimeSupport.enable can only be called once");
            }
        }

        private OffsetDateTimeSupport() {
        }

    }

    /**
     * {@code ZonedDateTime} support.
     */
    public static final class ZonedDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code ZonedDateTimeSupport} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code ZonedDateTimeSupport} is enabled,
         * {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code ZonedDateTimeSupport}.
         */
        public static final void enable() {
            var formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(ZonedDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj != null) {
                            stream.writeVal(formatter.format((ZonedDateTime) obj));
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        if (obj == null) {
                            return Any.wrapNull();
                        }
                        return Any.wrap(formatter.format((ZonedDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(ZonedDateTime.class, iter -> {
                    String value = iter.readString();
                    return value == null ? null : ZonedDateTime.parse(value, formatter);
                });
            } else {
                throw new IllegalStateException("ZonedDateTimeSupport.enable can only be called once");
            }
        }

        private ZonedDateTimeSupport() {
        }

    }

}
