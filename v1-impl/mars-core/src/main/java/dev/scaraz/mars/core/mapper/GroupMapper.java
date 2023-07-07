package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.core.domain.credential.Group;

public interface GroupMapper {
    GroupDTO toDTO(Group o);
}
