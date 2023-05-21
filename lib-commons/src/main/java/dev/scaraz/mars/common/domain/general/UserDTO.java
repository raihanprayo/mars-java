package dev.scaraz.mars.common.domain.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends AuditableDTO implements UserDetails {

    private String id;

    private String nik;

    private String name;

    private String email;

    private String password;

    private String phone;

    private boolean enabled;

    @Builder.Default
    private Set<RoleDTO> authorities = new HashSet<>();

    @Builder.Default
    private UserInfoDTO info = new UserInfoDTO();

    @Override
    @JsonIgnore
    public String getUsername() {
        return nik;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
