package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.SolutionDTO;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.order.WlSolution;
import dev.scaraz.mars.core.mapper.SolutionMapper;
import org.springframework.stereotype.Component;

@Component
public class SolutionMapperImpl implements SolutionMapper {

    @Override
    public SolutionDTO toDTO(Solution o) {
        if (o == null) return null;
        return SolutionDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .description(o.getDescription())
                .build();
    }

    @Override
    public SolutionDTO toDTO(WlSolution o) {
        if (o == null) return null;
        return SolutionDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .description(o.getDescription())
                .build();
    }

}
