package dev.scaraz.mars.common.utils.lambda;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface SingleSupplier<E, Z> {

    Path<E> get(Root<Z> root);

}
