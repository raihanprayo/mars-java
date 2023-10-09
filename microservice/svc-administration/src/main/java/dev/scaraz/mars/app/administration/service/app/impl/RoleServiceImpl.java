package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.config.security.SecurityUtil;
import dev.scaraz.mars.app.administration.service.app.RoleService;
import dev.scaraz.mars.app.administration.web.dto.UserAccount;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String MANAGED = "managed";

    private final RealmResource realmResource;

    @Override
    public void createInternal(RoleRepresentation role) {
        UserAccount account = SecurityUtil.getAccount();
        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(role.getAttributes(), LinkedHashMap::new);
        attributes.put("managed", List.of("1"));
        role.setAttributes(attributes);

        if (account != null) {
            attributes.put("witel", List.of(account.getWitel().name()));
            realmResource.clients()
                    .get(account.getWitel().clientId())
                    .roles()
                    .create(role);
        }
        else {
            realmResource.roles().create(role);
        }
    }

    @Override
    public void createInternal(Consumer<RoleRepresentation> consume) {
        RoleRepresentation role = new RoleRepresentation();
        consume.accept(role);
        createInternal(role);
    }

    @Override
    public RoleRepresentation create(RoleRepresentation role) {
        UserAccount account = SecurityUtil.getAccount();

        createInternal(role);

        if (account != null) {
            return realmResource.clients()
                    .get(account.getWitel().clientId())
                    .roles()
                    .get(role.getName())
                    .toRepresentation();
        }
        else {
            return realmResource.roles()
                    .get(role.getName())
                    .toRepresentation();
        }
    }

    @Override
    public RoleRepresentation create(Consumer<RoleRepresentation> consume) {
        UserAccount account = SecurityUtil.getAccount();
        RoleRepresentation role = new RoleRepresentation();
        consume.accept(role);
        createInternal(role);

        if (account != null) {
            return realmResource.clients()
                    .get(account.getWitel().clientId())
                    .roles()
                    .get(role.getName())
                    .toRepresentation();
        }
        else {
            return realmResource.roles()
                    .get(role.getName())
                    .toRepresentation();
        }
    }

    @Override
    public Map<String, List<RoleRepresentation>> findAll() {
        UserAccount account = SecurityUtil.getAccount();
        Map<String, List<RoleRepresentation>> result = new LinkedHashMap<>();

        result.put("realm", realmResource.roles().list().stream()
                .filter(role -> Objects.requireNonNullElse(role.getAttributes(), new LinkedHashMap<>()).containsKey(MANAGED))
                .collect(Collectors.toList()));

        if (account.getWitel() == Witel.ROC) {
            for (Witel value : Witel.values()) {
                result.put(value.clientId(), clientRoles(value).stream()
                        .filter(role -> role.getAttributes().containsKey(MANAGED))
                        .collect(Collectors.toList()));
            }
        }
        else {
            result.put(account.getWitel().clientId(), clientRoles(account.getWitel()).stream()
                    .filter(role -> role.getAttributes().containsKey(MANAGED))
                    .collect(Collectors.toList()));
        }

        return result;
    }

    @Override
    public List<RoleRepresentation> findByWitel(Witel witel) {
        return clientRoles(witel).stream()
                .filter(role -> role.getAttributes().containsKey(MANAGED))
                .collect(Collectors.toList());
    }

    private List<RoleRepresentation> clientRoles(Witel witel) {
        ClientRepresentation client = realmResource.clients().findByClientId(witel.clientId()).get(0);
        ClientResource cr = realmResource.clients().get(client.getId());
        return cr.roles().list();
    }

}
