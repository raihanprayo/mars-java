package dev.scaraz.mars.core.util;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class DelegateUser implements UserDetails {

    @Getter
    private final String id;

    private final String nik;

    private final String username;
    private final Collection<Role> authorities;
    private final boolean active;

    private final String password;

    public DelegateUser(Account account) {
        this.id = account.getId();
        this.nik = account.getNik();
        this.active = account.isActive();
        this.authorities = account.getRoles();
        this.password = account.getPassword();
        this.username = account.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
    public boolean isEnabled() {
        return active;
    }

    public String getNik() {
        return nik;
    }
}
