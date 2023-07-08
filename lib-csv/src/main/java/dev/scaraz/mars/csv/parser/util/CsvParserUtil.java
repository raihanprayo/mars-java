package dev.scaraz.mars.csv.parser.util;

import dev.scaraz.mars.csv.parser.validation.CsvValidation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CsvParserUtil {
    public static final String NUMBER_REGX = "^[0-9]$";

    private static boolean isBlank(String value) {
        return StringUtils.isBlank(value);
    }

    public static CsvValidation notNull(int line, int col, String value) {
        if (StringUtils.isBlank(value.trim()))
            return CsvValidation.error(line, col, "empty value");
        return CsvValidation.ok();
    }

    public static CsvValidation digits(int line, int col, boolean required, String value) {
        if (required) {
            CsvValidation validation = notNull(line, col, value);
            if (validation.isError()) return validation;
        }
        else if (isBlank(value)) return CsvValidation.ok();

        boolean matches = value.matches(NUMBER_REGX);
        if (matches) return CsvValidation.ok(value);
        return CsvValidation.error(line, col, "nilai seharusnya berupa deret angka");
    }

    public static <T extends Enum<T>> CsvValidation enumeration(int line, int col, boolean required, String value, Class<T> type) {
        CsvValidation validation;

        String names = Arrays.stream(type.getEnumConstants()).map(Enum::name)
                .collect(Collectors.joining("/"));
        String message = String.format("Nilai tidak sesuai, nilai seharusnya salah 1 dari %s", names);

        if (required) {
            validation = notNull(line, col, value);
            if (validation.isError()) return validation;
        }
        else if (StringUtils.isBlank(value)) return CsvValidation.ok();


        try {
            T t = Enum.valueOf(type, value.trim());
            return CsvValidation.ok(t);
        }
        catch (IllegalArgumentException e) {
            return CsvValidation.error(line, col, message);
        }
    }

    public static CsvValidation enumeration(int line, int col, boolean required, String value, Collection<String> enums) {
        CsvValidation validation;

        if (required) {
            validation = notNull(line, col, value);
            if (validation.isError()) return validation;
        }
        else if (StringUtils.isBlank(value)) return CsvValidation.ok();

        String names = enums.stream().collect(Collectors.joining("/"));
        String message = String.format("Nilai tidak sesuai, nilai seharusnya salah 1 dari %s", names);
        if (!enums.contains(value))
            return CsvValidation.error(line, col, message);
        return CsvValidation.ok(value);
    }

}
