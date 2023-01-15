package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;

public interface GroupQueryService extends BaseQueryService<Group, GroupCriteria> {
    Group findByIdOrName(String idOrName);
}
