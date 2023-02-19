package dev.scaraz.mars.core.domain.order;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_agent_workspace")
public class AgentWorkspace extends TimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private AgStatus status;

    @ManyToOne
    @JoinColumn(name = "ref_ticket_id", updatable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "ref_agent_id", updatable = false)
    private Agent agent;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "workspace", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AgentWorklog> worklogs = new HashSet<>();

    public Optional<AgentWorklog> getLastWorklog() {
        if (worklogs.isEmpty()) return Optional.empty();

        int i = 0;
        int last = worklogs.size() - 1;
        for (AgentWorklog worklog : worklogs) {
            if (i == last) return Optional.of(worklog);
            i++;
        }
        return Optional.empty();
    }

}
