package dev.scaraz.mars.security.authentication.identity;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsSessionToken implements MarsAuthentication {
    private String id;
    private String name;
    private String username;
    private Witel witel;
    private String sto;
    private String phone;
    private Long telegram;
    @Builder.Default
    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
}
