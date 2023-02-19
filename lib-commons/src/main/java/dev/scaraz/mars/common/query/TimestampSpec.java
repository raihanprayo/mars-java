package dev.scaraz.mars.common.query;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.domain.TimestampEntity_;
import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.utils.QueryBuilder;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;

public abstract class TimestampSpec<E extends TimestampEntity, C extends TimestampCriteria> extends QueryBuilder<E, C> {

    protected Specification<E> timestampSpec(Specification<E> spec, C criteria) {
        if (criteria != null) {
            spec = nonNull(spec, criteria.getCreatedAt(), TimestampEntity_.createdAt);
            spec = nonNull(spec, criteria.getUpdatedAt(), TimestampEntity_.updatedAt);
        }

        return spec;
    }

    protected <S extends TimestampCriteria> Specification<E> timestampSpec(
            Specification<E> spec,
            SingularAttribute<? super E, ? extends TimestampEntity> join,
            S subCriteria
    ) {
        if (subCriteria != null) {
            spec = nonNull(spec, subCriteria.getCreatedAt(), join, TimestampEntity_.createdAt);
            spec = nonNull(spec, subCriteria.getUpdatedAt(), join, TimestampEntity_.updatedAt);
        }
        return spec;
    }

}
