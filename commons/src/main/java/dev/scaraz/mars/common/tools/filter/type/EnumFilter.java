package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;

@Getter
public class EnumFilter<T extends Enum<? super T>> extends AbsRangeFilter<T> implements ReadableFilter<T> {

    @Getter
    private T like;

    private T gt;
    private T gte;

    private T lt;
    private T lte;

    public EnumFilter<T> setLike(T like) {
        this.like = like;
        return this;
    }

    @Override
    public EnumFilter<T> setGt(T greaterThan) {
        this.gt = greaterThan;
        return this;
    }

    @Override
    public EnumFilter<T> setGte(T greaterThanEqual) {
        this.gte = greaterThanEqual;
        return this;
    }

    @Override
    public EnumFilter<T> setLt(T lessThan) {
        this.lt = lessThan;
        return this;
    }

    @Override
    public EnumFilter<T> setLte(T lessThanEqual) {
        this.lte = lessThanEqual;
        return this;
    }

    @Override
    public EnumFilter<T> setEq(T value) {
        return (EnumFilter<T>) super.setEq(value);
    }

    @Override
    public EnumFilter<T> setIn(Collection<T> in) {
        return (EnumFilter<T>) super.setIn(in);
    }

    @Override
    public EnumFilter<T> setNullable(boolean nullable) {
        return (EnumFilter<T>) super.setNullable(nullable);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("eq", eq)
                .append("in", in)
                .append("nullable", nullable)
                .append("negated", negated)
                .append("like", like)
                .append("gt", gt)
                .append("gte", gte)
                .append("lt", lt)
                .append("lte", lte)
                .toString();
    }

}
