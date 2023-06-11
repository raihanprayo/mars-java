package dev.scaraz.mars.common.utils.lambda;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface PathSupplier<E, T> {
    Expression<T> apply(Root<E> root);

}
