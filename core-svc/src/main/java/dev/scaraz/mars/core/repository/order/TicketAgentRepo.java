package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAgentRepo extends JpaRepository<TicketAgent, String>, JpaSpecificationExecutor<TicketAgent> {

    boolean existsByTicketIdAndStatus(String ticketId, AgStatus status);
    boolean existsByTicketNoAndStatus(String ticketNo, AgStatus status);

}
