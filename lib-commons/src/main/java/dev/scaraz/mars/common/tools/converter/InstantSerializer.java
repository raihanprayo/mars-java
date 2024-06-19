package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.scaraz.mars.common.utils.AppConstants;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantSerializer extends JsonSerializer<Instant> {

    static final DateTimeFormatter PATTERN = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(AppConstants.ZONE_LOCAL);

    public InstantSerializer() {
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str = PATTERN.format(value);
        gen.writeString(str);
    }

}
