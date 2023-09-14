package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmService {

    private final RealmResource realmResource;

    @Async
    public void createWitelClients() {
        ClientsResource clients = realmResource.clients();
        for (Witel witel : Witel.values()) {
            String clientId = witel.clientId();

            List<ClientRepresentation> byClientId = clients.findByClientId(clientId);
            if (!byClientId.isEmpty()) continue;

            ClientRepresentation client = new ClientRepresentation();
            client.setClientId(clientId);
            client.setDescription(String.format("Witel %s Client Resource", witel));
            client.setAlwaysDisplayInConsole(true);
            client.setEnabled(true);
            client.setDirectGrantsOnly(true);
            client.setServiceAccountsEnabled(true);

            log.info("Create new Witel Client: {}", clientId);
            Response response = clients.create(client);

            if (response.getStatus() == 200 || response.getStatus() == 201)
                log.info("{} client id {} created", witel, clientId);

            response.close();
        }
    }

    public void impersonate() {
    }

}
