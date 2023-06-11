package dev.scaraz.mars.common.utils.lambda;

import dev.scaraz.mars.common.utils.QueryBuilder;

@FunctionalInterface
public interface SpecificationJoinChain<E, Z> {

    void join();

}
