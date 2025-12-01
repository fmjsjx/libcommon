package com.github.fmjsjx.libcommon.json;

import com.github.fmjsjx.libcommon.util.ReflectUtil;
import com.jsoniter.any.Any;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.List;

/**
 * Class that registers capability of serializing jsoniter {@code Any} objects with the Jackson3.
 *
 * @since 3.17
 */
public class Jackson3JsoniterModule extends SimpleModule {

    /**
     * Returns {@code true} if jsoniter is available, {@code false} otherwise.
     *
     * @return {@code true} if jsoniter is available, {@code false} otherwise
     */
    public static final boolean isJsoniterAvailable() {
        return JsoniterAvailableHolder.jsoniterAvailable;
    }

    private static final class JsoniterAvailableHolder {
        private static final boolean jsoniterAvailable = ReflectUtil.hasClassForName("com.jsoniter.any.Any");
    }

    /**
     * Returns the singleton {@link Jackson3JsoniterModule} instance.
     *
     * @return the singleton {@link Jackson3JsoniterModule} instance
     */
    public static final Jackson3JsoniterModule getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final Jackson3JsoniterModule INSTANCE = new Jackson3JsoniterModule();
    }

    private static final List<String> anyClasses() {
        return List.of(
                "com.jsoniter.any.Any",
                "com.jsoniter.any.LazyAny",
                "com.jsoniter.any.NotFoundAny",
                "com.jsoniter.any.TrueAny",
                "com.jsoniter.any.FalseAny",
                "com.jsoniter.any.ArrayLazyAny",
                "com.jsoniter.any.DoubleAny",
                "com.jsoniter.any.FloatAny",
                "com.jsoniter.any.IntAny",
                "com.jsoniter.any.LongAny",
                "com.jsoniter.any.NullAny",
                "com.jsoniter.any.LongLazyAny",
                "com.jsoniter.any.DoubleLazyAny",
                "com.jsoniter.any.ObjectLazyAny",
                "com.jsoniter.any.StringAny",
                "com.jsoniter.any.StringLazyAny",
                "com.jsoniter.any.ArrayAny",
                "com.jsoniter.any.ObjectAny",
                "com.jsoniter.any.ListWrapperAny",
                "com.jsoniter.any.ArrayWrapperAny",
                "com.jsoniter.any.MapWrapperAny"
        );
    }

    private static class JsoniterAnySerializerHolder {
        private static final StdSerializer<com.jsoniter.any.Any> SERIALIZER = new StdSerializer<>(com.jsoniter.any.Any.class) {
            @Override
            public void serialize(Any value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
                gen.writeRawValue(com.jsoniter.output.JsonStream.serialize(value));
            }
        };
    }

    /**
     * Constructs the new instance of {@link Jackson3JsoniterModule}.
     */
    public Jackson3JsoniterModule() {
        super("jsoniter");
        if (isJsoniterAvailable()) {
            for (var anyClass : anyClasses()) {
                try {
                    @SuppressWarnings("unchecked")
                    var clazz = (Class<? extends com.jsoniter.any.Any>) Class.forName(anyClass);
                    addSerializer(clazz, JsoniterAnySerializerHolder.SERIALIZER);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
        }
    }

}
