package cn.miss.common;

import cn.miss.common.lambda.LambdaUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.miss.common.FreeCombination.*;

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
        List<String> midSplits = Collections.singletonList(" ");
        List<String> hours = Arrays.asList("", "H", "HH");
        List<String> mins = Arrays.asList("", "m", "mm");
        List<String> seconds = Arrays.asList("", "s", "ss");
        List<String> millisecondSplits = Arrays.asList("", " ", ".");
        List<String> millisecond = Arrays.asList("", "S", "SS");
        List<String> suffixSplits = Arrays.asList("", ":");
        Stream<String> concat = combination(FreeCombination.strConcat(), years, prefixSplits, months, prefixSplits, days, midSplits, hours, suffixSplits, mins, suffixSplits, seconds, millisecondSplits, millisecond);

        Stream<String> unStreams = combination(String::concat, prefixSplits, midSplits, suffixSplits, millisecondSplits);
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


    public static LocalDateTime parse(String str) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDateTime parse = parse(str, formatter);
                System.out.println(formatter);
                return parse;
            } catch (Exception ignored) {
            }
        }
        List<String> patterns = lengthPatterns.get(str.length());
        for (String pattern : patterns) {
            try {
                Date parse = new SimpleDateFormat(pattern).parse(str);
                return LocalDateTime.ofInstant(parse.toInstant(),ZoneId.systemDefault());
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
        parse("2018-1-1");
        System.out.println(parse);
    }

}
