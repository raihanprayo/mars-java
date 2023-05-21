package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.common.domain.general.StoDTO;
import dev.scaraz.mars.user.datasource.domain.Sto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoMapper {

    StoDTO toDTO(Sto sto);
    Sto fromDTO(StoDTO sto);

}
