package dev.scaraz.mars.v1.admin.mapper;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import dev.scaraz.mars.v1.admin.domain.app.ConfigTag;
import dev.scaraz.mars.common.domain.ConfigDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ConfigMapper {

    ConfigDTO toDTO(Config config);

    default String toTagName(ConfigTag tag) {
        return tag.getName();
    }

}
