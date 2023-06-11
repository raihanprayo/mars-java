package dev.scaraz.mars.common.query;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.domain.AuditableEntity_;
import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.utils.lambda.PluralSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;

public abstract class AuditableSpec<E extends AuditableEntity, C extends AuditableCriteria> extends TimestampSpec<E, C> {

    protected Specification<E> auditSpec(Specification<E> spec, C criteria) {
        if (criteria != null) {
            spec = andNonNull(spec, criteria.getCreatedBy(), path(AuditableEntity_.updatedBy));
            spec = andNonNull(spec, criteria.getCreatedBy(), path(AuditableEntity_.createdBy));
        }

        return timestampSpec(spec, criteria);
    }

    protected <S extends AuditableCriteria> Specification<E> auditSpec(
            Specification<E> spec,
            SingularAttribute<? super E, ? extends AuditableEntity> join,
            S subCriteria
    ) {
        if (subCriteria != null) {
            spec = andNonNull(spec, subCriteria.getCreatedAt(), path(join, AuditableEntity_.createdAt));
            spec = andNonNull(spec, subCriteria.getUpdatedAt(), path(join, AuditableEntity_.updatedAt));
        }
        return timestampSpec(spec, join, subCriteria);
    }

    protected <Z, CZ extends AuditableCriteria, C extends Collection<? super E>>
    Specification<Z> auditSpec(Specification<Z> spec, CZ subCriteria, PluralSupplier<E, Z, C> join) {
        if (subCriteria != null) {
            spec = andNonNull(spec, subCriteria.getCreatedAt(), join.single(AuditableEntity_.createdAt));
            spec = andNonNull(spec, subCriteria.getUpdatedAt(), join.single(AuditableEntity_.updatedAt));
        }
        return timestampSpec(spec, subCriteria, join);
    }

}
