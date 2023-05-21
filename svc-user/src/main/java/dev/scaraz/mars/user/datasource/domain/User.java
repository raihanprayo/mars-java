package dev.scaraz.mars.user.datasource.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.user.datasource.embedded.UserInfo;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user")
public class User extends AuditableEntity implements UserDetails {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String nik;

    @Column
    private String password;

    @Column
    private String phone;

    @Column
    @Builder.Default
    private boolean enabled = false;

    @Embedded
    @Builder.Default
    private UserInfo info = new UserInfo();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    @JoinTable(
            name = "t_user_roles",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_role_id"))
    private Set<Role> authorities = new HashSet<>();

    @JsonIgnore
    public String getUsername() {
        return getNik();
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
