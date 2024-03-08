package dev.scaraz.mars.common.utils.lambda;

import jakarta.persistence.criteria.PluralJoin;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collection;

@FunctionalInterface
public interface PluralSupplier<E, Z, C extends Collection<? super E>> {

    PluralJoin<Z, C, E> join(Root<Z> root);

    default <T> PathSupplier<Z, T> single(SingularAttribute<? super E, T> attr) {
        return r -> this.join(r).get(attr);
    }

}
