package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.WlSolution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.Instant;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "v_worklog_summary")
public class WorklogSummary extends AuditableEntity {

    @Id
    private String id;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ag_status")
    private AgStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tc_take_status")
    private TcStatus takeStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "tc_close_status")
    private TcStatus closeStatus;

    @Column(name = "ws_created_at")
    private Instant wsCreatedAt;
    @Column(name = "ws_updated_at")
    private Instant wsUpdatedAt;

    private WlSolution solution;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

}
