package me.bristermitten.mittenlib.config;

import com.google.gson.reflect.TypeToken;
import me.bristermitten.mittenlib.util.Result;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility for deserializing collections using the MittenLib config system.
 * This is used by generated config classes.
 */
public class CollectionsUtils {
    private static final TypeToken<List<Map<String, Object>>> LIST_MAP_STRING_OBJECT_TOKEN = new TypeToken<List<Map<String, Object>>>() {
    };
    private static final TypeToken<Map<String, Object>> MAP_STRING_OBJECT_TOKEN = new TypeToken<Map<String, Object>>() {
    };


    private CollectionsUtils() {

    }

    /**
     * Attempt to deserialize a list using the MittenLib config system.
     *
     * @param rawData                 the raw data to deserialize, which should represent a {@code List<Map<String, Object>>}
     * @param baseContext             the base context to use for deserialization
     * @param deserializationFunction the function to use for turning a {@link Map} into an {@link T}
     * @param <T>                     the type to deserialize to
     * @return a {@link Result} containing the deserialized list, or a {@link Result#fail(Exception)} if deserialization failed
     */
    public static <T> Result<List<T>> deserializeList(Object rawData, DeserializationContext baseContext, DeserializationFunction<T> deserializationFunction) {
        Result<List<Map<String, Object>>> rawListRes = baseContext.getMapper().map(rawData, LIST_MAP_STRING_OBJECT_TOKEN);
        return rawListRes.flatMap(rawList -> {
            // Apply the deserialization function to each element of the list, flattening the result
            Result<List<T>> res = Result.ok(new ArrayList<>());
            for (Map<String, Object> map : rawList) {
                res = res.flatMap(list -> deserializationFunction.apply(baseContext.withData(map))
                        .map(t -> {
                            list.add(t);
                            return list;
                        }));
            }
            return res;
        });

    }

    /**
     * Attempt to deserialize a map using the MittenLib config system.
     *
     * @param keyType                 the type of the keys in the map
     * @param rawData                 the raw data to deserialize, which should represent a {@code Map<String, Object>}
     * @param baseContext             the base context to use for deserialization
     * @param deserializationFunction the function to use for turning a {@link Map} into an {@link V}
     * @param <K>                     the type of the keys in the map
     * @param <V>                     the type to deserialize to
     * @return a {@link Result} containing the deserialized map, or a {@link Result#fail(Exception)} if deserialization failed
     */
    public static <K, V> Result<Map<K, V>> deserializeMap(Class<K> keyType, Object rawData, DeserializationContext baseContext, DeserializationFunction<V> deserializationFunction) {
        //noinspection unchecked absolutely evil
        Result<Map<K, Map<String, Object>>> rawMapRes = baseContext.getMapper().map(rawData,
                (TypeToken<Map<K, Map<String, Object>>>) TypeToken.get(
                        new IDKParameterizedType(Map.class, keyType, MAP_STRING_OBJECT_TOKEN.getType())
                ));
        return rawMapRes.flatMap(rawMap -> {
            // Apply the deserialization function to each value in the map, flattening the result
            Result<Map<K, V>> res = Result.ok(new HashMap<>());
            for (Map.Entry<K, Map<String, Object>> entry : rawMap.entrySet()) {
                res = res.flatMap(map -> deserializationFunction.apply(baseContext.withData(entry.getValue()))
                        .map(val -> {
                            map.put(entry.getKey(), val);
                            return map;
                        }));
            }
            return res;
        });
    }

    static class IDKParameterizedType implements ParameterizedType {
        private final Class<?> container;
        private final Type[] wrapped;

        @Contract(pure = true)
        public IDKParameterizedType(Class<?> container, Type... wrapped) {
            this.container = container;
            this.wrapped = wrapped;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return this.wrapped;
        }

        @Override
        public Type getRawType() {
            return this.container;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
