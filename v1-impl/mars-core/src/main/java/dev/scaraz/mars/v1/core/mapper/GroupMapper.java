package dev.scaraz.mars.v1.core.mapper;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.v1.core.domain.credential.Group;

public interface GroupMapper {
    GroupDTO toDTO(Group o);
}
