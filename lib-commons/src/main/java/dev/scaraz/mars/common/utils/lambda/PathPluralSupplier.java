package dev.scaraz.mars.common.utils.lambda;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.PluralJoin;
import java.util.Collection;

@FunctionalInterface
public interface PathPluralSupplier<T, E, Z, C extends Collection<? super E>> {

    Expression<T> apply(PluralJoin<Z, C, E> plural);

    default PathSupplier<Z, T> compose(PluralSupplier<E, Z, C> plural) {
        return r -> this.apply(plural.join(r));
    }

}
