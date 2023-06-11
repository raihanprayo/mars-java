package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.web.dto.UserDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = RoleMapper.class,
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserDTO toDTO(MarsUser user);

}
