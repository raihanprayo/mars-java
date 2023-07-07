package dev.scaraz.mars.core.v2.util.dynamic;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.TextNode;
import dev.scaraz.mars.core.v2.util.enums.DynamicType;

import java.io.IOException;

public class DynamicDeserializer extends JsonDeserializer<DynamicType> {
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
