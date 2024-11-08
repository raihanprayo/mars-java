package dev.scaraz.mars.core.domain.agent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.WlSolution;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_agent_worklog")
public class AgentWorklog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "tc_take_status")
    @Enumerated(EnumType.STRING)
    private TcStatus takeStatus;

    @Column(name = "tc_close_status")
    @Enumerated(EnumType.STRING)
    private TcStatus closeStatus;

    @Column
    private String message;

    @Column(name = "reopen_message", updatable = false)
    private String reopenMessage;

    private WlSolution solution;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ref_workspace_id")
    private AgentWorkspace workspace;

}
