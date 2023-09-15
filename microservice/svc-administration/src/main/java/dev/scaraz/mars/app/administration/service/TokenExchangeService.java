package dev.scaraz.mars.app.administration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.app.administration.domain.cache.ImpersonateTokenCache;
import dev.scaraz.mars.app.administration.repository.cache.ImpersonateTokenCacheRepo;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenExchangeService {
    private final ObjectMapper objectMapper;

    private final Keycloak keycloak;
    private final ImpersonateTokenCacheRepo tokenCacheRepo;
    private final KeycloakSpringBootProperties keycloakProperties;

    public ImpersonateTokenCache exchange(String userId, Witel witel) {

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
//                    .addParameter("requested_token_type", "urn:ietf:params:oauth:token-type:access_token")
//                    .addParameter("subject_token", keycloak.tokenManager().getAccessTokenString())
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

}
