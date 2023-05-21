package dev.scaraz.mars.user.datasource.embedded;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.user.datasource.domain.Sto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    @Column(name = "mars_witel")
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mars_sto")
    private Sto sto;

    @Builder.Default
    @Column(name = "mars_tg_id")
    private long tgId = -1;

    @Column(name = "mars_tg_username")
    private String tgUsername;

}
