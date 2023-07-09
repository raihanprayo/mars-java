package dev.scaraz.mars.security.authentication.identity;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsWebToken implements MarsAuthentication {
    public static final String
        ISSUER_WEB = "web",
        ISSUER_API = "api";

    public static final String ACS = "acs";
    public static final String RFS = "rfs";

    private String sub;

    private String aud;

    /**
     * <code>web</code> or <code>api</code> or <code>telegram</code>
     */
    private String iss;

    private String nik;

    private Long telegram;

    private Witel witel;

    private String sto;

    private String phone;

    private Date issuedAt;

    private Date expiredAt;

    @Builder.Default
    private Collection<? extends GrantedAuthority> roles = new ArrayList<>();

    public boolean isRefreshToken() {
        return StringUtils.isNoneBlank(aud) &&
                aud.equals(RFS);
    }

    public static MarsWebTokenBuilder refresh() {
        return builder().aud(RFS);
    }

    public static MarsWebTokenBuilder access() {
        return builder().aud(ACS);
    }


    @Override
    public String getId() {
        return sub;
    }

    @Override
    public String getName() {
        return nik;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }
}
