package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.user.domain.csv.StoCsv;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.web.dto.StoDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface StoMapper {

    @Mapping(target = "id", source = "code")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sto toEntity(StoCsv csv);

    Sto toEntity(StoDTO csv);

    StoDTO toDTO(Sto sto);

}
