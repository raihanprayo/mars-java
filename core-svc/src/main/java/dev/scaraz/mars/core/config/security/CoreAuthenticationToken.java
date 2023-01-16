package dev.scaraz.mars.core.config.security;

import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserCredential;
import dev.scaraz.mars.core.util.AuthSource;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class CoreAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final AuthSource source;
    private final DelegateUser principal;
    private final UserCredential credentials;

    public CoreAuthenticationToken(AuthSource source, User user) {
        super(user.getRoles());
        this.source = source;
        this.principal = new DelegateUser(user);

        UserCredential credential = user.getCredential();
        this.credentials = UserCredential.builder()
                .id(credential.getId())
                .email(credential.getEmail())
                .password(credential.getPassword())
                .build();

        if (user.isActive())
            super.setAuthenticated(true);
    }

    @Override
    public UserCredential getCredentials() {
        return this.credentials;
    }

    @Override
    public DelegateUser getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}
