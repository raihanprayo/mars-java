package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_log_ticket")
public class LogTicket extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "ref_ticket_id")
    private Ticket ticket;

}
