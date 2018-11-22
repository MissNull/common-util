package cn.miss.common;

import cn.miss.common.lambda.LambdaUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2018/9/21.
 */
public class DateUtilNew {
    public static final ZoneId SP = ZoneId.of("Asia/Shanghai");

    //2011-12-03T10:15:30
    private static final String default_formatter_str = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter default_formatter = DateTimeFormatter.ofPattern(default_formatter_str);

    private static final List<DateTimeFormatter> formatters;
    private static final Map<Integer, List<String>> lengthPatterns = new HashMap<>();


    static {
        List<String> years = Arrays.asList("", "yyyy");
        List<String> months = Arrays.asList("", "MM", "M");
        List<String> days = Arrays.asList("", "dd", "d");
        List<String> prefixSplits = Arrays.asList("-", "", "/");
        List<String> midSplits = Arrays.asList("", " ");
        List<String> hours = Arrays.asList("", "H", "HH");
        List<String> mins = Arrays.asList("", "m", "mm");
        List<String> seconds = Arrays.asList("", "s", "ss");
        List<String> millisecondSplits = Arrays.asList("", " ", ".");
        List<String> millisecond = Arrays.asList("", "S", "SS");
        List<String> suffixSplits = Arrays.asList("", ":");
        Stream<String> concat = concat(years, prefixSplits, months, prefixSplits, days, midSplits, hours, suffixSplits, mins, suffixSplits, seconds, millisecondSplits, millisecond);

        Stream<String> unStreams = concat(prefixSplits, midSplits, suffixSplits, millisecondSplits);
        List<String> unStr = unStreams.filter(StringUtils::isNotBlank).filter(s -> s.length() > 1).collect(Collectors.toList());
        unStr.add("//");
        unStr.add("--");
        unStr.add("::");
        unStr.add("-/");
        unStr.add("/-");

        List<Predicate<String>> filter = filter(Arrays.asList("-", " ", "/", ":", "."), unStr);
        Stream<String> clean = clean(concat, filter);
        lengthPatterns.putAll(LambdaUtil.groupList(clean, String::length));


        Field[] fields = DateTimeFormatter.class.getFields();
        formatters = new LinkedList<>();
        for (Field field : fields) {
            if (field.getType() == DateTimeFormatter.class) {
                try {
                    formatters.add((DateTimeFormatter) field.get(null));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static Stream<String> concat(List<String>... lists) {
        if (lists == null || lists.length == 0) {
            return Stream.empty();
        }
        if (lists.length == 1) {
            return Stream.empty();
        }
        List<String> begin = lists[0];
        Stream<String> stringStream = begin.stream();
        for (int i = 1; i < lists.length; i++) {
            List<String> next = lists[i];
            stringStream = stringStream.flatMap(s -> mapper(s, next));
        }
        return stringStream;
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
        return results;
    }

    public static Stream<String> mapper(String s, Iterable<String> next) {
        return StreamSupport.stream(next.spliterator(), true).map(d -> {
            return s + d;
        });
    }


    public static LocalDateTime parse(String str) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return parse(str, formatter);
            } catch (Exception ignored) {
            }
        }
        List<String> patterns = lengthPatterns.get(str.length());
        for (String pattern : patterns) {
            try {
                return parse(str, pattern);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("未知的时间日期格式");
    }

    public static LocalDateTime parse(String str, String pattern) {
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String str, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(str, formatter);
        } catch (Exception e) {
            return LocalDateTime.of(LocalDate.parse(str, formatter), LocalTime.of(0, 0, 0));
        }
    }

    public static Date parseToDate(String str) {
        return parseToDate(str, ZoneId.systemDefault());
    }

    public static Date parseToDate(String str, ZoneId zoneId) {
        LocalDateTime parse = parse(str);
        Instant instant = parse.atZone(zoneId).toInstant();
        return Date.from(instant);
    }


    public static Date parseToDate(String str, String pattern) {
        return parseToDate(str, pattern, ZoneId.systemDefault());
    }

    public static Date parseToDate(String str, String pattern, ZoneId zoneId) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId);
        ZonedDateTime parse = ZonedDateTime.parse(str, dateTimeFormatter);
        return Date.from(parse.toInstant());
    }

    public static String format(Date date) {
        return format(date, default_formatter_str);
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, ZoneId.systemDefault());
    }

    public static String format(Date date, ZoneId zoneId) {
        return format(date, default_formatter_str, zoneId);
    }

    public static String format(Date date, String pattern, ZoneId zoneId) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }


    public static void main(String[] args) {
        LocalDateTime parse = parse("2018-10-11 11:01:10");
        System.out.println(parse);

    }

}
