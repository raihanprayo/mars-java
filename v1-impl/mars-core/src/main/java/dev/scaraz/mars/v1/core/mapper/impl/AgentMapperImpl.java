package dev.scaraz.mars.v1.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.AgentDTO;
import dev.scaraz.mars.common.domain.response.AgentWorklogDTO;
import dev.scaraz.mars.common.domain.response.AgentWorkspaceDTO;
import dev.scaraz.mars.v1.core.domain.order.Agent;
import dev.scaraz.mars.v1.core.domain.order.AgentWorklog;
import dev.scaraz.mars.v1.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.v1.core.mapper.AgentMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

//@Service
//@Transactional(readOnly = true)
@Component
public class AgentMapperImpl implements AgentMapper {

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
                .agent(toDTO(o.getAgent()))
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
                .solution(o.getSolution())
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
