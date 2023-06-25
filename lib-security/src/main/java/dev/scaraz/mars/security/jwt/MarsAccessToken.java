package dev.scaraz.mars.security.jwt;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsAccessToken {

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

    private Date issuedAt;

    private Date expiredAt;

    @Builder.Default
    private List<? extends GrantedAuthority> roles = new ArrayList<>();

    public boolean isRefreshToken() {
        return StringUtils.isNoneBlank(aud) &&
                aud.equals(RFS);
    }

    public static MarsJwtClaimsBuilder refresh() {
        return builder().aud(RFS);
    }

    public static MarsJwtClaimsBuilder access() {
        return builder().aud(ACS);
    }

}
