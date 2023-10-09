package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.Ticket;
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;

@Getter
@ToString

@Entity
@Table(name = "v_leader_board_fragment")
@TypeDef(typeClass = PostgreSQLIntervalType.class,
        defaultForType = Duration.class)
public class LeaderBoardFragment {

    @Id
    private long id;

    @Column(name = "solution_id")
    private Long solution;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "workspace_id")
    private long workspaceId;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Column(name = "issue")
    private String issue;

    @Column(name = "product")
    @Enumerated(EnumType.STRING)
    private Product product;

    @Column(name = "take_status")
    @Enumerated(EnumType.STRING)
    private TcStatus start;

    @Column(name = "close_status")
    @Enumerated(EnumType.STRING)
    private TcStatus close;

    @Column(name = "action_duration",
            columnDefinition = "interval")
    private Duration actionDuration;

    @Column(name = "ticket_created_at")
    private Instant ticketCreatedAt;

    @Column(name = "ticket_updated_at")
    private Instant ticketUpdatedAt;

}
