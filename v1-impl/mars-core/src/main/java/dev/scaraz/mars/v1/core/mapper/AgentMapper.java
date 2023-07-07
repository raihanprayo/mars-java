package dev.scaraz.mars.v1.core.mapper;

import dev.scaraz.mars.common.domain.response.AgentDTO;
import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.AgentWorkspaceDTO;
import dev.scaraz.mars.v1.core.domain.order.Agent;
import dev.scaraz.mars.v1.core.domain.order.AgentWorklog;
import dev.scaraz.mars.v1.core.domain.order.AgentWorkspace;

public interface AgentMapper {
    AgentDTO toDTO(Agent o);

    AgentWorkspaceDTO toDTO(AgentWorkspace o);

    AgentWorklogDTO toDTO(AgentWorklog worklog);

    AgentWorkspaceDTO toFullDTO(AgentWorkspace o);

    AgentWorklogDTO toFullDTO(AgentWorklog worklog);
}
