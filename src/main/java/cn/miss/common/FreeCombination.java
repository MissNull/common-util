package cn.miss.common;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in {new Date()}
 */
public class FreeCombination {

    public static <T_IN> Stream<T_IN> combination(BiFunction<T_IN, T_IN, T_IN> concat, Collection<T_IN>... lists) {
        if (lists == null || lists.length == 0) {
            return Stream.empty();
        }
        if (lists.length == 1) {
            return mapper(null, lists[0], concat);
        }
        Stream<T_IN> stringStream = lists[0].stream();
        for (int i = 1; i < lists.length; i++) {
            Iterable<T_IN> next = lists[i];
            stringStream = stringStream.flatMap(s -> mapper(s, next, concat));
        }
        return stringStream;
    }

    public static <T_IN, T_OUT> Stream<T_OUT> mapper(T_IN s, Iterable<T_IN> next, BiFunction<T_IN, T_IN, T_OUT> concat) {
        return StreamSupport.stream(next.spliterator(), true).map(d -> concat.apply(s, d));
    }

    public static Stream<String> clean(Stream<String> stringStream, List<Predicate<String>> predicates) {
        if (predicates != null) {
            for (Predicate<String> predicate : predicates) {
                stringStream = stringStream.filter(predicate);
            }
        }
        return stringStream;
    }

    public static List<Predicate<String>> filter(List<String> un, List<String> unStr) {
        List<Predicate<String>> results = new ArrayList<>();
        if (un != null) {
            for (String s : un) {
                if (s.length() > 0) {
                    results.add((str) -> !str.startsWith(s));
                    results.add((str) -> !str.endsWith(s));
                }
            }
        }
        if (unStr != null) {
            for (String s : unStr) {
                if (s.length() > 0) {
                    results.add(str -> !str.contains(s));
                }
            }
        }
        results.add(str -> str.length() != 0);
        results.add(str -> str.endsWith(" ") || str.startsWith(" "));
        return results;
    }


    public static <T, D> BiFunction<T, D, String> strConcat() {
        return (str1, str2) -> {
            if (str1 == null) {
                return str2 == null ? "" : str2.toString();
            }
            return str2 == null ? str1.toString() : str1.toString().concat(str2.toString());
        };
    }

}
