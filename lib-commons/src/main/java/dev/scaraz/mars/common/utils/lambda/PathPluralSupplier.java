package dev.scaraz.mars.common.utils.lambda;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.PluralJoin;
import java.util.Collection;

@FunctionalInterface
public interface PathPluralSupplier<T, E, Z, C extends Collection<? super E>> {

    Expression<T> apply(PluralJoin<Z, C, E> plural);

    default PathSupplier<Z, T> compose(PluralSupplier<E, Z, C> plural) {
        return r -> this.apply(plural.join(r));
    }

}
