package dev.scaraz.mars.common.domain.response;

import lombok.*;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.Instant;
import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken implements AuthenticatedPrincipal {

    private String id;

    private String audience;

    private boolean refresher;

    private String userId;

    private Instant expiredAt;

    private Instant issuedAt;

    private String name;

    private long telegram;

    private JwtGroupToken group;

    @Builder.Default
    private List<String> roles = new ArrayList<>();


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtGroupToken {
        private String id;
        private String name;
    }

}
