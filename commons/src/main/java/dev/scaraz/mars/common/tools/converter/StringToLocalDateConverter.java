package dev.scaraz.mars.common.tools.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    public static final String[] DATE_FORMATS = {
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "dd/MM/yyyy"
    };

    @Override
    public LocalDate convert(String source) {
        for (String date_format : DATE_FORMATS) {
            try {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern(date_format));
            }
            catch (DateTimeParseException ex) {
            }
        }
        return null;
    }

}
