package dev.scaraz.mars.common.query;

import dev.scaraz.mars.common.domain.TimestampEntity;
import dev.scaraz.mars.common.domain.TimestampEntity_;
import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.common.utils.lambda.PluralSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;

public abstract class TimestampSpec<E extends TimestampEntity, C extends TimestampCriteria> extends QueryBuilder<E, C> {

    protected Specification<E> timestampSpec(Specification<E> spec, C criteria) {
        if (criteria != null) {
            spec = andNonNull(spec, criteria.getCreatedAt(), path(TimestampEntity_.createdAt));
            spec = andNonNull(spec, criteria.getUpdatedAt(), path(TimestampEntity_.updatedAt));
        }

        return spec;
    }

    protected <S extends TimestampCriteria> Specification<E> timestampSpec(
            Specification<E> spec,
            SingularAttribute<? super E, ? extends TimestampEntity> join,
            S subCriteria
    ) {
        if (subCriteria != null) {
            spec = andNonNull(spec, subCriteria.getCreatedAt(), path(join, TimestampEntity_.createdAt));
            spec = andNonNull(spec, subCriteria.getUpdatedAt(), path(join, TimestampEntity_.updatedAt));
        }
        return spec;
    }

    protected <Z, CZ extends TimestampCriteria, C extends Collection<? super E>> Specification<Z> timestampSpec(
            Specification<Z> spec,
            CZ subCriteria,
            PluralSupplier<E, Z, C> join
    ) {
        if (subCriteria != null) {
            spec = andNonNull(spec, subCriteria.getCreatedAt(), join.single(TimestampEntity_.createdAt));
            spec = andNonNull(spec, subCriteria.getUpdatedAt(), join.single(TimestampEntity_.updatedAt));
        }
        return spec;
    }

}
