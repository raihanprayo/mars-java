package dev.scaraz.mars.core.repository.db.order;

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

    void deleteAllByTicketId(String ticketId);

    List<AgentWorkspace> findByAccountId(String userId);

    List<AgentWorkspace> findByTicketIdOrTicketNo(String tid, String tno);
    List<AgentWorkspace> findByTicketIdOrTicketNoOrderByCreatedAt(String tid, String tno);

    Optional<AgentWorkspace> findFirstByTicketIdOrderByCreatedAtDesc(String ticketId);

    Optional<AgentWorkspace> findFirstByTicketIdAndAccountIdOrderByCreatedAtDesc(String ticketId, String agentId);

    boolean existsByTicketIdAndStatus(String tid, AgStatus status);
    boolean existsByTicketNoAndStatus(String tno, AgStatus status);

}
