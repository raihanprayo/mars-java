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
public class Account extends AuditableEntity implements AuthenticatedPrincipal, UserDetails {

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

    @Column
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column(name = "sto")
    private String sto;

    @Column
    private boolean active;

//    @Column(name = "password")
//    private String password;

    @Embedded
    @Builder.Default
    private AccountTg tg = new AccountTg();

    @ToString.Exclude
    @Builder.Default
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private AccountSetting setting = new AccountSetting();

    @Builder.Default
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AccountCredential> credentials = new LinkedHashSet<>();

    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_roles",
            schema = "mars",
            joinColumns = @JoinColumn(name = "ref_user_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(isActive(), account.isActive())
                .append(getId(), account.getId())
                .append(getName(), account.getName())
                .append(getNik(), account.getNik())
                .append(getPhone(), account.getPhone())
                .append(getWitel(), account.getWitel())
                .append(getSto(), account.getSto())
                .append(getEmail(), account.getEmail())
                .append(getPassword(), account.getPassword())
                .append(getTg(), account.getTg())
                .append(getRoles(), account.getRoles())
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
                .append(getRoles())
                .toHashCode();
    }

    @PreUpdate
    @PrePersist
    protected void prePersist() {
        if (this.setting.getAccount() == null)
            this.setting.setAccount(this);
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public AccountCredential getCredential() {
        if (credentials == null || credentials.size() == 0) return null;
        return new ArrayList<>(credentials).get(0);
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return Optional.ofNullable(getCredential())
                .map(AccountCredential::format)
                .orElse(null);
    }

    @Override
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

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive();
    }

    public void addRoles(Role... roles) {
        this.roles.addAll(List.of(roles));
    }

    public boolean hasAnyRole(String... predicate) {
        List<String> roleList = List.of(predicate);
        return roles.stream().anyMatch(r -> roleList.contains(r.getName()));
    }

}
