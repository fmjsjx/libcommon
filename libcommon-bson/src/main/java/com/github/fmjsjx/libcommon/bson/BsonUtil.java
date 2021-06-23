package com.github.fmjsjx.libcommon.bson;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import org.bson.BsonDateTime;
import org.bson.Document;

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
            return Optional.of(LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
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
            return Optional.of(((Date) value).toInstant().atZone(zone));
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

    private BsonUtil() {
    }
}
