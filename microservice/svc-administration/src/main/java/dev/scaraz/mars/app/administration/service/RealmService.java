package dev.scaraz.mars.app.administration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.app.administration.domain.cache.ImpersonateTokenCache;
import dev.scaraz.mars.app.administration.repository.cache.ImpersonateTokenCacheRepo;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    public void createAdminUser() {
        if (INITIALIZER.containsKey(INIT_KEY_ADMIN)) return;
        INITIALIZER.put(INIT_KEY_ADMIN, 1);

        UsersResource users = realmResource.users();
        List<UserRepresentation> accounts = users.search("admin", true);
        if (!accounts.isEmpty()) return;

        log.info("CREATING Administrator ACCOUNT");
        UserRepresentation admin = new UserRepresentation();
        admin.setUsername("admin");
        admin.setFirstName("administrator");

        MultiValueMap<String, String> attributes = new LinkedMultiValueMap<>();
        attributes.add("witel", Witel.ROC.name());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType("password");
        credential.setValue("admin");

        admin.setAttributes(attributes);
        admin.setCredentials(List.of(credential));

        users.create(admin).close();
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

    public ImpersonateTokenCache impersonate(String userId, Witel witel) {
        if (tokenCacheRepo.existsById(userId)) return tokenCacheRepo.getById(userId);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            String serverUrl = String.join("/",
                    keycloakProperties.getAuthServerUrl(),
                    "realms",
                    keycloakProperties.getRealm(),
                    "protocol",
                    "openid-connect",
                    "token"
            );

            try (CloseableHttpResponse response = client.execute(RequestBuilder.post()
                    .setUri(serverUrl)
                    .addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .addParameter("client_id", keycloakProperties.getResource())
                    .addParameter("client_secret", keycloakProperties.getCredentials().get("secret").toString())
                    .addParameter("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                    .addParameter("requested_token_type", "urn:ietf:params:oauth:token-type:access_token")
                    .addParameter("subject_token", keycloak.tokenManager().getAccessTokenString())
                    .addParameter("requested_subject", userId)
                    .addParameter("audience", witel.clientId())
                    .build())
            ) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode <= 299) {
                    AccessTokenResponse accessToken = objectMapper.readValue(response.getEntity().getContent(), AccessTokenResponse.class);
                    return tokenCacheRepo.save(ImpersonateTokenCache.builder()
                            .id(userId)
                            .accessToken(accessToken.getToken())
                            .accessTokenExpired(accessToken.getExpiresIn())
                            .refreshToken(accessToken.getRefreshToken())
                            .refreshTokenExpired(accessToken.getRefreshExpiresIn())
                            .build());
                }
                else if (statusCode >= 400 && statusCode <= 499) {
                    AccessTokenResponse accessToken = objectMapper.readValue(response.getEntity().getContent(), AccessTokenResponse.class);
                    String error = accessToken.getError();
                    String errorDescription = accessToken.getErrorDescription();

                    throw new BadRequestException(String.format("Failed to impersonate user: %s - %s", error, errorDescription));
                }

                throw new BadRequestException("Failed to impersonate user: " + statusCode);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
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
