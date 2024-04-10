package dev.scaraz.mars.core.repository.db.view;

import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderBoardFragmentRepo extends
        JpaRepository<LeaderBoardFragment, String>,
        JpaSpecificationExecutor<LeaderBoardFragment> {

    List<LeaderBoardFragment> findAllByTicketIdAndAgentId(String ticketId, String agentId);

}
