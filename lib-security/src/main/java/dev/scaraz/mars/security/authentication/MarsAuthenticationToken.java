package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import dev.scaraz.mars.security.constants.AccessType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public abstract class MarsAuthenticationToken<T extends MarsAuthentication> extends AbstractAuthenticationToken {

    @Getter
    private final AccessType access;

    private final T authentication;

    public MarsAuthenticationToken(
            AccessType access,
            T authentication
    ) {
        super(authentication.getAuthorities());
        this.access = access;
        this.authentication = authentication;
        super.setAuthenticated(getAuthorities().size() > 0);
    }

    @Override
    public T getPrincipal() {
        return authentication;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}
