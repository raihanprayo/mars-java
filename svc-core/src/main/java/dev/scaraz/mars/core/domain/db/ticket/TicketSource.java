package dev.scaraz.mars.core.domain.db.ticket;

import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TicketSource {

    @Column(name = "src_witel", updatable = false)
    @Enumerated(EnumType.STRING)
    private Witel witel;

    @Column(name = "src_sto", updatable = false)
    private String sto;

    @Column(name = "src_sender_name", updatable = false)
    private String senderName;

    @Column(name = "src_sender_tg_id", updatable = false)
    private long senderTgId;

    @Column(name = "src_chat", updatable = false)
    @Enumerated(EnumType.STRING)
    private TcSource source;

}
