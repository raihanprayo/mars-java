package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.AgentDTO;
import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.AgentWorkspaceDTO;
import dev.scaraz.mars.core.domain.agent.Agent;
import dev.scaraz.mars.core.domain.agent.AgentWorklog;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.mapper.AgentMapper;
import dev.scaraz.mars.core.mapper.SolutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

//@Service
//@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class AgentMapperImpl implements AgentMapper {

    private final SolutionMapper solutionMapper;

    @Override
    public AgentDTO toDTO(Agent o) {
        if (o == null) return null;
        return AgentDTO.builder()
                .id(o.getId())
                .nik(o.getNik())
                .telegramId(o.getTelegramId())
                .userId(o.getUserId())
                .createdBy(o.getCreatedBy())
                .createdAt(o.getCreatedAt())
                .updatedBy(o.getUpdatedBy())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    @Override
    public AgentWorkspaceDTO toDTO(AgentWorkspace o) {
        if (o == null) return null;
        return AgentWorkspaceDTO.builder()
                .id(o.getId())
                .status(o.getStatus())
//                .agent(toDTO(o.getAgent()))
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    @Override
    public AgentWorklogDTO toDTO(AgentWorklog o) {
        if (o == null) return null;
        return AgentWorklogDTO.builder()
                .id(o.getId())
                .takeStatus(o.getTakeStatus())
                .closeStatus(o.getCloseStatus())
                .solution(solutionMapper.toDTO(o.getSolution()))
                .message(o.getMessage())
                .reopenMessage(o.getReopenMessage())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }


    @Override
    public AgentWorkspaceDTO toFullDTO(AgentWorkspace o) {
        return Optional.ofNullable(toDTO(o))
                .map(dto -> dto.toBuilder()
                        .worklogs(o.getWorklogs().stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList()))
                        .build())
                .orElse(null);
    }

    @Override
    public AgentWorklogDTO toFullDTO(AgentWorklog worklog) {
        if (worklog == null) return null;
        AgentWorkspace workspace = worklog.getWorkspace();
        return toDTO(worklog).toBuilder()
                .workspace(toDTO(workspace))
                .build();
    }
}
