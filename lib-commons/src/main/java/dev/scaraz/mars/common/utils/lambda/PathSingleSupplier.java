package dev.scaraz.mars.common.utils.lambda;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

@FunctionalInterface
public interface PathSingleSupplier<T, E, Z> {

    Expression<T> apply(Path<E> single);

    default PathSupplier<Z, T> compose(SingularAttribute<Z, E> attr) {
        return r -> this.apply(r.get(attr));
    }

}
