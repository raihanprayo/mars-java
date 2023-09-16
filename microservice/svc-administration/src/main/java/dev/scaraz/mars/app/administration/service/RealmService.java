package dev.scaraz.mars.app.administration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.app.administration.repository.cache.ImpersonateTokenCacheRepo;
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
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
            INIT_KEY_POLICIES = "witel-clients",
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

    private void createWitelClient() {
//        realmResource.clients().findByClientId()
    }

    public void resetClients() {
//        log.info("RESET ALL WITEL CLIENT RESOURCE");
//        for (Witel value : Witel.values()) {
//            ClientRepresentation cr = realmResource.clients().findByClientId(value.clientId()).get(0);
//            realmResource.clients().get(cr.getId()).remove();
//        }
//
//        ClientResource cr = getRealmManagementClientResource();
//        ClientPolicyRepresentation cpr = cr.authorization().policies().client().findByName(POLICY_NAME);
//        log.debug("Client Policy: {}", cpr);
//        if (cpr != null)
//            cr.authorization().policies().client().findById(cpr.getId()).remove();
//        INITIALIZER.remove(INIT_KEY_POLICIES);
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
