package dev.scaraz.mars.common.utils.lambda;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface PathSupplier<E, T> {
    Expression<T> apply(Root<E> root);

}
