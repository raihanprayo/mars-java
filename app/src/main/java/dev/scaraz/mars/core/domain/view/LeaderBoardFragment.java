package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.TcIssue;
import dev.scaraz.mars.core.domain.order.WlSolution;
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
public class  LeaderBoardFragment extends AuditableEntity {

    @Id
    private long id;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "workspace_id")
    private long workspaceId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "take_status")
    @Enumerated(EnumType.STRING)
    private TcStatus start;

    @Column(name = "close_status")
    @Enumerated(EnumType.STRING)
    private TcStatus close;

    @Column(name = "drt_action")
    @Type(PostgreSQLIntervalType.class)
    private Duration actionDuration;

    @Column(name = "drt_response")
    @Type(PostgreSQLIntervalType.class)
    private Duration responseDuration;

    private TcIssue issue;

    private WlSolution solution;

    @Column(name = "last_log_at")
    private Instant lastTicketLogAt;

}
