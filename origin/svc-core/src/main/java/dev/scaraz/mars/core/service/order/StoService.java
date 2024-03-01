package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Sto;

public interface StoService {
    Sto save(Sto sto);

    Sto create(Sto sto);
}
