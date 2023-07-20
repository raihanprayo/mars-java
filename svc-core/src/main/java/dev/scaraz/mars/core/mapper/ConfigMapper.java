package dev.scaraz.mars.core.mapper;

//import dev.scaraz.mars.core.v2.domain.app.Config;
//import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
//import dev.scaraz.mars.core.v2.web.dto.ConfigDTO;
//import dev.scaraz.mars.core.v2.web.dto.ConfigTagDTO;
//import org.mapstruct.Builder;
//import org.mapstruct.Mapper;


import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.common.domain.ConfigTagDTO;
import dev.scaraz.mars.core.domain.Config;
import dev.scaraz.mars.core.domain.ConfigTag;

import java.util.List;

public interface ConfigMapper {

    ConfigDTO toDTO(Config config);

    List<ConfigDTO> toDTO(List<Config> config);

    ConfigTagDTO toDTO(ConfigTag tag);

}
