package dev.scaraz.mars.core.domain.db.ticket;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_ticket")
public class Ticket extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String no;

    @Column(name = "incident_no", updatable = false)
    private String incidentNo;

    @Column(name = "service_no", updatable = false)
    private String serviceNo;

    @Column
    @Enumerated(EnumType.STRING)
    private TcStatus status;

    @Builder.Default
    @Column(updatable = false)
    private boolean gaul = false;

    @Column(name = "issue_product", updatable = false)
    @Enumerated(EnumType.STRING)
    private Product product;

    @Column(name = "issue_code", updatable = false)
    private String issue;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column
    @Builder.Default
    private TicketSource source = new TicketSource();

}
