package dev.scaraz.mars.core.v2.domain.embed;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AccountMisc {

    @Column(name = "witel")
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column(name = "sto")
    private String sto;

    @Column(name = "telegram")
    private Long telegram;

    @Column(name = "email")
    private String email;

}
