package dev.scaraz.mars.app.administration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.app.administration.repository.cache.ImpersonateTokenCacheRepo;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.RoleConstant;
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
import org.keycloak.representations.idm.authorization.Logic;
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
    private static final String
            POLICY_NAME = "witels-token-exchange";
    private static final String
            INIT_KEY_CLIENTS = "witel-clients",
            INIT_KEY_ADMIN = "admin-user";

    private final Keycloak keycloak;
    private final RealmResource realmResource;
    private final KeycloakSpringBootProperties keycloakProperties;

    private final ImpersonateTokenCacheRepo tokenCacheRepo;
    private final ObjectMapper objectMapper;

    @Cacheable("kc:client")
    public ClientRepresentation getRealmManagement() {
        return realmResource.clients().findByClientId("realm-management").get(0);
    }

    private ClientResource getRealmManagementClientResource() {
        return realmResource.clients().get(getRealmManagement().getId());
    }

    @Async
    public void createWitelClients() {
        if (INITIALIZER.containsKey(INIT_KEY_CLIENTS)) return;
        INITIALIZER.put(INIT_KEY_CLIENTS, 1);

        ClientsResource clients = realmResource.clients();
        Map<Witel, ClientMetadata> clientIds = new EnumMap<>(Witel.class);
        try {
            for (Witel witel : Witel.values()) {
                String clientId = witel.clientId();

                List<ClientRepresentation> byClientId = clients.findByClientId(clientId);
                if (!byClientId.isEmpty()) continue;

                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(clientId);
                client.setDescription(String.format("Witel %s Client Resource", witel));
                client.setAlwaysDisplayInConsole(true);
                client.setEnabled(true);
                client.setServiceAccountsEnabled(true);

                log.info("Create new Witel Client ID: {}", clientId);
                try (Response createResponse = clients.create(client)) {
                    boolean isOK = createResponse.getStatus() == 200 || createResponse.getStatus() == 201;
                    if (!isOK) return;

                    String createdId = CreatedResponseUtil.getCreatedId(createResponse);
                    ClientResource clientResource = clients.get(createdId);
                    ManagementPermissionReference ref = clientResource.setPermissions(new ManagementPermissionRepresentation(true));
                    String tokenExchangePermissionId = ref.getScopePermissions().get("token-exchange");
//
//                ClientResource rmClient = getRealmManagementClientResource();
//                PermissionsResource permissionsResource = rmClient.authorization().permissions();
//
//                log.info("Updating Witel Client Token-Exchange permission - {}", witel);
//                ScopePermissionResource spRes = permissionsResource.scope().findById(tokenExchangePermissionId);
//                ScopePermissionRepresentation spRep = spRes.toRepresentation();
//                spRep.setName(String.format("%s-token-exchange-permission", witel.toString().toLowerCase()));
//                spRep.setDescription(witel.name());
//                spRes.update(spRep);
                    clientIds.put(witel, new ClientMetadata(createdId, tokenExchangePermissionId));
                }
            }

            if (clientIds.isEmpty()) return;

            ClientResource realmMngClient = getRealmManagementClientResource();
            String policyId = createTokenExchangePolicy(
                    clientIds.values().stream()
                            .map(ClientMetadata::getClientId)
                            .collect(Collectors.toList()),
                    realmMngClient
            );
            try {
                for (Witel witel : Witel.values())
                    updateClientPermissionPolicy(witel, clientIds.get(witel), policyId, realmMngClient);
            }
            catch (Exception ex) {
                realmMngClient.authorization().policies().client().findById(policyId).remove();
                throw ex;
            }
        }
        catch (Exception ex) {
            if (!clientIds.isEmpty()) {
                for (Witel witel : clientIds.keySet()) {
                    ClientMetadata md = clientIds.get(witel);
                    clients.get(md.clientId).remove();
                }
            }

            throw ex;
        }
    }

    @Async
    public void createAdministration() {
        if (INITIALIZER.containsKey(INIT_KEY_ADMIN)) return;
        INITIALIZER.put(INIT_KEY_ADMIN, 1);

        RolesResource roles = realmResource.roles();
        Set<String> realmRoles = roles.list().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toSet());

        for (String roleName : RoleConstant.PERMISSIONS.keySet()) {
            if (realmRoles.contains(roleName)) continue;

            log.info("CREATE NEW ROLE PERMISSION - {}", roleName);
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(RoleConstant.PERMISSIONS.get(roleName));
            roles.create(role);
        }

        if (!realmRoles.contains(RoleConstant.ROLE_COMPOSITE_ADMINISTRATOR)) {
            log.info("CREATE ADMIN ROLE");
            ClientResource rmcr = getRealmManagementClientResource();
            List<RoleRepresentation> rmRoles = rmcr.roles().list();

            RoleRepresentation adminRole = new RoleRepresentation();
            adminRole.setName(RoleConstant.ROLE_COMPOSITE_ADMINISTRATOR);
            adminRole.setDescription("admin role");
            adminRole.setComposite(true);
            roles.create(adminRole);

            RoleResource adminRoleResource = roles.get(RoleConstant.ROLE_COMPOSITE_ADMINISTRATOR);
            adminRoleResource.addComposites(rmRoles);

            List<RoleRepresentation> customRoles = Stream.of(
                            RoleConstant.Permission.ISSUE_QUERY,
                            RoleConstant.Permission.ISSUE_MANAGE,
                            RoleConstant.Permission.ISSUE_QUERY,
                            RoleConstant.Permission.STO_MANAGE,

                            RoleConstant.Permission.TICKET_QUERY
                    )
                    .map(e -> roles.get(e.getKey()).toRepresentation())
                    .collect(Collectors.toList());

            adminRoleResource.addComposites(customRoles);
            UsersResource users = realmResource.users();
            List<UserRepresentation> accounts = users.search("admin", true);
            if (accounts.isEmpty()) {
                log.info("CREATE admin ACCOUNT");
                UserRepresentation admin = new UserRepresentation();
                admin.setEnabled(true);
                admin.setUsername("admin");
                admin.setFirstName("administrator");
                admin.setRealmRoles(List.of(adminRole.getId()));

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType("password");
                credential.setValue("admin");

                admin.setCredentials(List.of(credential));
                users.create(admin).close();
            }
        }
    }

    public void resetClients() {
        log.info("RESET ALL WITEL CLIENT RESOURCE");
        for (Witel value : Witel.values()) {
            ClientRepresentation cr = realmResource.clients().findByClientId(value.clientId()).get(0);
            realmResource.clients().get(cr.getId()).remove();
        }

        ClientResource cr = getRealmManagementClientResource();
        ClientPolicyRepresentation cpr = cr.authorization().policies().client().findByName(POLICY_NAME);
        log.debug("Client Policy: {}", cpr);
        if (cpr != null)
            cr.authorization().policies().client().findById(cpr.getId()).remove();
        INITIALIZER.remove(INIT_KEY_CLIENTS);
    }

    private String createTokenExchangePolicy(Collection<String> clientIds, ClientResource realmMngClient) {
        ClientPolicyRepresentation cpRep = new ClientPolicyRepresentation();
        cpRep.setName(POLICY_NAME);
        cpRep.setLogic(Logic.POSITIVE);
        cpRep.addClient(clientIds.toArray(String[]::new));

        ClientPoliciesResource cpRes = realmMngClient.authorization().policies().client();
        cpRes.findByName(cpRep.getName());

        try (Response response = cpRes.create(cpRep)) {
            log.info("CREATE TOKEN EXCHANGE Policy response Status - {}", response.getStatus());

            cpRep = response.readEntity(ClientPolicyRepresentation.class);
            log.info("CREATE TOKEN EXCHANGE Policy ID - {}", cpRep.getId());
            return cpRep.getId();
        }
    }

    private void updateClientPermissionPolicy(Witel witel,
                                              ClientMetadata clientMetadata,
                                              String policyId,
                                              ClientResource realmMngClient
    ) {
        PermissionsResource permissionsResource = realmMngClient.authorization().permissions();

        log.info("Updating Witel Client token-exchange permission - {}", witel);
        ScopePermissionResource spRes = permissionsResource.scope().findById(clientMetadata.permissionId);
        ScopePermissionRepresentation spRep = spRes.toRepresentation();
        spRep.setName(String.format("%s-token-exchange-permission", witel.toString().toLowerCase()));
        spRep.setDescription(witel.name());
        spRep.addPolicy(policyId);
        spRes.update(spRep);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ClientMetadata {
        private String clientId;
        private String permissionId;
    }
}
