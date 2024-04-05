package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.criteria.StoCriteria;

public interface StoQueryService extends BaseQueryService<Sto, StoCriteria> {
    Sto findById(int id);
}
