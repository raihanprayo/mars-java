package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.SolutionDTO;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.order.WlSolution;

public interface SolutionMapper {
    SolutionDTO toDTO(Solution o);

    SolutionDTO toDTO(WlSolution o);
}
