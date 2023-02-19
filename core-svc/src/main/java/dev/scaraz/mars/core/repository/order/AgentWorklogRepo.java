package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentWorklogRepo extends
        JpaRepository<AgentWorklog, Long>,
        JpaSpecificationExecutor<AgentWorklog> {
}