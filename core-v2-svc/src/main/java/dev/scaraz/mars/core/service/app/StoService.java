package dev.scaraz.mars.core.service.app;

import dev.scaraz.mars.core.datasource.domain.Sto;

public interface StoService {
    Sto save(Sto sto);

    Sto create(Sto sto);
}
