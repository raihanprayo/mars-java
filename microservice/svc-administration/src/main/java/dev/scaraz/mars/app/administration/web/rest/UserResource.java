package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientMappingsRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/user")
public class UserResource {

    private final RealmResource realmResource;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<UserRepresentation> list = realmResource.users().list();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<?> getRoles(@PathVariable String id) {
        MappingsRepresentation all = realmResource.users().get(id).roles().getAll();
        Map<String, List<RoleRepresentation>> mappedRoles = new LinkedHashMap<>();

        mappedRoles.put("realm", all.getRealmMappings());
        Map<String, ClientMappingsRepresentation> clientMappings = Objects.requireNonNullElse(
                all.getClientMappings(),
                new LinkedHashMap<>()
        );

        for (Witel value : Witel.values()) {
            ClientMappingsRepresentation mapping = clientMappings.get(value.clientId());
            if (mapping != null)
                mappedRoles.put(value.clientId(), mapping.getMappings());
        }

        return new ResponseEntity<>(
                mappedRoles,
                HttpStatus.OK
        );
    }

}
