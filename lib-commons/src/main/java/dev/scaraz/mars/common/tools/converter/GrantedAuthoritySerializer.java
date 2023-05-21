package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;

public class GrantedAuthoritySerializer extends JsonSerializer<Collection<GrantedAuthority>> {
    @Override
    public void serialize(Collection<GrantedAuthority> grantedAuthority,
                          JsonGenerator gen,
                          SerializerProvider serializer
    ) throws IOException {
        String[] authorities = grantedAuthority.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        gen.writeArray(authorities, 0, authorities.length);
    }
}
