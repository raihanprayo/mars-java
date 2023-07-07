package dev.scaraz.mars.v1.core.repository.order;

import dev.scaraz.mars.v1.core.domain.order.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepo extends JpaRepository<Agent, String>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findByUserId(String userId);

//    Optional<Agent> findByTicketIdAndUserId(String tcId, String usrId);
//
//    boolean existsByTicketIdAndStatus(String ticketId, AgStatus status);
//
//    boolean existsByTicketNoAndStatus(String ticketNo, AgStatus status);

    //    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("update TicketAgent ag set " +
//            "ag.status = :agStatus, " +
//            "ag.closeStatus = :tcStatus, " +
//            "ag.description = :desc " +
//            "where ag.id = :id")
//    default Agent updateStatusAndCloseStatusAndCloseDesc(String id,
//                                                         AgStatus agStatus,
//                                                         TcStatus closeStatus,
//                                                         @Nullable Long solution,
//                                                         @Nullable String desc) {
//        return findById(id).map(ag -> {
//                    ag.setStatus(agStatus);
//                    ag.setCloseStatus(closeStatus);
//                    ag.setDescription(desc);
//                    ag.setSolution(solution);
//                    return save(ag);
//                })
//                .orElseThrow(() -> NotFoundException.entity(Agent.class, "id", id));
//    }

}
