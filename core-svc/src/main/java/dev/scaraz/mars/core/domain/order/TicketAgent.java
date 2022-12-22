package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket_agent")
public class TicketAgent extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    @Enumerated(EnumType.STRING)
    private AgStatus status;

    @Column(name = "tc_close_status")
    @Enumerated(EnumType.STRING)
    private TcStatus closeStatus;

    @ManyToOne
    @JoinColumn(name = "ref_ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "ref_user_id")
    private User user;

    @Column
    private String description;

    @Column
    private String reopenDescription;

}
