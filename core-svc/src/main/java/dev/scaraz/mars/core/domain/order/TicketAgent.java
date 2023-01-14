package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ref_user_id")
    private User user;

    @Column
    private String description;

    @Column(updatable = false)
    private String reopenDescription;

    @OneToOne(mappedBy = "agent")
    private TicketAsset assets;
}
