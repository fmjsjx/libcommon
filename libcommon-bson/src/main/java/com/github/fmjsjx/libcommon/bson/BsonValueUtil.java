package com.github.fmjsjx.libcommon.bson;

import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * A utility class for {@link BsonValue}s.
 *
 * @author MJ Fang
 * @since 3.1
 */
public class BsonValueUtil {

    private static final class NumberEncodersHolder {
        private static final Map<Class<?>, Function<Number, BsonValue>> encoders;

        static {
            Function<Number, BsonValue> toBsonInt32 = n -> new BsonInt32(n.intValue());
            Function<Number, BsonValue> toBsonInt64 = n -> new BsonInt64(n.longValue());
            Function<Number, BsonValue> toBsonDouble = n -> new BsonDouble(n.doubleValue());
            encoders = Map.ofEntries(
                    Map.entry(Byte.class, toBsonInt32),
                    Map.entry(Short.class, toBsonInt32),
                    Map.entry(Integer.class, toBsonInt32),
                    Map.entry(AtomicInteger.class, toBsonInt32),
                    Map.entry(Long.class, toBsonInt64),
                    Map.entry(AtomicLong.class, toBsonInt64),
                    Map.entry(Float.class, toBsonDouble),
                    Map.entry(Double.class, toBsonDouble),
                    Map.entry(Decimal128.class, n -> new BsonDecimal128((Decimal128) n)),
                    Map.entry(BigDecimal.class, n -> new BsonDecimal128(new Decimal128((BigDecimal) n))),
                    Map.entry(BigInteger.class, n -> new BsonDecimal128(new Decimal128(new BigDecimal((BigInteger) n))))
            );
        }

    }

    private static final class CommonEncodersHolder {
        private static final Map<Class<?>, Function<Object, BsonValue>> encoders;

