package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public WhoamiDTO fromUser(User user) {
        if (user == null) return null;

        List<String> appRole = new ArrayList<>();
        List<String> groupRole = new ArrayList<>();
        for (Role role : user.getRoles())
            appRole.add(role.getName());


        Group group = user.getGroup();
        return WhoamiDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nik(user.getNik())
                .telegramId(user.getTelegramId())
                .email(user.getCredential().getEmail())
                .username(user.getCredential().getUsername())
                .roles(appRole)
                .group(WhoamiDTO.Group.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .roles(groupRole)
                        .build())
                .build();
    }

    ;

}
