package dev.scaraz.mars.core.mapper.impl;

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
import dev.scaraz.mars.core.mapper.ConfigMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConfigMapperImpl implements ConfigMapper {

    @Override
    public ConfigDTO toDTO(Config config) {
        if (config == null) return null;
        return ConfigDTO.builder()
                .key(config.getKey())
                .value(config.getValue())
                .type(config.getType())
                .tag(toTagName(config.getTag()))
                .build();
    }

    @Override
    public List<ConfigDTO> toDTO(List<Config> configs) {
        return configs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ConfigTagDTO toDTO(ConfigTag tag) {
        if (tag == null) return null;
        return ConfigTagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    private String toTagName(ConfigTag tag) {
        return tag == null ? null : tag.getName();
    }

}
