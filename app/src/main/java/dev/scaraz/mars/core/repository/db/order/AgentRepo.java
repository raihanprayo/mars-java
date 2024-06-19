package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepo extends JpaRepository<Agent, String>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findByUserId(String userId);

}
