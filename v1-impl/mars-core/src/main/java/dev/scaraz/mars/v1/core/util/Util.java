package dev.scaraz.mars.v1.core.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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

}
