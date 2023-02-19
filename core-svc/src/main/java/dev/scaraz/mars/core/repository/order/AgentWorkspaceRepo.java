package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentWorkspaceRepo extends
        JpaRepository<AgentWorkspace, Long>,
        JpaSpecificationExecutor<AgentWorkspace> {

    List<AgentWorkspace> findByTicketId(String ticketId);

    Optional<AgentWorkspace> findFirstByTicketIdOrderByCreatedAtDesc(String ticketId);

    Optional<AgentWorkspace> findFirstByTicketIdAndAgentIdOrderByCreatedAtDesc(String ticketId, String agentId);

    boolean existsByTicketIdAndStatus(String ticketId, AgStatus status);

}
