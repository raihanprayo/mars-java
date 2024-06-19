package dev.scaraz.mars.common.tools.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Instant;

public class StringToInstantConverter implements Converter<String, Instant> {

    @Override
    public Instant convert(String source) {
        if (source == null) return null;
        return Instant.from(InstantSerializer.PATTERN.parse(source));
    }

}
