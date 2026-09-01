package com.github.fmjsjx.libcommon.collection;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fmjsjx.libcommon.util.ArrayUtil;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.util.Objects.checkIndex;

/**
 * This class implements the {@link IntList} interface, backed by a resizable
 * {@code int} array that avoids boxing and unboxing overhead.
 *
 * @author MJ Fang
 * @since 4.3
 */
public class IntArrayList extends AbstractList<@NonNull Integer>
        implements IntList, RandomAccess, Cloneable {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final int[] EMPTY_VALUES = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final int[] DEFAULT_CAPACITY_EMPTY_VALUES = {};

    @SuppressWarnings("SuspiciousSystemArraycopy")
    static void fastRemoveByIndices(@NonNull Object values, int start, int end, @NonNull IntArrayList toRemoveIndices) {
        var base = start - 1;
        var prevIndex = base;
        var prevDestIndex = -1;
        for (var i = 0; i < toRemoveIndices.size(); i++) {
            // Use valueData method to avoid unnecessary range check
            var index = toRemoveIndices.valueData(i);
            if (prevIndex == base) {
                prevDestIndex = index;
            } else {
                var length = index - prevIndex - 1;
                if (length > 0) {
                    System.arraycopy(values, prevIndex + 1, values, prevDestIndex, length);
                    prevDestIndex += length;
                }
            }
            prevIndex = index;
        }
        var length = end - prevIndex - 1;
        if (length > 0) {
            System.arraycopy(values, prevIndex + 1, values, prevDestIndex, length);
        }
    }

    private int[] values;
    private int size;

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IntArrayList() {
        values = DEFAULT_CAPACITY_EMPTY_VALUES;
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     *                                  negative
     */
    public IntArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            values = new int[initialCapacity];
        } else if (initialCapacity == 0) {
            values = EMPTY_VALUES;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * Constructs a list containing the values in the specified array.
     *
     * @param values the array whose values are to be placed into this list
     */
    public IntArrayList(int... values) {
        if (values.length != 0) {
            this.values = Arrays.copyOf(values, values.length);
            size = values.length;
        } else {
            this.values = EMPTY_VALUES;
        }
    }

    /**
     * Constructs a list containing the elements in the specified collection.
     *
     * @param c the collection whose elements are to be placed into this list
     */
    public IntArrayList(Collection<? extends @NonNull Integer> c) {
        if (c.isEmpty()) {
            values = EMPTY_VALUES;
        } else if (c instanceof IntArrayList other) {
            values = Arrays.copyOf(other.values, other.size);
            size = other.size;
        } else {
            values = new int[c.size()];
            var i = 0;
            for (var e : c) {
                values[i++] = e;
            }
            size = i;
        }
    }

    /**
     * Returns a shallow copy of this {@code IntArrayList} instance.
     *
     * @return a clone of this {@code IntArrayList} instance
     */
    @Override
    public @NonNull Object clone() {
        try {
            var clone = (IntArrayList) super.clone();
            clone.values = Arrays.copyOf(values, size);
            clone.modCount = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public boolean contains(int value) {
        return ArrayUtil.contains(values, 0, size, value);
    }

    /**
     * This helper method split out from add(E) to keep method
     * bytecode size under 35 (the -XX:MaxInlineSize default value),
     * which helps when add(E) is called in a C1-compiled loop.
     *
     * @param value  value to be added
     * @param values the array to be added to
     * @param s      the size of the array to be added to
     */
    private void add(int value, int[] values, int s) {
        if (s == values.length) {
            values = grow();
        }
        values[s] = value;
        size = s + 1;
    }

    @Override
    public boolean add(int value) {
        modCount++;
        add(value, values, size);
        return true;
    }

    @Override
    public int valueAt(int index) {
        checkIndex(index, size);
        return valueData(index);
    }

    int valueData(int index) {
        return values[index];
    }

    @Override
    public int set(int index, int value) {
        var oldValue = valueAt(index);
        values[index] = value;
        return oldValue;
    }

    @Override
    public void add(int index, int value) {
        rangeCheckForAdd(index);
        modCount++;
        var size = this.size;
        var values = this.values;
        if (size == values.length) {
            values = grow();
        }
        System.arraycopy(values, index, values, index + 1, size - index);
        values[index] = value;
        this.size = size + 1;
    }

    @Override
    public int indexOf(int value) {
        return indexOfRange(value, 0, size);
    }

    private int indexOfRange(int value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = start; i < end; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(int value) {
        return lastIndexOfRange(value, 0, size);
    }

    private int lastIndexOfRange(int value, @SuppressWarnings("SameParameterValue") int start, int end) {
        var values = this.values;
        for (var i = end - 1; i >= start; i--) {
            if (values[i] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int removeAt(int index) {
        checkIndex(index, size);
        var values = this.values;
        var oldValue = values[index];
        fastRemove(values, index);
        return oldValue;
    }

    private void fastRemove(int[] values, int index) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > index) {
            System.arraycopy(values, index + 1, values, index, newSize - index);
        }
        size = newSize;
    }

    @Override
    public boolean removeFirst(int value) {
        var index = indexOf(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAllValue(int value) {
        var values = this.values;
        var size = this.size;
        var toRemoveIndices = new IntArrayList();
        for (var i = 0; i < size; i++) {
            if (values[i] == value) {
                toRemoveIndices.add(i);
            }
        }
        return fastRemove(values, 0, size, toRemoveIndices);
    }

    private boolean fastRemove(int @NonNull [] values, @SuppressWarnings("SameParameterValue") int start, int end, @NonNull IntArrayList toRemoveIndices) {
        if (toRemoveIndices.isEmpty()) {
            return false;
        }
        modCount++;
        fastRemoveByIndices(values, start, end, toRemoveIndices);
        size -= toRemoveIndices.size();
        return true;
    }

    @Override
    public boolean removeAll(int @NonNull ... values) {
        var size = this.size;
        var valuesData = this.values;
        var toRemoveIndices = new IntArrayList();
        for (var i = 0; i < size; i++) {
            if (ArrayUtil.contains(values, valuesData[i])) {
                toRemoveIndices.add(i);
            }
        }
        return fastRemove(valuesData, 0, size, toRemoveIndices);
    }

    @Override
    public @NonNull IntStream intStream() {
        return Arrays.stream(values, 0, size);
    }

    @Override
    public int @NonNull [] toIntArray() {
        return Arrays.copyOf(values, size);
    }

    @Override
    public void forEach(@NonNull IntConsumer action) {
        for (var i = 0; i < size; i++) {
            action.accept(values[i]);
        }
    }

    @Override
    public boolean contains(@NonNull Object o) {
        return switch (o) {
            case Integer i -> contains(i.intValue());
            case Long l -> l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE && contains(l.intValue());
            case Short s -> contains(s.intValue());
            case Byte b -> contains(b.intValue());
            case BigInteger bi -> contains(bi.intValueExact());
            default -> false;
        };
    }

    @Override
    public @NonNull Integer get(int index) {
        return valueAt(index);
    }

    @Override
    public @NonNull Integer set(int index, @NonNull Integer element) {
        return set(index, element.intValue());
    }

    @Override
    public boolean add(@NonNull Integer e) {
        return add(e.intValue());
    }

    @Override
    public void add(int index, @NonNull Integer element) {
        add(index, element.intValue());
    }

    @Override
    public @NonNull Integer remove(int index) {
        return removeAt(index);
    }

    @Override
    public @NonNull Integer removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        } else {
            var values = this.values;
            var oldValue = values[0];
            fastRemove(values, 0);
            return oldValue;
        }
    }

    @Override
    public @NonNull Integer removeLast() {
        var last = size - 1;
        if (last < 0) {
            throw new NoSuchElementException();
        } else {
            var values = this.values;
            var oldValue = values[last];
            fastRemove(values, last);
            return oldValue;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        modCount++;
        size = 0;
    }

    /**
     * Increases the capacity of this {@code IntArrayList} instance, if
     * necessary, to ensure that it can hold at least the number of
     * elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > values.length
                && !(values == DEFAULT_CAPACITY_EMPTY_VALUES
                && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }

    private int[] grow(int minCapacity) {
        var oldCapacity = values.length;
        if (oldCapacity > 0 || values != DEFAULT_CAPACITY_EMPTY_VALUES) {
            var newCapacity = ArrayUtil.newLength(oldCapacity,
                    minCapacity - oldCapacity,
                    oldCapacity >> 1);
            return values = Arrays.copyOf(values, newCapacity);
        } else {
            return values = new int[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    private int[] grow() {
        return grow(size + 1);
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private @NonNull String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    /**
     * {@code IntArrayList} jsoniter support.
     */
    public static final class IntArrayListJsoniterSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code IntArrayListJsoniterSupport} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code IntArrayListJsoniterSupport} is enabled,
         *         {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        private static final JsonIterator.ReadArrayCallback READ_INT_ARRAY_LIST = (subIter, attachment) -> {
            var valueType = subIter.whatIsNext();
            if (valueType != ValueType.NUMBER) {
                throw new JsonException("expect int, but found " + valueType);
            }
            ((IntArrayList) attachment).add(subIter.readInt());
            return true;
        };

        /**
         * Enables {@code IntArrayListJsoniterSupport}.
         */
        public static final void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(IntArrayList.class, new Encoder.ReflectionEncoder() {

                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        if (obj == null) {
                            stream.writeNull();
                            return;
                        }
                        IntArrayList list = (IntArrayList) obj;
                        var len = list.size();
                        if (len == 0) {
                            stream.writeEmptyArray();
                            return;
                        }
                        stream.writeArrayStart();
                        stream.writeIndention();
                        stream.writeVal(list.valueData(0));
                        for (var i = 1; i < len; i++) {
                            stream.writeMore();
                            stream.writeVal(list.valueData(i));
                        }
                        stream.writeArrayEnd();
                    }

                    @Override
                    public Any wrap(Object obj) {
                        IntArrayList list = (IntArrayList) obj;
                        return Any.wrap(list.toIntArray());
                    }

                });
                JsoniterSpi.registerTypeDecoder(IntArrayList.class, iter -> {
                    var list = new IntArrayList();
                    iter.readArrayCB(READ_INT_ARRAY_LIST, list);
                    return list;
                });
                JsoniterSpi.registerTypeDecoder(IntList.class, iter -> {
                    var list = new IntArrayList();
                    iter.readArrayCB(READ_INT_ARRAY_LIST, list);
                    return list;
                });
            } else {
                throw new IllegalStateException("IntArrayListSupport.enable can only be called once");
            }
        }

        private IntArrayListJsoniterSupport() {
        }

    }

    /**
     * {@code IntArrayList} fastjson2 support.
     */
    public static final class IntArrayListFastjson2Support {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        /**
         * Returns {@code true} if {@code IntArrayListFastjson2Support} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code IntArrayListFastjson2Support} is enabled,
         *         {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Enables {@code IntArrayListFastjson2Support}.
         */
        public static final void enable() {
            if (enabled.compareAndSet(false, true)) {
                JSON.register(IntArrayList.class, (jsonWriter, object, fieldName, fieldType, features) -> {
                    if (object == null) {
                        jsonWriter.writeNull();
                        return;
                    }
                    IntArrayList list = (IntArrayList) object;
                    var len = list.size();
                    jsonWriter.startArray();
                    for (var i = 0; i < len; i++) {
                        if (i > 0) {
                            jsonWriter.writeComma();
                        }
                        jsonWriter.writeInt32(list.valueData(i));
                    }
                    jsonWriter.endArray();
                });
                ObjectReader<IntArrayList> reader = (jsonReader, fieldType, fieldName, features) -> {
                    if (jsonReader.nextIfNull()) {
                        return null;
                    }
                    var list = new IntArrayList();
                    jsonReader.nextIfArrayStart();
                    while (!jsonReader.nextIfArrayEnd()) {
                        list.add(jsonReader.readInt32Value());
                    }
                    return list;
                };
                JSON.register(IntArrayList.class, reader);
                JSON.register(IntList.class, reader);
            } else {
                throw new IllegalStateException("IntArrayListFastjson2Support.enable can only be called once");
            }
        }

        private IntArrayListFastjson2Support() {
        }

    }

    /**
     * {@code IntArrayList} Jackson2 support.
     */
    public static final class IntArrayListJackson2Support {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        private static final JsonSerializer<IntArrayList> SERIALIZER = new JsonSerializer<>() {
            @Override
            public void serialize(IntArrayList list, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                var len = list.size();
                gen.writeStartArray();
                for (var i = 0; i < len; i++) {
                    gen.writeNumber(list.valueData(i));
                }
                gen.writeEndArray();
            }
        };

        private static final JsonDeserializer<IntArrayList> DESERIALIZER = new JsonDeserializer<>() {
            @Override
            public IntArrayList deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (!p.isExpectedStartArrayToken()) {
                    throw MismatchedInputException.from(p, IntArrayList.class, "expect JSON array, but found " + p.currentToken());
                }
                var list = new IntArrayList();
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    list.add(p.getIntValue());
                }
                return list;
            }
        };

        /**
         * Returns {@code true} if {@code IntArrayListJackson2Support} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code IntArrayListJackson2Support} is enabled,
         *         {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Returns a new Jackson2 {@link Module} supporting encoding/decoding
         * {@code IntArrayList}s and {@code IntList}s.
         *
         * @return a Jackson2 {@code Module}
         */
        public static final Module module() {
            var module = new SimpleModule("IntArrayListJackson2Module");
            module.addSerializer(IntArrayList.class, SERIALIZER);
            module.addDeserializer(IntArrayList.class, DESERIALIZER);
            module.addDeserializer(IntList.class, DESERIALIZER);
            return module;
        }

        /**
         * Enables {@code IntArrayListJackson2Support} on the specified
         * {@code ObjectMapper}.
         *
         * @param mapper the {@code ObjectMapper} to enable the support on
         */
        public static final void enable(ObjectMapper mapper) {
            if (enabled.compareAndSet(false, true)) {
                mapper.registerModule(module());
            } else {
                throw new IllegalStateException("IntArrayListJackson2Support.enable can only be called once");
            }
        }

        private IntArrayListJackson2Support() {
        }

    }

    /**
     * {@code IntArrayList} Jackson3 support.
     */
    public static final class IntArrayListJackson3Support {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        private static final tools.jackson.databind.ser.std.StdSerializer<IntArrayList> SERIALIZER = new tools.jackson.databind.ser.std.StdSerializer<>(IntArrayList.class) {
            @Override
            public void serialize(IntArrayList list, tools.jackson.core.JsonGenerator gen, tools.jackson.databind.SerializationContext provider) {
                var len = list.size();
                gen.writeStartArray();
                for (var i = 0; i < len; i++) {
                    gen.writeNumber(list.valueData(i));
                }
                gen.writeEndArray();
            }
        };

        private static final tools.jackson.databind.deser.std.StdDeserializer<IntArrayList> DESERIALIZER = new tools.jackson.databind.deser.std.StdDeserializer<>(IntArrayList.class) {
            @Override
            public IntArrayList deserialize(tools.jackson.core.JsonParser p, tools.jackson.databind.DeserializationContext ctxt) {
                if (!p.isExpectedStartArrayToken()) {
                    throw tools.jackson.databind.exc.MismatchedInputException.from(p, IntArrayList.class, "expect JSON array, but found " + p.currentToken());
                }
                var list = new IntArrayList();
                while (p.nextToken() != tools.jackson.core.JsonToken.END_ARRAY) {
                    list.add(p.getIntValue());
                }
                return list;
            }
        };

        /**
         * Returns {@code true} if {@code IntArrayListJackson3Support} is enabled,
         * {@code false} otherwise.
         *
         * @return {@code true} if {@code IntArrayListJackson3Support} is enabled,
         *         {@code false} otherwise
         */
        public static final boolean enabled() {
            return enabled.get();
        }

        /**
         * Returns a new Jackson3 module supporting encoding/decoding
         * {@code IntArrayList}s and {@code IntList}s.
         *
         * @return a Jackson3 module
         */
        public static final tools.jackson.databind.JacksonModule module() {
            var module = new tools.jackson.databind.module.SimpleModule("IntArrayListJackson3Module");
            module.addSerializer(IntArrayList.class, SERIALIZER);
            module.addDeserializer(IntArrayList.class, DESERIALIZER);
            module.addDeserializer(IntList.class, DESERIALIZER);
            return module;
        }

        /**
         * Enables {@code IntArrayListJackson3Support} on the specified mapper
         * builder.
         *
         * @param builder the mapper builder to enable the support on
         */
        public static final void enable(tools.jackson.databind.cfg.MapperBuilder<?, ?> builder) {
            if (enabled.compareAndSet(false, true)) {
                builder.addModule(module());
            } else {
                throw new IllegalStateException("IntArrayListJackson3Support.enable can only be called once");
            }
        }

        private IntArrayListJackson3Support() {
        }

    }

}
