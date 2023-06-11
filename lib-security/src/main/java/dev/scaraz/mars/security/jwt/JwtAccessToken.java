package dev.scaraz.mars.security.jwt;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAccessToken implements Serializable {

    private String aud;
    @Builder.Default
    private boolean refreshToken = false;

    private String subject;

    private String nik;

    private Witel witel;

    private String sto;

    private Long telegram;

    private Date expiredAt;

    private Date issuedAt;

    @Builder.Default
    private List<? extends GrantedAuthority> roles = new ArrayList<>();

}
