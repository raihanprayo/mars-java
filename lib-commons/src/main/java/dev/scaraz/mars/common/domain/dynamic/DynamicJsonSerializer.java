package dev.scaraz.mars.common.domain.dynamic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DynamicJsonSerializer extends JsonSerializer<DynamicType> {

    @Override
    public void serialize(DynamicType type, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("class", type.getType().getCanonicalName());
        gen.writeStringField("enum", type.getType().getSimpleName().toUpperCase());
        gen.writeEndObject();
    }

}
