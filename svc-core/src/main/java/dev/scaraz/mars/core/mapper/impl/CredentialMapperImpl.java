package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.AccountTg;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountSetting;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CredentialMapperImpl implements CredentialMapper {

    @Lazy
    private final RoleMapper roleMapper;

    @Override
    public UserDTO toDTO(Account o) {
        if (o == null) return null;
        return UserDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .nik(o.getNik())
                .phone(o.getPhone())
                .witel(o.getWitel())
                .sto(o.getSto())
                .tg(toDTO(o.getTg()))
                .active(o.isActive())
                .roles(o.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .setting(toDTO(o.getSetting()))
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    @Override
    public UserDTO.UserTgDTO toDTO(AccountTg o) {
        if (o == null) return null;
        return UserDTO.UserTgDTO.builder()
                .id(o.getId())
                .username(o.getUsername())
                .build();
    }

    @Override
    public UserDTO.UserSettingDTO toDTO(AccountSetting o) {
        if (o == null) return null;
        return UserDTO.UserSettingDTO.builder()
                .lang(o.getLang())
                .build();
    }

    @Override
    public WhoamiDTO fromUser(Account account) {
        if (account == null) return null;

        List<String> appRole = account.getRoles().stream()
                .map(Role::toString)
                .collect(Collectors.toList());

        WhoamiDTO.WhoamiDTOBuilder builder = WhoamiDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .nik(account.getNik())
                .email(account.getEmail())
                .witel(account.getWitel())
                .sto(account.getSto())
                .telegramId(account.getTg().getId())
                .username(account.getTg().getUsername())
                .roles(appRole);

        return builder.build();
    }

}
