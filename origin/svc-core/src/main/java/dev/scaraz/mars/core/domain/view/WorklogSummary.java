package dev.scaraz.mars.core.domain.view;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
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
public class WorklogSummary {

    @Id
    private String id;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "solution")
    private String solution;

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

    @Column(name = "wl_created_at")
    private Instant wlCreatedAt;
    @Column(name = "wl_updated_at")
    private Instant wlUpdatedAt;

}
