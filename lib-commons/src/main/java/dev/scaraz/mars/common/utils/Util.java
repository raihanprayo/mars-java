package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.Translator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
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
            Pattern.compile("P(?<day>[+-]?[0-9]D)?(T((?<hour>[+-]?[0-9]+)H)?((?<minute>[+-]?[0-9]+)M)((?<second>[+-]?[0-9]+([,.][0-9]+)?)S)?)?",
                    Pattern.CASE_INSENSITIVE);

    public static String durationDescribe(Duration duration) {

        String text = duration.toString();
        Matcher matcher = DURATION_PATTERN.matcher(text);


        String[] units = {"date.day", "date.hour", "date.minute", "date.second"};
        String[] converted = new String[units.length];

        BiFunction<String, Integer, String> formatter = (s, i) ->
                converted[i] = String.format("%s %s",
                        (long) Double.parseDouble(s.replaceAll("[a-zA-Z]", "")),
                        Translator.tr(units[i])
                );

        for (String s : text.replaceAll("[PTpt]", " ").trim().replaceAll("([HMShms])", "$1 ").trim().split(" ")) {
            if (StringUtils.isBlank(s)) continue;

            s = s.toUpperCase();
            if (s.endsWith("D"))
                formatter.apply(s, 0);
            else if (s.endsWith("H"))
                formatter.apply(s, 1);
            else if (s.endsWith("M"))
                formatter.apply(s, 2);
            else if (s.endsWith("S"))
                formatter.apply(s, 3);
        }

        return Arrays.stream(converted)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    public static String durationDescribe3Segment(Duration duration) {

        String text = duration.toString();
        Matcher matcher = DURATION_PATTERN.matcher(text);

        String[] converted = new String[]{"00", "00", "00"};

        BiFunction<String, Integer, String> formatter = (s, i) ->
                converted[i] = String.format("%02d",
                        (long) Double.parseDouble(s.replaceAll("[a-zA-Z]", ""))
                );

        for (String s : text.replaceAll("[PTpt]", " ").trim().replaceAll("([HMShms])", "$1 ").trim().split(" ")) {
            if (StringUtils.isBlank(s)) continue;

            s = s.toUpperCase();
//            if (s.endsWith("D"))
//                formatter.apply(s, 0);
            if (s.endsWith("H"))
                formatter.apply(s, 0);
            else if (s.endsWith("M"))
                formatter.apply(s, 1);
            else if (s.endsWith("S"))
                formatter.apply(s, 2);
        }

        return Arrays.stream(converted)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(":"));
    }


    private static class DurationParser {

        /**
         * Hours per day.
         */
        static final int HOURS_PER_DAY = 24;
        /**
         * Minutes per hour.
         */
        static final int MINUTES_PER_HOUR = 60;
        /**
         * Minutes per day.
         */
        static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
        /**
         * Seconds per minute.
         */
        static final int SECONDS_PER_MINUTE = 60;
        /**
         * Seconds per hour.
         */
        static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
        /**
         * Seconds per day.
         */
        static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
        /**
         * Milliseconds per day.
         */
        static final long MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L;
        /**
         * Microseconds per day.
         */
        static final long MICROS_PER_DAY = SECONDS_PER_DAY * 1000_000L;
        /**
         * Nanos per millisecond.
         */
        static final long NANOS_PER_MILLI = 1000_000L;
        /**
         * Nanos per second.
         */
        static final long NANOS_PER_SECOND = 1000_000_000L;
        /**
         * Nanos per minute.
         */
        static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
        /**
         * Nanos per hour.
         */
        static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
        /**
         * Nanos per day.
         */
        static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;

        private static boolean charMatch(CharSequence text, int start, int end, char c) {
            return (start >= 0 && end == start + 1 && text.charAt(start) == c);
        }

        private static long parseNumber(CharSequence text, int start, int end, int multiplier, String errorText) {
            // regex limits to [-+]?[0-9]+
            if (start < 0 || end < 0) {
                return 0;
            }
            try {
                long val = Long.parseLong(text, start, end, 10);
                return Math.multiplyExact(val, multiplier);
            }
            catch (NumberFormatException | ArithmeticException ex) {
                throw (DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex);
            }
        }

        private static int parseFraction(CharSequence text, int start, int end, int negate) {
            // regex limits to [0-9]{0,9}
            if (start < 0 || end < 0 || end - start == 0) {
                return 0;
            }
            try {
                int fraction = Integer.parseInt(text, start, end, 10);

                // for number strings smaller than 9 digits, interpret as if there
                // were trailing zeros
                for (int i = end - start; i < 9; i++) {
                    fraction *= 10;
                }
                return fraction * negate;
            }
            catch (NumberFormatException | ArithmeticException ex) {
                throw (DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: fraction", text, 0).initCause(ex);
            }
        }
    }
}
