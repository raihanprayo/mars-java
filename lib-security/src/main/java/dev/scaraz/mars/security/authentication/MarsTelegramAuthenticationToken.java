package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.authentication.identity.MarsTelegramToken;
import dev.scaraz.mars.security.constants.AccessType;

public class MarsTelegramAuthenticationToken extends MarsAuthenticationToken<MarsTelegramToken> {

    public MarsTelegramAuthenticationToken(MarsTelegramToken authentication) {
        super(AccessType.TELEGRAM, authentication);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

}
