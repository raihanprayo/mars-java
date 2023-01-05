package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Optional;

@Repository
public interface TicketAgentRepo extends JpaRepository<TicketAgent, String>, JpaSpecificationExecutor<TicketAgent> {

    Optional<TicketAgent> findByTicketIdAndUserId(String tcId, String usrId);

    boolean existsByTicketIdAndStatus(String ticketId, AgStatus status);

    boolean existsByTicketNoAndStatus(String ticketNo, AgStatus status);

    //    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("update TicketAgent ag set " +
//            "ag.status = :agStatus, " +
//            "ag.closeStatus = :tcStatus, " +
//            "ag.description = :desc " +
//            "where ag.id = :id")
    default TicketAgent updateStatusAndCloseStatusAndCloseDesc(String id,
                                                               AgStatus agStatus,
                                                               TcStatus tcStatus,
                                                               @Nullable String desc) {
        return findById(id).map(ag -> {
                    ag.setStatus(agStatus);
                    ag.setCloseStatus(tcStatus);
                    ag.setDescription(desc);
                    return save(ag);
                })
                .orElseThrow(() -> NotFoundException.entity(TicketAgent.class, "id", id));
    }

}
