package dev.scaraz.mars.common.domain.general;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.GrantedAuthorityDeserializer;
import dev.scaraz.mars.common.tools.converter.GrantedAuthoritySerializer;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {

    private String token;
    private String id;
    private String subject;
    private String name;
    private String email;
    private String nik;
    private Long tg;

    @Builder.Default
    private AccessInfo info = new AccessInfo();

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private Date issued;

    private Date expired;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessInfo {
        private Witel witel;
        private String sto;
    }

    @Override
    public String toString() {
        return nik;
    }
}