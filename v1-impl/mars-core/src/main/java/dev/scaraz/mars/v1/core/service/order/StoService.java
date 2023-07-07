package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.v1.core.domain.order.Sto;

public interface StoService {
    Sto save(Sto sto);

    Sto create(Sto sto);
}
