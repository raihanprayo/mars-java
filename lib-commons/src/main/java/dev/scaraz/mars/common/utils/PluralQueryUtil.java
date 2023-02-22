package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.utils.spec.EqualSpec;
import dev.scaraz.mars.common.utils.spec.InclusionSpec;
import dev.scaraz.mars.common.utils.spec.SpecifiedSpec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

public abstract class PluralQueryUtil {

    // Has Set Attribute
    protected static <T, E, A1> Specification<E> create(
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attr1, valAttr);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attr1, valAttr);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attr1, valAttr);

        return null;
    }

    protected static <T, E, A1, A2> Specification<E> create(
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attr1, attr2, valAttr);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attr1, attr2, valAttr);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attr1, attr2, valAttr);

        return null;
    }


    // Has List Attribute
    protected static <T, E, A1> Specification<E> create(
            Filter<T> filter,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attr1, valAttr);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attr1, valAttr);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attr1, valAttr);

        return null;
    }

    protected static <T, E, A1, A2> Specification<E> create(
            Filter<T> filter,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attr1, attr2, valAttr);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attr1, attr2, valAttr);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attr1, attr2, valAttr);

        return null;
    }
}
