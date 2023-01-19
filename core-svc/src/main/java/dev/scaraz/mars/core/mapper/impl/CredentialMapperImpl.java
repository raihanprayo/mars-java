package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CredentialMapperImpl implements CredentialMapper {

    @Lazy
    private final RoleMapper roleMapper;

    @Override
    public UserDTO toDTO(User o) {
        if (o == null) return null;
        return UserDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .nik(o.getNik())
                .phone(o.getPhone())
                .active(o.isActive())
                .telegramId(o.getTelegramId())
                .group(toPartialDTO(o.getGroup()))
                .roles(o.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .credential(toDTO(o.getCredential()))
                .setting(toDTO(o.getSetting()))
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    @Override
    public UserDTO.UserCredentialDTO toDTO(UserCredential o) {
        if (o == null) return null;
        return UserDTO.UserCredentialDTO.builder()
                .username(o.getUsername())
                .email(o.getEmail())
                .build();
    }

    @Override
    public UserDTO.UserSettingDTO toDTO(UserSetting o) {
        if (o == null) return null;
        return UserDTO.UserSettingDTO.builder()
                .lang(o.getLang())
                .build();
    }

    @Override
    public GroupDTO toPartialDTO(Group o) {
        if (o == null) return null;
        return GroupDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .build();
    }

    @Override
    public WhoamiDTO fromUser(User user) {
        if (user == null) return null;

        List<String> appRole = user.getRoles().stream()
                .map(Role::toString)
                .collect(Collectors.toList());

        WhoamiDTO.WhoamiDTOBuilder builder = WhoamiDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nik(user.getNik())
                .telegramId(user.getTelegramId())
                .email(user.getCredential().getEmail())
                .username(user.getCredential().getUsername())
                .witel(user.getWitel())
                .sto(user.getSto())
                .roles(appRole);

        Group group = user.getGroup();
        if (group != null) {
            builder.group(WhoamiDTO.Group.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .build());
        }

        return builder.build();
    }

}
