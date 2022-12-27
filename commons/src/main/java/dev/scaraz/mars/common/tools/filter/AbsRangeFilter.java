package dev.scaraz.mars.common.tools.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbsRangeFilter<T extends Comparable<? super T>> extends AbsFilter<T> implements RangeFilter<T> {

    private T gt;
    private T gte;

    private T lt;
    private T lte;

    public AbsRangeFilter<T> setGt(T greaterThan) {
        this.gt = greaterThan;
        return this;
    }

    public AbsRangeFilter<T> setGte(T greaterThanEqual) {
        this.gte = greaterThanEqual;
        return this;
    }

    public AbsRangeFilter<T> setLt(T lessThan) {
        this.lt = lessThan;
        return this;
    }

    public AbsRangeFilter<T> setLte(T lessThanEqual) {
        this.lte = lessThanEqual;
        return this;
    }

    @Override
    public AbsRangeFilter<T> setEq(T value) {
        return (AbsRangeFilter<T>) super.setEq(value);
    }

    @Override
    public AbsRangeFilter<T> setIn(Collection<T> in) {
        return (AbsRangeFilter<T>) super.setIn(in);
    }

    @Override
    public AbsRangeFilter<T> setNullable(boolean nullable) {
        return (AbsRangeFilter<T>) super.setNullable(nullable);
    }
}
