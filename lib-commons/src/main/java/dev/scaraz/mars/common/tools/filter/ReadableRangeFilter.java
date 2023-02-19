package dev.scaraz.mars.common.tools.filter;

public interface ReadableRangeFilter<T extends Comparable<? super T>> extends ReadableFilter<T>, RangeFilter<T> {
    @Override
    ReadableRangeFilter<T> setGt(T greaterThan);

    @Override
    ReadableRangeFilter<T> setGte(T greaterThanEqual);

    @Override
    ReadableRangeFilter<T> setLt(T lessThan);

    @Override
    ReadableRangeFilter<T> setLte(T lessThanEqual);

    @Override
    ReadableRangeFilter<T> setLike(T like);
}
