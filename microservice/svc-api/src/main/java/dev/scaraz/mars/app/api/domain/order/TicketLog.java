package dev.scaraz.mars.app.api.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket_log")
public class TicketLog extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column
    @Enumerated(EnumType.STRING)
    private TcStatus status;

    @Column
    private String message;

    @Column
    private String solutionId;

}
