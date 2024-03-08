package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_agent_worklog")
public class AgentWorklog extends TimestampEntity {

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
    private String solution;

    @Column
    private String message;

    @Column(name = "reopen_message", updatable = false)
    private String reopenMessage;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ref_workspace_id")
    private AgentWorkspace workspace;

}
