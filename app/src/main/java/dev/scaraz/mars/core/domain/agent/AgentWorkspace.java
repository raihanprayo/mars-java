package dev.scaraz.mars.core.domain.agent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_agent_workspace")
public class AgentWorkspace extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private AgStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ref_ticket_id", updatable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private Account account;

    @Builder.Default
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "workspace", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AgentWorklog> worklogs = new HashSet<>();

    @JsonIgnore
    public Optional<AgentWorklog> getLastWorklog() {
        if (worklogs.isEmpty()) return Optional.empty();
        List<AgentWorklog> wls = new ArrayList<>(worklogs);
        return Optional.ofNullable(wls.get(worklogs.size() - 1));
    }

    @Override
    public String toString() {
        return "AgentWorkspace{" +
                "id=" + id +
                ", status=" + status +
                ", ticket=" + ticket.getId() +
                ", account=" + account.getId() +
                '}';
    }
}
