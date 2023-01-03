package dev.scaraz.mars.common.tools.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbsFilter<T> implements Filter<T> {

    protected T eq;
    protected Collection<T> in;
    protected boolean nullable = true;
    protected boolean negated = false;

    public AbsFilter<T> setEq(T value) {
        this.eq = value;
        return this;
    }

    public AbsFilter<T> setIn(Collection<T> in) {
        this.in = in;
        return this;
    }

    public AbsFilter<T> setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public AbsFilter<T> setNegated(boolean negated) {
        this.negated = negated;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("eq", eq)
                .append("in", in)
                .append("nullable", nullable)
                .append("negated", negated)
                .toString();
    }
}
