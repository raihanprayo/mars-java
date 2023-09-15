package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.app.administration.service.RealmService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/kc")
public class KeycloakAppResource {

    private final RealmService realmService;
    private final UserService userService;

    @DeleteMapping("/clients")
    public void resets() {
        realmService.resetClients();
    }

    @PostMapping("/clients")
    public void recreateClients() {
        realmService.createWitelClients();
    }

    @GetMapping("/impersonate")
    public ResponseEntity<?> impersonate(@RequestParam String userId) {
        UserRepresentation user = userService.findById(userId);
        return ResponseEntity.ok(realmService.impersonate(user.getId(), Witel.ROC));
    }

}
