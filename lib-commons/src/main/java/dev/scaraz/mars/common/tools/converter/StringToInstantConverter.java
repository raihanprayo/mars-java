package dev.scaraz.mars.common.tools.converter;

import dev.scaraz.mars.common.utils.AppConstants;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.ZoneOffset;

public class StringToInstantConverter implements Converter<String, Instant> {

    @Override
    public Instant convert(String source) {
        return Instant.from(InstantSerializer.PATTERN.parse(source))
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(AppConstants.ZONE_LOCAL)
                .toInstant();
    }

}
