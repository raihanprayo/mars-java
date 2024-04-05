package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StringFilter extends AbsFilter<String> implements ReadableFilter<String> {

    private String like;

    public StringFilter setOpt(Opt opt) {
        super.setOpt(opt);
        return this;
    }

    public StringFilter setLike(String like) {
        this.like = like;
        return this;
    }

    @Override
    public StringFilter setEq(String value) {
        return (StringFilter) super.setEq(value);
    }

    @Override
    public StringFilter setIn(Collection<String> in) {
        return (StringFilter) super.setIn(in);
    }

    @Override
    public StringFilter setSpecified(Boolean specified) {
        return (StringFilter) super.setSpecified(specified);
    }

    @Override
    public StringFilter setNegated(boolean negated) {
        return (StringFilter) super.setNegated(negated);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("eq", eq)
                .append("in", in)
                .append("nullable", specified)
                .append("negated", negated)
                .append("like", like)
                .toString();
    }

}
