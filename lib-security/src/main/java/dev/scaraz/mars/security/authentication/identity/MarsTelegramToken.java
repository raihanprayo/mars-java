package dev.scaraz.mars.security.authentication.identity;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsTelegramToken implements MarsAuthentication {

    private String id;
    private String name;
    private Witel witel;
    private String sto;
    private String phone;
    private Long telegram;

    @Getter(AccessLevel.NONE)
    private Collection<? extends GrantedAuthority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
}
