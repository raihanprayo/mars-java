package dev.scaraz.mars.core.util;

import java.util.Collection;
import java.util.function.Predicate;
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

}
