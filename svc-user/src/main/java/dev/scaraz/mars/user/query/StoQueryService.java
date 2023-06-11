package dev.scaraz.mars.user.query;

import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.web.criteria.StoCriteria;

public interface StoQueryService extends BaseQueryService<Sto, StoCriteria> {
    Sto findByIdOrName(String nameOrAlias);
}
