package dev.scaraz.mars.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class MarsUser extends AuditableEntity implements UserDetails {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    private String nik;

    @Column(name = "tg_id")
    private long telegram;

    private Witel witel;

    private String sto;

    private String phone;

    private String email;

    private String password;

    private boolean enabled;

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_roles",
            schema = "mars",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_role_id"))
    private List<Role> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return nik;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MarsUser)) return false;

        MarsUser marsUser = (MarsUser) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getTelegram(), marsUser.getTelegram())
                .append(isEnabled(), marsUser.isEnabled())
                .append(getId(), marsUser.getId())
                .append(getName(), marsUser.getName())
                .append(getNik(), marsUser.getNik())
                .append(getWitel(), marsUser.getWitel())
                .append(getSto(), marsUser.getSto())
                .append(getPhone(), marsUser.getPhone())
                .append(getEmail(), marsUser.getEmail())
                .append(getPassword(), marsUser.getPassword())
                .append(getRoles(), marsUser.getRoles())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getName())
                .append(getNik())
                .append(getTelegram())
                .append(getWitel())
                .append(getSto())
                .append(getPhone())
                .append(getEmail())
                .append(getPassword())
                .append(isEnabled())
                .append(getRoles())
                .toHashCode();
    }
}
