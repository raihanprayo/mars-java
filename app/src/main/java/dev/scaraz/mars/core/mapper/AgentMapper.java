package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.AgentDTO;
import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.AgentWorkspaceDTO;
import dev.scaraz.mars.core.domain.agent.Agent;
import dev.scaraz.mars.core.domain.agent.AgentWorklog;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;

public interface AgentMapper {
    AgentDTO toDTO(Agent o);

    AgentWorkspaceDTO toDTO(AgentWorkspace o);

    AgentWorklogDTO toDTO(AgentWorklog worklog);

    AgentWorkspaceDTO toFullDTO(AgentWorkspace o);

    AgentWorklogDTO toFullDTO(AgentWorklog worklog);
}
