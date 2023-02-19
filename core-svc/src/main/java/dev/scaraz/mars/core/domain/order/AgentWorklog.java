package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
    private Long solution;

    @Column
    private String message;

    @Column(name = "reopen_message", updatable = false)
    private String reopenMessage;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_workspace_id")
    private AgentWorkspace workspace;

}
