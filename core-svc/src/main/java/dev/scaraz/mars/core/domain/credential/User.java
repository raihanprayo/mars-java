package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user")
public class User extends AuditableEntity implements AuthenticatedPrincipal, UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    private String nik;

    @Column
    private String name;

    @Column
    private String phone;

    @Column(name = "email")
    private String email;

//    @Column(name = "tg_id")
//    private Long telegramId;

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column(name = "sto")
    private String sto;

    @Column
    private boolean active;

    @Column(name = "password")
    private String password;

    @Embedded
    @Builder.Default
    private UserTg tg = new UserTg();
//
//    @ToString.Exclude
//    @Builder.Default
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private UserCredential credential = new UserCredential();

    @ToString.Exclude
    @Builder.Default
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSetting setting = new UserSetting();

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_group_id")
    private Group group;

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_roles",
            schema = "mars",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_role_id"))
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof User)) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(isActive(), user.isActive())
                .append(getId(), user.getId())
                .append(getName(), user.getName())
                .append(getNik(), user.getNik())
                .append(getPhone(), user.getPhone())
                .append(getWitel(), user.getWitel())
                .append(getSto(), user.getSto())
                .append(getEmail(), user.getEmail())
                .append(getPassword(), user.getPassword())
                .append(getTg(), user.getTg())
                .append(getGroup(), user.getGroup())
                .append(getRoles(), user.getRoles())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getName())
                .append(getNik())
                .append(getPhone())
                .append(getWitel())
                .append(getSto())
                .append(isActive())
                .append(getEmail())
                .append(getPassword())
                .append(getTg())
                .append(getGroup())
                .append(getRoles())
                .toHashCode();
    }

    @PreUpdate
    @PrePersist
    protected void prePersist() {
        if (this.setting.getUser() == null)
            this.setting.setUser(this);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return getName();
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

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive();
    }
}
