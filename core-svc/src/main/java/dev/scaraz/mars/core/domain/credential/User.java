package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    private String name;

    @Column
    private String nik;

    @Column
    private String phone;

    @Column(name = "tg_id")
    private Long telegramId;

    @Column
    private boolean active;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_group_id")
    private Group group;

    @ToString.Exclude
    @Builder.Default
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserCredential credential = new UserCredential();

    @ToString.Exclude
    @Builder.Default
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSetting setting = new UserSetting();

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_roles",
            schema = "mars",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_role_id"))
    private Set<Role> roles = new HashSet<>();

    public boolean canLogin() {
        return Optional.ofNullable(group)
                .map(g -> g.getSetting().canLogin())
                .orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof User)) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(getTelegramId(), user.getTelegramId())
                .append(getId(), user.getId())
                .append(getName(), user.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getName())
                .append(getTelegramId())
                .toHashCode();
    }

    @PreUpdate
    @PrePersist
    protected void prePersist() {
        if (this.setting.getUser() == null)
            this.setting.setUser(this);

        if (this.credential.getUser() == null)
            this.credential.setUser(this);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return Optional.ofNullable(getCredential())
                .map(UserCredential::getPassword)
                .orElse(null);
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
