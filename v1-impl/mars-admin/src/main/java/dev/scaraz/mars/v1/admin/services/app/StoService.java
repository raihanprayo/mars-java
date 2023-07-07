package dev.scaraz.mars.v1.admin.services.app;

import dev.scaraz.mars.v1.admin.domain.app.Sto;

public interface StoService {
    Sto save(Sto sto);

    Sto create(Sto sto);
}
