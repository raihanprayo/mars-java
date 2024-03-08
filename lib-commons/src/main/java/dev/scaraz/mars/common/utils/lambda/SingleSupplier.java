package dev.scaraz.mars.common.utils.lambda;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface SingleSupplier<E, Z> {

    Path<E> get(Root<Z> root);

}
