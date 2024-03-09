package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.AgentWorklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentWorklogRepo extends
        JpaRepository<AgentWorklog, Long>,
        JpaSpecificationExecutor<AgentWorklog> {

    List<AgentWorklog> findByWorkspaceTicketIdOrWorkspaceTicketNoOrderByCreatedAt(String tid, String tno);


    @Modifying
    @Query("update AgentWorklog wl set " +
            "wl.solution.name = :solName," +
            "wl.solution.description = :solDesc " +
            "where wl.solution.id = :solId")
    void updateSolutionBySolutionId(long solId, String solName, String solDesc);

}