        static {
            var startOfWeeks = Map.ofEntries(
                    Map.entry(DayOfWeek.SUNDAY, new BsonString("sun")),
                    Map.entry(DayOfWeek.MONDAY, new BsonString("mon")),
                    Map.entry(DayOfWeek.TUESDAY, new BsonString("tue")),
                    Map.entry(DayOfWeek.WEDNESDAY, new BsonString("wed")),
                    Map.entry(DayOfWeek.THURSDAY, new BsonString("thu")),
                    Map.entry(DayOfWeek.FRIDAY, new BsonString("fri")),
                    Map.entry(DayOfWeek.SATURDAY, new BsonString("sat"))
            );
            encoders = Map.ofEntries(
                    Map.entry(String.class, v -> new BsonString(v.toString())),
                    Map.entry(char[].class, v -> new BsonString(new String((char[]) v))),
                    Map.entry(Boolean.class, v -> BsonBoolean.valueOf((Boolean) v)),
                    Map.entry(Date.class, v -> new BsonDateTime(((Date) v).getTime())),
                    Map.entry(LocalDateTime.class, v -> new BsonDateTime(DateTimeUtil.toEpochMilli((LocalDateTime) v))),
                    Map.entry(ZonedDateTime.class, v -> new BsonDateTime(((ZonedDateTime) v).toInstant().toEpochMilli())),
                    Map.entry(OffsetDateTime.class, v -> new BsonDateTime(((OffsetDateTime) v).toInstant().toEpochMilli())),
                    Map.entry(DateUnit.class, v -> ((DateUnit) v).toBsonString()),
                    Map.entry(MetaDataKeyword.class, v -> ((MetaDataKeyword) v).toBsonString()),
                    Map.entry(BsonType.class, v -> new BsonInt32(((BsonType) v).getValue())),
                    Map.entry(DayOfWeek.class, startOfWeeks::get),
                    Map.entry(byte[].class, v -> new BsonBinary((byte[]) v)),
                    Map.entry(UUID.class, v -> new BsonBinary((UUID) v)),
                    Map.entry(short[].class, v -> {
                        var array = (short[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(new BsonInt32(a));
                        }
                        return value;
                    }),
                    Map.entry(int[].class, v -> {
                        var array = (int[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(new BsonInt32(a));
                        }
                        return value;
                    }),
                    Map.entry(long[].class, v -> {
                        var array = (long[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(new BsonInt64(a));
                        }
                        return value;
                    }),
                    Map.entry(double[].class, v -> {
                        var array = (double[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(new BsonDouble(a));
                        }
                        return value;
                    }),
                    Map.entry(float[].class, v -> {
                        var array = (float[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(new BsonDouble(a));
                        }
                        return value;
                    }),
                    Map.entry(boolean[].class, v -> {
                        var array = (boolean[]) v;
                        var value = new BsonArray(array.length);
                        for (var a : array) {
                            value.add(BsonBoolean.valueOf(a));
                        }
                        return value;
                    })
            );
        }
    }

    /**
     * Encodes number to {@link BsonValue}.
     *
     * @param number the number
     * @return the encoded {@code BsonValue}
     */
    public static final BsonValue encode(Number number) {
        if (number == null) {
            return BsonNull.VALUE;
        }
        var encoder = NumberEncodersHolder.encoders.get(number.getClass());
        if (encoder == null) {
            return new BsonDecimal128(new Decimal128(new BigDecimal(number.toString())));
        }
        return encoder.apply(number);
    }

    /**
     * Encodes any object to {@link BsonValue}.
     *
     * @param value the value
     * @return the encoded {@code BsonValue}
     */
    public static final BsonValue encode(Object value) {
        if (value == null) {
            return BsonNull.VALUE;
        }
        if (value instanceof BsonValue bson) {
            return bson;
        }
        if (value instanceof Number number) {
            return encode(number);
        }
        var valueType = value.getClass();
        var encoder = CommonEncodersHolder.encoders.get(valueType);
        if (encoder != null) {
            return encoder.apply(value);
        }
        if (valueType.isArray()) {
            var array = (Object[]) value;
            var bsonArray = new BsonArray(array.length);
            for (var v : array) {
                bsonArray.add(encode(v));
            }
            return bsonArray;
        }
        if (value instanceof Iterable<?> iterable) {
            var bsonArray = new BsonArray(iterable instanceof Collection<?> collection ? collection.size() : 10);
            for (var v : iterable) {
                bsonArray.add(encode(v));
            }
            return bsonArray;
        }
        if (value instanceof Map<?, ?> map) {
            BsonDocument doc = new BsonDocument(map.size() << 1);
            for (var entry : map.entrySet()) {
                doc.append(entry.getKey().toString(), encode(entry.getValue()));
            }
            return doc;
        }
        if (value instanceof ZoneId zoneId) {
            return new BsonString(zoneId.getId());
        }
        return new BsonString(value.toString());
    }

    /**
     * Encodes value using the specified writer given.
     *
     * @param writer        the writer
     * @param value         the value to be written
     * @param codecRegistry the {@link CodecRegistry}
     * @param <TItem>       the type of the value
     * @author MJ Fang
     * @since 3.2
     */
    @SuppressWarnings("unchecked")
    public static <TItem> void encodeValue(final BsonDocumentWriter writer, final TItem value, final CodecRegistry codecRegistry) {
        if (value == null) {
            writer.writeNull();
        } else if (value instanceof Bson bson) {
            codecRegistry.get(BsonDocument.class)
                    .encode(writer, bson.toBsonDocument(BsonDocument.class, codecRegistry), EncoderContext.builder().build());
        } else {
            ((Codec<TItem>) codecRegistry.get(value.getClass())).encode(writer, value, EncoderContext.builder().build());
        }
    }

    /**
     * Encodes any objects to {@link BsonValue}.
     *
     * @param values the values
     * @return the encoded {@code BsonValue}
     */
    public static final BsonValue encodeList(Object... values) {
        var array = new BsonArray(values.length);
        for (var value : values) {
            array.add(encode(value));
        }
        return array;
    }

    static final Bson operator(String name, BsonValue value) {
        return new BsonDocument(1).append(name, value);
    }

    static final Bson operator(String name, Object value) {
        return operator(name, encode(value));
    }

    private BsonValueUtil() {
    }

}
