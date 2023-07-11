package dev.scaraz.mars.security.authentication.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

public class MarsLoginAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final boolean confirmed;

    public MarsLoginAuthenticationToken(String principal, String credential, boolean confirmed) {
        super(principal, credential);
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
