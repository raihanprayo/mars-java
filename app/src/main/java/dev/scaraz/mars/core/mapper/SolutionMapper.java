package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.SolutionDTO;
import dev.scaraz.mars.core.domain.symptom.Solution;
import dev.scaraz.mars.core.domain.order.WlSolution;
import org.mapstruct.Mapper;

@Mapper
public abstract class SolutionMapper {

    public abstract SolutionDTO toDTO(Solution o);

    public abstract SolutionDTO toDTO(WlSolution o);

}
