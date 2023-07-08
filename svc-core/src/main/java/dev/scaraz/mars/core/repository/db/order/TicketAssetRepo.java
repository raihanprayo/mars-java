package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.TicketAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAssetRepo extends JpaRepository<TicketAsset, Long> {
    List<TicketAsset> findAllByTicketIdAndAgentId(String ticketId, String agentId);

    List<TicketAsset> findAllByTicketIdAndAgentIsNull(String ticketId);
    List<TicketAsset> findAllByTicketId(String ticketId);
}
