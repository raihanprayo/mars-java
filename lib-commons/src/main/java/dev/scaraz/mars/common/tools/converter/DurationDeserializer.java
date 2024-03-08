package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (jsonParser.isExpectedStartArrayToken()) {
            return read(jsonParser, deserializationContext);
        }
        return Duration.ZERO;
    }

    public Duration read(JsonParser parser, DeserializationContext context) throws IOException {
        StringBuilder result = new StringBuilder("P");
        parser.nextToken();

        int iterate = 0;
        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            JsonToken token = parser.getCurrentToken();

            if (!token.isNumeric()) throw MismatchedInputException.from(context, "invalid input expect a number type");

            switch (iterate) {
                case 0: {
                    double value = parser.getValueAsDouble();
                    result.append(value).append("D");
                }
                case 1: {
                    double value = parser.getValueAsDouble();
                    result.append(value).append("H");
                }
                case 2: {
                    double value = parser.getValueAsDouble();
                    result.append(value).append("M");
                }
                case 3: {
                    double value = parser.getValueAsDouble();
                    result.append(value).append("S");
                }
            }

            iterate += 1;
            parser.nextToken();
        }

        if (iterate != 3)
            throw InvalidDefinitionException.from(parser, "expected array length of 4 numbers");

        return Duration.parse(result.toString());
    }


}
