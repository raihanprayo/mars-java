package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.app.administration.web.dto.UserRegistrationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserService {

    private final RealmResource realm;

    @Cacheable(
            cacheNames = "tg:user",
            unless = "#result == null",
            sync = true)
    public UserRepresentation findByTelegramId(long telegramId) throws IllegalStateException {
        List<UserRepresentation> users = realm.users().searchByAttributes("telegram=" + telegramId);
        if (users.size() != 1) throw new IllegalStateException("user not found");
        return users.get(0);
    }

    public Optional<UserRepresentation> findByTelegramIdOpt(long telegramId) {
        try {
            UserRepresentation user = findByTelegramId(telegramId);
            return Optional.of(user);
        }
        catch (IllegalStateException ex) {
        }
        return Optional.empty();
    }

    public void registerNewUser(UserRegistrationDTO dto) {
        UsersResource users = realm.users();
        UserRepresentation user = new UserRepresentation();

        // TODO: config user approval
        user.setEnabled(true);

        String name = dto.getName().trim();
        String[] split = name.split(" ");

        if (split.length == 1) user.setFirstName(name);
        else {
            int i = name.lastIndexOf(" ");
            user.setFirstName(name.substring(0, i).trim());
            user.setLastName(name.substring(i).trim());
        }

        MultiValueMap<String, String> attributes = new LinkedMultiValueMap<>();
        attributes.set("phone", dto.getPhone());
        attributes.set("witel", dto.getWitel().name());

        if (StringUtils.isNotBlank(dto.getSto()))
            attributes.set("sto", dto.getSto());

        if (dto.getTelegramId() != null)
            attributes.set("telegram", dto.getTelegramId().toString());

        user.setAttributes(attributes);
        users.create(user);
    }

}
