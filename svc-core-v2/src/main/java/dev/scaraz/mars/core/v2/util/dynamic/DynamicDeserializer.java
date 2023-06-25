package dev.scaraz.mars.core.v2.util.dynamic;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.scaraz.mars.core.v2.util.enums.DynamicType;

import java.io.IOException;

public class DynamicDeserializer extends JsonDeserializer<DynamicType> {

//    @Override
//    public void serialize(DynamicType type, JsonGenerator gen, SerializerProvider provider) throws IOException {
//        gen.writeStartObject();
//        gen.writeStringField("class", type.getType().getCanonicalName());
//        gen.writeStringField("enum", type.getType().getSimpleName().toUpperCase());
//        gen.writeEndObject();
//    }

    @Override
    public DynamicType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            TreeNode node = parser.getCodec().readTree(parser);
            TextNode classField = (TextNode) node.get("class");

            return DynamicType.from(classField.asText());
        }

        throw InvalidFormatException.from(
                parser,
                "Invalid start token",
                parser.getValueAsString(),
                DynamicType.class
        );
    }
}
