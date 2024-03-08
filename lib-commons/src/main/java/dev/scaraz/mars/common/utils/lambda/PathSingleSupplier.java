package dev.scaraz.mars.common.utils.lambda;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.SingularAttribute;

@FunctionalInterface
public interface PathSingleSupplier<T, E, Z> {

    Expression<T> apply(Path<E> single);

    default PathSupplier<Z, T> compose(SingularAttribute<Z, E> attr) {
        return r -> this.apply(r.get(attr));
    }

}
