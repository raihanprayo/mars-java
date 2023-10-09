package dev.scaraz.mars.app.administration.service.app;

import dev.scaraz.mars.common.tools.enums.Witel;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface RoleService {
    void createInternal(RoleRepresentation role);

    void createInternal(Consumer<RoleRepresentation> consume);

    RoleRepresentation create(RoleRepresentation role);

    RoleRepresentation create(Consumer<RoleRepresentation> consume);

    Map<String, List<RoleRepresentation>> findAll();

    List<RoleRepresentation> findByWitel(Witel witel);
}
