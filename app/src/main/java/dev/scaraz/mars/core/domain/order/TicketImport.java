package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)

@Entity
@Table(name = "t_ticket_import")
public class TicketImport extends TimestampCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_ticket_id")
    private Ticket ticket;

    @Column(name = "witel")
    private Witel witel;

    @Column(name = "ref_external_ticket_id")
    private String externalId;

}
