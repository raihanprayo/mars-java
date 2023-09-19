package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.app.administration.config.CacheConfiguration;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.RealmConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmService {

    private static final Map<String, Object> INITIALIZER = new LinkedHashMap<>();
    public static final String
            CLIENT_IMPERSONATOR = "client-impersonator",
            USER_IMPERSONATED = "user-impersonated";
    private static final String
            INIT_KEY_CLIENTS = "witel-clients",
            INIT_KEY_ADMIN = "admin-user";

    private final Keycloak keycloak;
    private final RealmResource realmResource;
    private final KeycloakSpringBootProperties keycloakProperties;

    @Cacheable(
            value = CacheConfiguration.CACHE_KEYCLOAK_CLIENT,
            key = "'realm-management'")
    public ClientRepresentation getRealmManagement() {
        return realmResource.clients().findByClientId("realm-management").get(0);
    }

    public ClientResource getRealmManagementClientResource() {
        return realmResource.clients().get(getRealmManagement().getId());
    }

    @Async
    public void createAdministration() {
        if (INITIALIZER.containsKey(INIT_KEY_ADMIN)) return;
        INITIALIZER.put(INIT_KEY_ADMIN, 1);

        RolesResource roles = realmResource.roles();
        Set<String> realmRoles = roles.list().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toSet());

        if (!realmRoles.contains(RealmConstant.ROLE_COMPOSITE_ADMINISTRATOR)) {
            log.info("CREATE ADMIN ROLE");
            ClientResource rmcr = getRealmManagementClientResource();
            List<RoleRepresentation> rmRoles = rmcr.roles().list();

            RoleRepresentation adminRole = new RoleRepresentation();
            adminRole.setName(RealmConstant.ROLE_COMPOSITE_ADMINISTRATOR);
            adminRole.setDescription("admin role");
            adminRole.setComposite(true);
            roles.create(adminRole);

            RoleResource adminRoleResource = roles.get(RealmConstant.ROLE_COMPOSITE_ADMINISTRATOR);
            adminRoleResource.addComposites(rmRoles);

            List<RoleRepresentation> customRoles = Stream.of(
                            RealmConstant.Permission.ISSUE_QUERY,
                            RealmConstant.Permission.ISSUE_MANAGE,
                            RealmConstant.Permission.ISSUE_QUERY,
                            RealmConstant.Permission.STO_MANAGE,

                            RealmConstant.Permission.TICKET_QUERY
                    )
                    .map(e -> roles.get(e.getKey()).toRepresentation())
                    .collect(Collectors.toList());

            log.info("ROLES: {}", customRoles);
            adminRoleResource.addComposites(customRoles);
        }


        UsersResource users = realmResource.users();
        List<UserRepresentation> accounts = users.search("admin", true);
        if (accounts.isEmpty()) {
            log.info("CREATE admin ACCOUNT");

            RoleRepresentation adminRole = realmResource.roles().get(RealmConstant.ROLE_COMPOSITE_ADMINISTRATOR).toRepresentation();
            UserRepresentation admin = new UserRepresentation();
            admin.setEnabled(true);
            admin.setUsername("admin");
            admin.setFirstName("administrator");

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType("password");
            credential.setValue("admin");

            admin.setCredentials(List.of(credential));
            Response createAdminResponse = users.create(admin);
            String adminId = CreatedResponseUtil.getCreatedId(createAdminResponse);
            users.get(adminId).roles().realmLevel().add(List.of(adminRole));
        }
    }

    @Async
    public void createWitelClients() {
        if (INITIALIZER.containsKey(INIT_KEY_CLIENTS)) return;
        INITIALIZER.put(INIT_KEY_CLIENTS, 1);

        ClientsResource clientsResource = realmResource.clients();
        List<String> clients = clientsResource.findAll().stream()
                .map(ClientRepresentation::getClientId)
                .collect(Collectors.toList());

        ClientResource rmcr = getRealmManagementClientResource();
        ClientPolicyRepresentation clientImpersonatorPolicy = rmcr.authorization()
                .policies()
                .client()
                .findByName(CLIENT_IMPERSONATOR);
        ClientPolicyResource clientImpersonatorPolicyResource = rmcr.authorization()
                .policies()
                .client()
                .findById(clientImpersonatorPolicy.getId());

        List<ClientMetadata> tokenExchangeMap = new ArrayList<>();
        for (Witel witel : Witel.values()) {
            String clientId = witel.clientId();
            if (clients.stream().anyMatch(id -> id.equals(clientId)))
                continue;

            log.info("CREATE Witel Client - {}", clientId);
            try {
                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(clientId);
                client.setDescription(String.format("%s client", witel));
                client.setAlwaysDisplayInConsole(true);
                client.setServiceAccountsEnabled(true);

                try (Response response = clientsResource.create(client)) {
                    if (response.getStatus() == 201) {
                        client = clientsResource.findByClientId(clientId).get(0);

                        log.info("ENABLE Client PERMISSION CONFIG - {}", clientId);
                        ManagementPermissionReference permissions = clientsResource
                                .get(client.getId())
                                .setPermissions(new ManagementPermissionRepresentation(true));
                        String tokenExchangeId = permissions.getScopePermissions().get("token-exchange");
                        log.info("Client PERMISSION token-exchange ID - {}", tokenExchangeId);
                        tokenExchangeMap.add(new ClientMetadata(
                                client.getId(),
                                tokenExchangeId,
                                witel
                        ));
                    }
                }
            }
            catch (Exception ex) {
                List<ClientRepresentation> o = clientsResource.findByClientId(clientId);
                if (!o.isEmpty()) clientsResource.get(o.get(0).getId()).remove();
            }
        }

        if (!tokenExchangeMap.isEmpty()) {
            log.debug("CURRENT {} POLICY ClientIds - {}", CLIENT_IMPERSONATOR, clientImpersonatorPolicy.getClients());
            clientImpersonatorPolicy.addClient(tokenExchangeMap.stream()
                    .map(ClientMetadata::getClientId)
                    .toArray(String[]::new));
            clientImpersonatorPolicyResource.update(clientImpersonatorPolicy);

            log.debug("client-impersonator ID - {}", clientImpersonatorPolicy.getId());
            for (ClientMetadata entry : tokenExchangeMap) {
                Witel witel = entry.getWitel();
                String tokenExchangeId = entry.getTokenExchangeId();

                ScopePermissionResource scopePermissionResource = rmcr.authorization()
                        .permissions()
                        .scope()
                        .findById(tokenExchangeId);
                ScopePermissionRepresentation permission = scopePermissionResource.toRepresentation();

                permission.setDescription(String.format("%s Token Exchange Permission", witel));
                permission.addPolicy(clientImpersonatorPolicy.getId());
                log.debug("UPDATE Witel Permission - {}", witel);
                scopePermissionResource.update(permission);
            }
        }
    }


    public void resetWitelClients() {
        log.info("RESET ALL WITEL CLIENT RESOURCE");
        for (Witel value : Witel.values()) {
            ClientRepresentation cr = realmResource.clients().findByClientId(value.clientId()).get(0);
            realmResource.clients().get(cr.getId()).remove();
        }
        INITIALIZER.remove(INIT_KEY_CLIENTS);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ClientMetadata {
        private String clientId;
        private String tokenExchangeId;
        private Witel witel;
    }
}
