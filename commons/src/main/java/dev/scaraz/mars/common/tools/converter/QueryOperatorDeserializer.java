package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.scaraz.mars.common.tools.enums.QueryOperator;

import java.io.IOException;

public class QueryOperatorDeserializer extends JsonDeserializer<QueryOperator> {

    @Override
    public QueryOperator deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return QueryOperator.byOperator(jsonParser.getText());
    }
}
