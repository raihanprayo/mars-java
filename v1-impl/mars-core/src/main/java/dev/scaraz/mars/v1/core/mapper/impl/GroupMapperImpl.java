package dev.scaraz.mars.v1.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.v1.core.domain.credential.Group;
import dev.scaraz.mars.v1.core.mapper.GroupMapper;
import org.springframework.stereotype.Component;

@Component
public class GroupMapperImpl implements GroupMapper {

    @Override
    public GroupDTO toDTO(Group o) {
        if (o == null) return null;
        return GroupDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .setting(GroupDTO.GroupSettingDTO.builder()
                        .canLogin(o.getSetting().canLogin())
                        .build())
                .build();
    }

}
