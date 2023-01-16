package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;
import dev.scaraz.mars.common.tools.filter.Filter;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.List;

@Getter
public class EnumFilter<T extends Enum<? super T>> extends AbsRangeFilter<T> {

    @Override
    public EnumFilter<T> setGt(T greaterThan) {
        return (EnumFilter<T>) super.setGt(greaterThan);
    }

    @Override
    public EnumFilter<T> setGte(T greaterThanEqual) {
        return (EnumFilter<T>) super.setGte(greaterThanEqual);
    }

    @Override
    public EnumFilter<T> setLt(T lessThan) {
        return (EnumFilter<T>) super.setLt(lessThan);
    }

    @Override
    public EnumFilter<T> setLte(T lessThanEqual) {
        return (EnumFilter<T>) super.setLte(lessThanEqual);
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
    public EnumFilter<T> setSpecified(Boolean specified) {
        return (EnumFilter<T>) super.setSpecified(specified);
    }

    @Override
    public EnumFilter<T> setNegated(boolean negated) {
        return (EnumFilter<T>) super.setNegated(negated);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("eq", eq)
                .append("in", in)
                .append("nullable", specified)
                .append("negated", negated)
                .append("gt", gt)
                .append("gte", gte)
                .append("lt", lt)
                .append("lte", lte)
                .toString();
    }

}
