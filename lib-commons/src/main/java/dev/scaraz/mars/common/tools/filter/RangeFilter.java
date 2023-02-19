package dev.scaraz.mars.common.tools.filter;

import java.util.Collection;

public interface RangeFilter<T extends Comparable<? super T>> extends Filter<T> {
    RangeFilter<T> setGt(T greaterThan);

    RangeFilter<T> setGte(T greaterThanEqual);

    RangeFilter<T> setLt(T lessThan);

    RangeFilter<T> setLte(T lessThanEqual);

    T getGt();
    T getGte();
    T getLt();
    T getLte();

}
