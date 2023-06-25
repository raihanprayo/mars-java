package dev.scaraz.mars.core.v2.domain.credential;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.core.v2.domain.embed.AccountMisc;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_account")
public class Account extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "nik")
    private String username;

    @Column
    private String name;

    @Column(name = "active")
    private boolean enabled;

    @Builder.Default
    private AccountMisc misc = new AccountMisc();

    @Builder.Default
    @OrderBy("priority ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private Set<AccountCredential> credentials = new LinkedHashSet<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "account")
    private AccountExpired expired;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public AccountCredential getCredential() {
        if (credentials == null || credentials.size() == 0) return null;
        return new ArrayList<>(credentials).get(0);
    }

    @Override
    public String getPassword() {
        return Optional.ofNullable(getCredential())
                .map(AccountCredential::format)
                .orElse(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void replace(AccountExpired expired) {
        this.expired.setActive(expired.isActive());
        this.expired.setDate(expired.getDate());
    }

    @PrePersist
    protected void prePersist() {
        if (expired != null && expired.getAccount() == null)
            expired.setAccount(this);

        for (AccountCredential credential : credentials) {
            if (credential.getAccount() == null)
                credential.setAccount(this);
        }
    }

}
