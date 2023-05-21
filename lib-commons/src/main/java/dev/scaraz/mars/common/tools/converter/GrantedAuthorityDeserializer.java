package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GrantedAuthorityDeserializer extends JsonDeserializer<Set<GrantedAuthority>> {


    @Override
    public Set<GrantedAuthority> deserialize(JsonParser jsonParser,
                                              DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        if (jsonParser.isExpectedStartArrayToken()) {
            List<String> list = jsonParser.readValueAs(new TypeReference<List<String>>() {
            });

            return list.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
