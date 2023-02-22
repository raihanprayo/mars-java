package dev.scaraz.mars.core.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import lombok.*;

import javax.persistence.*;
import java.util.*;

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
    @JsonIgnore
    @ToString.Exclude
    @JoinColumn(name = "ref_ticket_id", updatable = false)
    private Ticket ticket;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "ref_agent_id", updatable = false)
    private Agent agent;

    @ToString.Exclude
    @Builder.Default
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "workspace", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AgentWorklog> worklogs = new HashSet<>();

    @JsonIgnore
    public Optional<AgentWorklog> getLastWorklog() {
        if (worklogs.isEmpty()) return Optional.empty();
        List<AgentWorklog> wls = new ArrayList<>(worklogs);
        return Optional.ofNullable(wls.get(worklogs.size() - 1));
    }

}
