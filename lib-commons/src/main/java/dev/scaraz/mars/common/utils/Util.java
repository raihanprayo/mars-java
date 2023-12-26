package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.Translator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
    private Util() {
    }

    public static String enumToString(String delimiter, Class<? extends Enum<?>> type) {
        return String.join(
                delimiter,
                Stream.of(type.getEnumConstants())
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }

    public static boolean isStringNumber(String text) {
        try {
            Long.parseLong(text);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

    public static <T> T getLastItem(Iterable<T> container) {
        T t = null;
        if (container instanceof List) {
            List<T> items = (List<T>) container;
            t = items.get(items.size() - 1);
        }
        else {
            Iterator<T> itr = container.iterator();

            while (itr.hasNext()) {
                T item = itr.next();
                if (!itr.hasNext()) t = item;
            }
        }

        return t;
    }

    public static void createDirIfNotExist(Path path) {
        File file = path.toFile();
        if (!file.exists()) file.mkdirs();
    }

    private static final Pattern DURATION_PATTERN =
            Pattern.compile("([-+]?)P(?:(?<day>[-+]?[0-9]+)D)?" +
                            "(T(?:(?<hour>[-+]?[0-9]+)H)?(?:(?<minute>[-+]?[0-9]+)M)?(?<second>(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9})))?S)?)?",
                    Pattern.CASE_INSENSITIVE);

    public static String durationDescribe(Duration duration) {
        String drt = duration.toString();
        Matcher matcher = DURATION_PATTERN.matcher(drt);
        String[] segments = {
                matcher.group("day"),
                matcher.group("hour"),
                matcher.group("minute"),
                matcher.group("second")
        };
        String[] units = {"date.day", "date.hour", "date.minute", "date.second"};

        String[] converted = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            String value = segments[i];
            if (StringUtils.isNotBlank(value)) {
                String unit = Translator.tr(units[i]);
                converted[i] =
                        String.format("%s %s", value, unit);
            }
        }

        return Arrays.stream(converted).filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

}
