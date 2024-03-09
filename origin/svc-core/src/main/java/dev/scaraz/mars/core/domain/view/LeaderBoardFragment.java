package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.*;
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.time.Instant;

@Getter
@ToString

@Entity
@Table(name = "v_leader_board_fragment")
public class LeaderBoardFragment extends AuditableEntity {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "workspace_id")
    private long workspaceId;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Column(name = "take_status")
    @Enumerated(EnumType.STRING)
    private TcStatus start;

    @Column(name = "close_status")
    @Enumerated(EnumType.STRING)
    private TcStatus close;

    @Column(name = "action_duration")
    @Type(PostgreSQLIntervalType.class)
    private Duration actionDuration;

    @Column(name = "ticket_created_at")
    private Instant ticketCreatedAt;

    @Column(name = "ticket_updated_at")
    private Instant ticketUpdatedAt;


    private TcIssue issue;

    private WlSolution solution;

}
