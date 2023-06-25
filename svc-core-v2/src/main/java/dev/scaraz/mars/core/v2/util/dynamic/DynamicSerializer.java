package dev.scaraz.mars.core.v2.util.dynamic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.scaraz.mars.core.v2.util.enums.DynamicType;

import java.io.IOException;

public class DynamicSerializer extends JsonSerializer<DynamicType> {

    @Override
    public void serialize(DynamicType type, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("class", type.getType().getCanonicalName());
        gen.writeStringField("enum", type.getType().getSimpleName().toUpperCase());
        gen.writeEndObject();
    }

//    public static boolean isPrimitive(DynamicType type) {
//        return type == DynamicType.STRING ||
//                type == DynamicType.CHAR ||
//                type == DynamicType.BOOLEAN ||
//                type == DynamicType.INTEGER ||
//                type == DynamicType.LONG ||
//                type == DynamicType.DOUBLE ||
//                type == DynamicType.SHORT ||
//                type == DynamicType.FLOAT;
//    }

}
