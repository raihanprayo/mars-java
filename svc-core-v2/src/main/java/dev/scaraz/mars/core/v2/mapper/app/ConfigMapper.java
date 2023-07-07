package dev.scaraz.mars.core.v2.mapper.app;

import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import dev.scaraz.mars.core.v2.web.dto.ConfigDTO;
import dev.scaraz.mars.core.v2.web.dto.ConfigTagDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ConfigMapper {

    ConfigDTO toDTO(Config config);

    ConfigTagDTO toDTO(ConfigTag tag);

    default String tagName(ConfigTag t) {
        return t.getName();
    }

}
