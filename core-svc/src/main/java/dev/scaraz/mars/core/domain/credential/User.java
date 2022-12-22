package dev.scaraz.mars.core.domain.credential;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.AuthenticatedPrincipal;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_user")
public class User extends AuditableEntity implements AuthenticatedPrincipal {

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
    @JsonProperty(namespace = "tgId")
    private long telegramId;

    @Column
    private boolean active;

    @ManyToOne
    @JoinTable(name = "t_group_members",
            joinColumns = @JoinColumn(name = "ref_group_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_user_id"))
    private Group group;

    @Builder.Default
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserCredential credential = new UserCredential();

    @Builder.Default
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserSetting setting = new UserSetting();

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "t_user_roles",
            joinColumns = @JoinColumn(name = "ref_role_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_user_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }
    public void addRoles(Role... roles) {
        this.roles.addAll(Set.of(roles));
    }

    public boolean hasRole(String name) {
        return roles.stream().anyMatch(e -> !e.isGroupRole() && e.getName().equalsIgnoreCase(name));
    }

    public boolean hasRole(Role role) {
        return roles.stream().anyMatch(r -> !r.isGroupRole() && r.equals(role));
    }

    public boolean hasGroupRole(String name) {
        return roles.stream().anyMatch(e -> e.isGroupRole() && e.getName().equalsIgnoreCase(name));
    }

    public boolean hasGroupRole(Role role) {
        return roles.stream().anyMatch(r -> r.isGroupRole() && r.equals(role));
    }

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
    }

}
