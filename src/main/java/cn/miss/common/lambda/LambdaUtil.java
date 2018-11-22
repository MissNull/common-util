package cn.miss.common.lambda;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author 周林顺
 * @Description:
 * @Date: Created in 2018/4/24.
 */
public class LambdaUtil {

    /**
     * @param collection
     * @param keyMapper   T -> ID
     * @param valueMapper T -> V
     * @param <ID>        Map的key
     * @param <T>
     * @param <V>         Map的Value为List，泛型为V
     * @return
     */
    public static <ID, T, V> Map<ID, List<V>> groupList(Stream<T> collection, Function<T, ID> keyMapper, Function<T, V> valueMapper) {
        return collection.collect(Collectors.toMap(keyMapper, valueMapper.andThen(Lists::newArrayList), (v1, v2) -> {
            v1.addAll(v2);
            return v1;
        }));
    }


    public static <ID, T, V> Map<ID, List<V>> groupList(Collection<T> collection, Function<T, ID> keyMapper, Function<T, V> valueMapper) {
        return collection.stream().collect(Collectors.toMap(keyMapper, valueMapper.andThen(Lists::newArrayList), (v1, v2) -> {
            v1.addAll(v2);
            return v1;
        }));
    }

    public static <ID, T> Map<ID, List<T>> groupList(Stream<T> collection, Function<T, ID> keyMapper) {
        return groupList(collection, keyMapper, t -> t);
    }

    public static <ID, T> Map<ID, List<T>> groupList(Collection<T> collection, Function<T, ID> keyMapper) {
        return groupList(collection, keyMapper, t -> t);
    }


    /**
     * 根据传入的keyMapper对collection分类 value默认为当前元素 对于重复的key会默认保留最后面一个value
     *
     * @param collection
     * @param keyMapper
     * @param <ID>
     * @param <T>
     * @return
     */

    public static <ID, T> Map<ID, T> groupBySingle(Collection<T> collection, Function<T, ID> keyMapper) {
        return collection.stream().collect(toMap(keyMapper));
    }

    public static <ID, T, F> Map<ID, F> groupBySingle(Collection<T> collection, Function<T, ID> keyMapper, Function<T, F> valueMapper) {
        return collection.stream().collect(toMap(keyMapper, valueMapper));
    }

    public static <ID, T> Map<ID, T> groupBySingle(Stream<T> stream, Function<T, ID> keyMapper) {
        return stream.collect(toMap(keyMapper));
    }

    public static <ID, T, F> Map<ID, F> groupBySingle(Stream<T> stream, Function<T, ID> keyMapper, Function<T, F> valueMapper) {
        return stream.collect(toMap(keyMapper, valueMapper));
    }

    public static <ID, T> Collector<T, ?, Map<ID, T>> toMap(Function<T, ID> keyMapper) {
        return toMap(keyMapper, c -> c);
    }

    public static <ID, T, F> Collector<T, ?, Map<ID, F>> toMap(Function<T, ID> keyMapper, Function<T, F> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (f, f2) -> f2);
    }

    public static <T, R> List<R> mapAndCollect(Collection<T> list, Function<T, R> map) {
        return list.stream().map(map).collect(Collectors.toList());
    }

    public static <T, R> List<R> mapAndCollect(T[] list, Function<T, R> map) {
        return Arrays.stream(list).map(map).collect(Collectors.toList());
    }

}
