package com.github.fmjsjx.libcommon.bson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonNumber;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import com.github.fmjsjx.libcommon.json.JsonException;
import com.github.fmjsjx.libcommon.util.DateTimeUtil;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;

/**
 * Utility class for BSON.
 * 
 * @since 2.0
 */
public class BsonUtil {

    /**
     * Gets the value in an embedded document.
     * 
     * @param <V>      the type of the return value
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<V>}
     */
    public static final <V> Optional<V> embedded(Document document, Object... keys) {
        return embedded(document, Arrays.asList(keys));
    }

    /**
     * Gets the value in an embedded document.
     * 
     * @param <V>      the type of the return value
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<V>}
     */
    @SuppressWarnings("unchecked")
    public static final <V> Optional<V> embedded(Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return Optional.empty();
            }
            i++;
        }
        return value == null ? Optional.empty() : Optional.of((V) value);
    }

    /**
     * Gets the {@code Document} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<Document>}
     */
    public static final Optional<Document> embeddedDocument(Document document, Object... keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code Document} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<Document>}
     */
    public static final Optional<Document> embeddedDocument(Document document, List<Object> keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code int} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalInt}
     */
    public static final OptionalInt embeddedInt(Document document, Object... keys) {
        return embeddedInt(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code int} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalInt}
     */
    public static final OptionalInt embeddedInt(Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return OptionalInt.empty();
            }
            i++;
        }
        if (value == null) {
            return OptionalInt.empty();
        }
        if (value instanceof Number) {
            return OptionalInt.of(((Number) value).intValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code long} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalLong}
     */
    public static final OptionalLong embeddedLong(Document document, Object... keys) {
        return embeddedLong(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code long} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalInt}
     */
    public static final OptionalLong embeddedLong(Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return OptionalLong.empty();
            }
            i++;
        }
        if (value == null) {
            return OptionalLong.empty();
        }
        if (value instanceof Number) {
            return OptionalLong.of(((Number) value).longValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code double} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalDouble}
     */
    public static final OptionalDouble embeddedDouble(Document document, Object... keys) {
        return embeddedDouble(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code double} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalDouble}
     */
    public static final OptionalDouble embeddedDouble(Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return OptionalDouble.empty();
            }
            i++;
        }
        if (value == null) {
            return OptionalDouble.empty();
        }
        if (value instanceof Number) {
            return OptionalDouble.of(((Number) value).doubleValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code LocalDateTime} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<LocalDateTime>}
     */
    public static final Optional<LocalDateTime> embeddedDateTime(Document document, Object... keys) {
        return embeddedDateTime(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code LocalDateTime} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<LocalDateTime>}
     */
    public static final Optional<LocalDateTime> embeddedDateTime(Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return Optional.empty();
            }
            i++;
        }
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Date) {
            return Optional.of(LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
        }
        throw new ClassCastException(String.format("The value is not a Date (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code ZonedDateTime} value in an embedded document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<ZonedDateTime>}
     */
    public static final Optional<ZonedDateTime> embeddedDateTime(ZoneId zone, Document document, Object... keys) {
        return embeddedDateTime(zone, document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code ZonedDateTime} value in an embedded document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<ZonedDateTime>}
     */
    public static final Optional<ZonedDateTime> embeddedDateTime(ZoneId zone, Document document, List<Object> keys) {
        Object value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value instanceof Document) {
                value = ((Document) value).get(key);
            } else if (value instanceof List) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (List<?>) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return Optional.empty();
            }
            i++;
        }
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Date) {
            return Optional.of(((Date) value).toInstant().atZone(zone));
        }
        throw new ClassCastException(String.format("The value is not a Date (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code string} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<String>}
     */
    public static final Optional<String> stringValue(Document document, String key) {
        return Optional.ofNullable(document.getString(key));
    }

    /**
     * Gets the {@code Document} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<Document>}
     */
    public static final Optional<Document> documentValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Document) {
            return Optional.of((Document) value);
        }
        throw new ClassCastException(String.format("The value is not a Document (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code int} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalInt}
     */
    public static final OptionalInt intValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return OptionalInt.empty();
        }
        if (value instanceof Number) {
            return OptionalInt.of(((Number) value).intValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code long} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalLong}
     */
    public static final OptionalLong longValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return OptionalLong.empty();
        }
        if (value instanceof Number) {
            return OptionalLong.of(((Number) value).longValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code double} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalDouble}
     */
    public static final OptionalDouble doubleValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return OptionalDouble.empty();
        }
        if (value instanceof Number) {
            return OptionalDouble.of(((Number) value).doubleValue());
        }
        throw new ClassCastException(String.format("The value is not a Number (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code LocalDateTime} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<LocalDateTime>}
     */
    public static final Optional<LocalDateTime> dateTimeValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Date) {
            return Optional.of(DateTimeUtil.local((Date) value));
        }
        throw new ClassCastException(String.format("The value is not a Date (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code ZonedDateTime} value in an document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<ZonedDateTime>}
     */
    public static final Optional<ZonedDateTime> dateTimeValue(ZoneId zone, Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Date) {
            return Optional.of(DateTimeUtil.zoned((Date) value, zone));
        }
        throw new ClassCastException(String.format("The value is not a Date (%s)", value.getClass().getName()));
    }

    /**
     * Convert the specified {@link LocalDateTime} to {@link BsonDateTime}.
     * 
     * @param time the {@code LocalDateTime} to be converted
     * @return a {@code BsonDateTime}
     */
    public static final BsonDateTime toBsonDateTime(LocalDateTime time) {
        return toBsonDateTime(time.atZone(ZoneId.systemDefault()));
    }

    /**
     * Convert the specified {@link ZonedDateTime} to {@link BsonDateTime}.
     * 
     * @param time the {@code ZonedDateTime} to be converted
     * @return a {@code BsonDateTime}
     */
    public static final BsonDateTime toBsonDateTime(ZonedDateTime time) {
        return new BsonDateTime(time.toInstant().toEpochMilli());
    }

    /**
     * Gets the {@link BsonValue} in an embedded document.
     * 
     * @param <V>      the type of the return value
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<V>}
     */
    public static final <V extends BsonValue> Optional<V> embedded(BsonDocument document, Object... keys) {
        return embedded(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@link BsonValue} in an embedded document.
     * 
     * @param <V>      the type of the return value
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<V>}
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public static final <V extends BsonValue> Optional<V> embedded(BsonDocument document, List<Object> keys) {
        BsonValue value = Objects.requireNonNull(document, "document must not be null");
        var i = 0;
        for (var key : keys) {
            if (value.isNull()) {
                return Optional.empty();
            } else if (value.isDocument()) {
                value = ((BsonDocument) value).get(key);
            } else if (value.isArray()) {
                var index = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                var list = (BsonArray) value;
                if (index < list.size()) {
                    value = list.get(index);
                } else {
                    value = null;
                }
            } else {
                var path = keys.stream().limit(i).map(String::valueOf).collect(Collectors.joining("."));
                throw new ClassCastException(
                        String.format("At dot notation \"%s\", the value is not a Document or a List (%s)", path,
                                value.getClass().getName()));
            }
            if (value == null) {
                return Optional.empty();
            }
            i++;
        }
        return value == null ? Optional.empty() : Optional.of((V) value);
    }

    /**
     * Gets the {@code BsonDocument} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<BsonDocument>}
     * @since 2.1
     */
    public static final Optional<BsonDocument> embeddedDocument(BsonDocument document, Object... keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code BsonDocument} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<BsonDocument>}
     * @since 2.1
     */
    public static final Optional<BsonDocument> embeddedDocument(BsonDocument document, List<Object> keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code int} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalInt}
     * @since 2.1
     */
    public static final OptionalInt embeddedInt(BsonDocument document, Object... keys) {
        return embeddedInt(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code int} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalInt}
     * @since 2.1
     */
    public static final OptionalInt embeddedInt(BsonDocument document, List<Object> keys) {
        Optional<BsonNumber> value = embedded(document, keys);
        if (value.isPresent()) {
            return OptionalInt.of(value.get().intValue());
        }
        return OptionalInt.empty();
    }

    /**
     * Gets the {@code long} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalLong}
     * @since 2.1
     */
    public static final OptionalLong embeddedLong(BsonDocument document, Object... keys) {
        return embeddedLong(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code long} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalLong}
     * @since 2.1
     */
    public static final OptionalLong embeddedLong(BsonDocument document, List<Object> keys) {
        Optional<BsonNumber> value = embedded(document, keys);
        if (value.isPresent()) {
            return OptionalLong.of(value.get().longValue());
        }
        return OptionalLong.empty();
    }

    /**
     * Gets the {@code double} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code OptionalDouble}
     * @since 2.1
     */
    public static final OptionalDouble embeddedDouble(BsonDocument document, Object... keys) {
        return embeddedDouble(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code double} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code OptionalDouble}
     * @since 2.1
     */
    public static final OptionalDouble embeddedDouble(BsonDocument document, List<Object> keys) {
        Optional<BsonNumber> value = embedded(document, keys);
        if (value.isPresent()) {
            return OptionalDouble.of(value.get().doubleValue());
        }
        return OptionalDouble.empty();
    }

    /**
     * Gets the {@code LocalDateTime} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<LocalDateTime>}
     * @since 2.1
     */
    public static final Optional<LocalDateTime> embeddedDateTime(BsonDocument document, Object... keys) {
        return embeddedDateTime(document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code LocalDateTime} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<LocalDateTime>}
     * @since 2.1
     */
    public static final Optional<LocalDateTime> embeddedDateTime(BsonDocument document, List<Object> keys) {
        return embedded(document, keys).map(BsonUtil::toLocalDateTime);
    }

    /**
     * Gets the {@code ZonedDateTime} value in an embedded document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<ZonedDateTime>}
     * @since 2.1
     */
    public static final Optional<ZonedDateTime> embeddedDateTime(ZoneId zone, BsonDocument document, Object... keys) {
        return embeddedDateTime(zone, document, Arrays.asList(keys));
    }

    /**
     * Gets the {@code ZonedDateTime} value in an embedded document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<ZonedDateTime>}
     * @since 2.1
     */
    public static final Optional<ZonedDateTime> embeddedDateTime(ZoneId zone, BsonDocument document,
            List<Object> keys) {
        return embedded(document, keys).map(value -> toZonedDateTime(value, zone));
    }

    /**
     * Gets the {@code string} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<String>}
     * @since 2.1
     */
    public static final Optional<String> stringValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        return Optional.of(value.asString().getValue());
    }

    /**
     * Gets the {@code BsonDocument} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<BsonDocument>}
     * @since 2.1
     */
    public static final Optional<BsonDocument> documentValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        return Optional.of(value.asDocument());
    }

    /**
     * Gets the {@code int} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalInt}
     */
    public static final OptionalInt intValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return OptionalInt.empty();
        }
        if (value instanceof BsonNumber) {
            return OptionalInt.of(((BsonNumber) value).intValue());
        }
        throw new ClassCastException(String.format("The value is not a BsonNumber (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code long} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalLong}
     */
    public static final OptionalLong longValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return OptionalLong.empty();
        }
        if (value instanceof BsonNumber) {
            return OptionalLong.of(((BsonNumber) value).longValue());
        }
        throw new ClassCastException(String.format("The value is not a BsonNumber (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code double} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code OptionalDouble}
     */
    public static final OptionalDouble doubleValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return OptionalDouble.empty();
        }
        if (value instanceof BsonNumber) {
            return OptionalDouble.of(((BsonNumber) value).doubleValue());
        }
        throw new ClassCastException(String.format("The value is not a BsonNumber (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code LocalDateTime} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<LocalDateTime>}
     */
    public static final Optional<LocalDateTime> dateTimeValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        return Optional.of(BsonUtil.toLocalDateTime(value));
    }

    /**
     * Gets the {@code ZonedDateTime} value in an document.
     * 
     * @param zone     the zone to combine with, not null
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<ZonedDateTime>}
     */
    public static final Optional<ZonedDateTime> dateTimeValue(ZoneId zone, BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        if (value.isDateTime()) {
            return Optional.of(ZonedDateTime.ofInstant(Instant.ofEpochMilli(((BsonDateTime) value).getValue()), zone));
        } else if (value.isTimestamp()) {
            return Optional.of(DateTimeUtil.zoned(((BsonTimestamp) value).getTime(), zone));
        }
        throw new ClassCastException(
                String.format("The value is not a BsonDateTime or BsonTimestamp (%s)", value.getClass().getName()));
    }

    /**
     * 
     * Gets the {@code ObjectId} value in an document with key {@code "_id"}.
     * 
     * @param document the source document
     * @return an {@code Optional<ObjectId>}
     * @since 2.1
     */
    public static final Optional<ObjectId> objectIdValue(Document document) {
        return objectIdValue(document, "_id");
    }

    /**
     * Gets the {@code ObjectId} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<ObjectId>}
     * @since 2.1
     */
    public static final Optional<ObjectId> objectIdValue(Document document, String key) {
        return Optional.ofNullable(document.getObjectId(key));
    }

    /**
     * 
     * Gets the {@code ObjectId} value in an document with key {@code "_id"}.
     * 
     * @param document the source document
     * @return an {@code Optional<ObjectId>}
     * @since 2.1
     */
    public static final Optional<ObjectId> objectIdValue(BsonDocument document) {
        return objectIdValue(document, "_id");
    }

    /**
     * Gets the {@code ObjectId} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<ObjectId>}
     * @since 2.1
     */
    public static final Optional<ObjectId> objectIdValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        return Optional.of(value.asObjectId().getValue());
    }

    /**
     * Parse the JSON value to {@link Document}.
     * 
     * @param json the source JSON string
     * @return a {@code Document}
     * 
     * @since 2.1
     */
    public static final Document parseToDocument(String json) {
        return parseToDocument(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parse the JSON value to {@link Document}.
     * 
     * @param json the source byte array stores JSON value
     * @return a {@code Document}
     */
    public static final Document parseToDocument(byte[] json) {
        var iterator = JsonIterator.parse(json);
        try {
            var type = iterator.whatIsNext();
            if (type == ValueType.OBJECT) {
                return convertToDocument(iterator);
            }
            throw new IllegalArgumentException("the given json expected an OBJECT but was " + type);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private static final Document convertToDocument(JsonIterator iterator) throws IOException {
        var doc = new Document();
        for (var field = iterator.readObject(); field != null; field = iterator.readObject()) {
            var type = iterator.whatIsNext();
            if (type == ValueType.OBJECT) {
                doc.put(field, convertToDocument(iterator));
            } else if (type == ValueType.ARRAY) {
                doc.put(field, convertToList(iterator));
            } else {
                doc.put(field, convertToValue(type, iterator));
            }
        }
        return doc;
    }

    private static final List<Object> convertToList(JsonIterator iterator) throws IOException {
        var list = new ArrayList<>();
        for (; iterator.readArray();) {
            var type = iterator.whatIsNext();
            if (type == ValueType.OBJECT) {
                list.add(convertToDocument(iterator));
            } else if (type == ValueType.ARRAY) {
                list.add(convertToList(iterator));
            } else {
                list.add(convertToValue(type, iterator));
            }
        }
        return list;
    }

    private static final Object convertToValue(ValueType type, JsonIterator iterator) throws IOException {
        if (type == ValueType.STRING) {
            return iterator.readString();
        } else if (type == ValueType.NUMBER) {
            var str = iterator.readNumberAsString();
            var dotIndex = str.indexOf('.');
            if (dotIndex == -1) {
                try {
                    long value = Long.parseLong(str);
                    if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                        return Integer.valueOf((int) value);
                    }
                    return Long.valueOf(value);
                } catch (NumberFormatException e) {
                    return Decimal128.parse(str);
                }
            } else {
                if (str.length() > 15) {
                    return Decimal128.parse(str);
                }
                return Double.valueOf(str);
            }
        } else if (type == ValueType.NULL) {
            iterator.readNull();
            return null;
        } else if (type == ValueType.BOOLEAN) {
            return Boolean.valueOf(iterator.readBoolean());
        }
        iterator.skip();
        return null;
    }

    /**
     * Parse the JSON value to {@link BsonValue}.
     * 
     * @param <T>  the type of the return value
     * @param json the source JSON string
     * @return a {@code BsonValue}
     * @since 2.1
     */
    public static final <T extends BsonValue> T parseToBson(String json) {
        return parseToBson(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parse the JSON value to {@link BsonValue}.
     * 
     * @param <T>  the type of the return value
     * @param json the source byte array stores JSON value
     * @return a {@code BsonValue}
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public static final <T extends BsonValue> T parseToBson(byte[] json) {
        var iterator = JsonIterator.parse(json);
        try {
            return (T) convertToBson(iterator);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private static final BsonValue convertToBson(JsonIterator iterator) throws IOException {
        var type = iterator.whatIsNext();
        if (type == ValueType.OBJECT) {
            var doc = new BsonDocument();
            for (var field = iterator.readObject(); field != null; field = iterator.readObject()) {
                doc.put(field, convertToBson(iterator));
            }
            return doc;
        } else if (type == ValueType.ARRAY) {
            BsonArray array = new BsonArray();
            for (; iterator.readArray();) {
                array.add(convertToBson(iterator));
            }
            return array;
        } else {
            if (type == ValueType.STRING) {
                return new BsonString(iterator.readString());
            } else if (type == ValueType.NUMBER) {
                var str = iterator.readNumberAsString();
                var dotIndex = str.indexOf('.');
                if (dotIndex == -1) {
                    try {
                        long value = Long.parseLong(str);
                        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                            return new BsonInt32((int) value);
                        }
                        return new BsonInt64(value);
                    } catch (NumberFormatException e) {
                        return new BsonDecimal128(Decimal128.parse(str));
                    }
                } else {
                    if (str.length() > 15) {
                        return new BsonDecimal128(Decimal128.parse(str));
                    }
                    double value = Double.parseDouble(str);
                    return new BsonDouble(value);
                }
            } else if (type == ValueType.NULL) {
                iterator.readNull();
                return BsonNull.VALUE;
            } else if (type == ValueType.BOOLEAN) {
                return BsonBoolean.valueOf(iterator.readBoolean());
            }
            iterator.skip();
            // no other type
            return BsonNull.VALUE;
        }
    }

    /**
     * Gets the list value in an embedded document.
     * 
     * @param <E>      the type of the element in list
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<List<E>>}
     * 
     * @since 2.2
     */
    public static final <E> Optional<List<E>> embeddedList(Document document, Object... keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the list value in an embedded document.
     * 
     * @param <E>      the type of the element in list
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<List<E>>}
     * 
     * @since 2.2
     */
    public static final <E> Optional<List<E>> embeddedList(Document document, List<Object> keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code Document} value in an document.
     * 
     * @param <E>      the type of the element in list
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<List<E>>}
     * 
     * @since 2.2
     */
    @SuppressWarnings("unchecked")
    public static final <E> Optional<List<E>> listValue(Document document, String key) {
        var value = document.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof List) {
            return Optional.of((List<E>) value);
        }
        throw new ClassCastException(String.format("The value is not a List (%s)", value.getClass().getName()));
    }

    /**
     * Gets the {@code BsonArray} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the array of keys
     * @return an {@code Optional<BsonArray>}
     * 
     * @since 2.2
     */
    public static final Optional<BsonArray> embeddedArray(BsonDocument document, Object... keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code BsonArray} value in an embedded document.
     * 
     * @param document the source document
     * @param keys     the list of keys
     * @return an {@code Optional<BsonArray>}
     * 
     * @since 2.2
     */
    public static final Optional<BsonArray> embeddedArray(BsonDocument document, List<Object> keys) {
        return embedded(document, keys);
    }

    /**
     * Gets the {@code BsonArray} value in an document.
     * 
     * @param document the source document
     * @param key      the key
     * @return an {@code Optional<BsonArray>}
     * 
     * @since 2.2
     */
    public static final Optional<BsonArray> arrayValue(BsonDocument document, String key) {
        var value = document.get(key);
        if (value == null || value.isNull()) {
            return Optional.empty();
        }
        return Optional.of(value.asArray());
    }

    /**
     * Converts the specified {@link BsonValue} to {@link LocalDateTime} with the
     * system default time-zone.
     * 
     * @param value the {@code BsonValue}
     * @return a {@code LocalDateTime}
     * 
     * @since 2.3
     */
    public static final LocalDateTime toLocalDateTime(BsonValue value) {
        return toLocalDateTime(value, ZoneId.systemDefault());
    }

    /**
     * 
     * Converts the specified {@link BsonValue} to {@link LocalDateTime}.
     * 
     * @param value the {@code BsonValue}
     * @param zone  the time zone
     * @return a {@code LocalDateTime}
     * 
     * @since 2.3
     */
    public static final LocalDateTime toLocalDateTime(BsonValue value, ZoneId zone) {
        if (value.isDateTime()) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((BsonDateTime) value).getValue()), zone);
        } else if (value.isTimestamp()) {
            return DateTimeUtil.local(((BsonTimestamp) value).getTime());
        }
        throw new ClassCastException(
                String.format("The value is not a BsonDateTime or BsonTimstamp (%s)", value.getClass().getName()));
    }

    /**
     * Converts the specified {@link BsonValue} to {@link ZonedDateTime} with the
     * system default time-zone.
     * 
     * @param value the {@code BsonValue}
     * @return a {@code ZonedDateTime}
     * 
     * @since 2.3
     */
    public static final ZonedDateTime toZonedDateTime(BsonValue value) {
        return toZonedDateTime(value, ZoneId.systemDefault());
    }

    /**
     * Converts the specified {@link BsonValue} to {@link ZonedDateTime}.
     * 
     * @param value the {@code BsonValue}
     * @param zone  the time zone
     * @return a {@code ZonedDateTime}
     * 
     * @since 2.3
     */
    public static final ZonedDateTime toZonedDateTime(BsonValue value, ZoneId zone) {
        if (value.isDateTime()) {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(((BsonDateTime) value).getValue()), zone);
        } else if (value.isTimestamp()) {
            return DateTimeUtil.zoned(((BsonTimestamp) value).getTime(), zone);
        }
        throw new ClassCastException(
                String.format("The value is not a BsonDateTime or BsonTimstamp (%s)", value.getClass().getName()));
    }

    /**
     * Converts the specified {@link BsonValue} to {@link OffsetDateTime} with the
     * system default time-zone.
     * 
     * @param value the {@code BsonValue}
     * @param zone  the time zone
     * @return a {@code OffsetDateTime}
     * 
     * @since 2.3
     */
    public static final OffsetDateTime toOffsetDateTime(BsonValue value) {
        return toOffsetDateTime(value, ZoneId.systemDefault());
    }

    /**
     * Converts the specified {@link BsonValue} to {@link OffsetDateTime}.
     * 
     * @param value the {@code BsonValue}
     * @param zone  the time zone
     * @return a {@code OffsetDateTime}
     * 
     * @since 2.3
     */
    public static final OffsetDateTime toOffsetDateTime(BsonValue value, ZoneId zone) {
        if (value.isDateTime()) {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(((BsonDateTime) value).getValue()), zone);
        } else if (value.isTimestamp()) {
            return DateTimeUtil.offset(((BsonTimestamp) value).getTime(), zone);
        }
        throw new ClassCastException(
                String.format("The value is not a BsonDateTime or BsonTimstamp (%s)", value.getClass().getName()));
    }

    private BsonUtil() {
    }

}
