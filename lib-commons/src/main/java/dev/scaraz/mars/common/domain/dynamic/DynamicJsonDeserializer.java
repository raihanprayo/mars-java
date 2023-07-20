package dev.scaraz.mars.common.domain.dynamic;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class DynamicJsonDeserializer extends JsonDeserializer<DynamicType> {
    @Override
    public DynamicType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            TreeNode node = parser.getCodec().readTree(parser);
            TextNode classField = (TextNode) node.get("class");

            try {
                return DynamicType.of(getClass()
                        .getClassLoader()
                        .loadClass(classField.asText()));
            }
            catch (ClassNotFoundException e) {
                throw InvalidFormatException.from(
                        parser,
                        "Invalid class type: " + e.getMessage()
                );
            }
        }

        throw InvalidFormatException.from(
                parser,
                "Invalid start token",
                parser.getValueAsString(),
                DynamicType.class
        );
    }
}
