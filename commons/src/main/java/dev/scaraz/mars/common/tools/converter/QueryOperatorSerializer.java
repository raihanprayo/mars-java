package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.scaraz.mars.common.tools.enums.QueryOperator;

import java.io.IOException;

public class QueryOperatorSerializer extends JsonSerializer<QueryOperator> {

    @Override
    public void serialize(QueryOperator queryOperator, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String operator = queryOperator.getOperator();
        jsonGenerator.writeString(operator);
    }

}
