package dev.scaraz.mars.security.authentication.identity;

import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public interface MarsAuthentication extends Principal {

    String getId();

    @Override
    String getName();

    Witel getWitel();

    String getSto();

    Long getTelegram();

    String getPhone();

    Collection<? extends GrantedAuthority> getAuthorities();

}
