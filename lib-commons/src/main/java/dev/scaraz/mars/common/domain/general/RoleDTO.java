package dev.scaraz.mars.common.domain.general;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO extends AuditableDTO implements GrantedAuthority {

    private String id;
    private String name;
    private int order;

    @Override
    public String getAuthority() {
        return name;
    }

}
