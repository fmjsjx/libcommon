package com.github.fmjsjx.libcommon.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fmjsjx.libcommon.util.ReflectUtil;

import java.util.List;

/**
 * Class that registers capability of serializing jsoniter {@code Any} objects with the Jackson core.
 *
 * @author MJ Fang
 * @since 3.4.3
 */
public class JsoniterModule extends SimpleModule {

    private static final boolean jsoniterAvailable = ReflectUtil.hasClassForName("com.jsoniter.any.Any");

    /**
     * Returns {@code true} if jsoniter is available, {@code false} otherwise.
     *
     * @return {@code true} if jsoniter is available, {@code false} otherwise
     */
    public static final boolean isJsoniterAvailable() {
        return jsoniterAvailable;
    }

    private static final class InstanceHolder {
        private static final JsoniterModule INSTANCE = new JsoniterModule();
    }

    /**
     * Returns the singleton {@link JsoniterModule} instance.
     *
     * @return the singleton {@link JsoniterModule} instance
     */
    public static final JsoniterModule getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Constructs the new instance of {@link JsoniterModule}.
     */
    public JsoniterModule() {
        super("jsoniter");
        if (isJsoniterAvailable()) {
            var anyClasses = List.of(
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
            for (var className : anyClasses) {
                try {
                    @SuppressWarnings("unchecked")
                    var clazz = (Class<? extends com.jsoniter.any.Any>) Class.forName(className);
                    addSerializer(clazz, JsoniterAnySerializer.getInstance());
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            addSerializer(JsoniterAnySerializer.getInstance());
        }
    }


}
