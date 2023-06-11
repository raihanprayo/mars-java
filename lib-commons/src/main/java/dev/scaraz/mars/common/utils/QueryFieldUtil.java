package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import dev.scaraz.mars.common.utils.spec.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;

public class QueryFieldUtil {

    protected static <E, T> Specification<E> create(Filter<T> filter, SingularAttribute<? super E, T> attribute) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attribute);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attribute);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attribute);

        return null;
    }

    protected static <T, E, A1> Specification<E> create(
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
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
            SingularAttribute<? super E, A1> attr1,
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

    protected static <T, E, A1, A2, A3> Specification<E> create(
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, attr1, attr2, attr3, valAttr);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, attr1, attr2, attr3, valAttr);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), attr1, attr2, attr3, valAttr);

        return null;
    }


    protected static <T, E> Specification<E> compose(
            Filter<T> filter,
            PathSupplier<E, T> targetPath
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return EqualSpec.spec(filter.getEq(), negated, targetPath);
        if (filter.getIn() != null)
            return InclusionSpec.spec(filter.getIn(), negated, targetPath);
        if (filter.getSpecified() != null)
            return SpecifiedSpec.spec(filter.getSpecified(), targetPath);

        return null;
    }


    protected static <T extends Comparable<? super T>, E> Specification<E> composeRange(
            RangeFilter<T> filter,
            PathSupplier<E, T> targetPath
    ) {
        Specification<E> spec = compose(filter, targetPath);
        if (spec == null) {
            spec = Specification.where(null);

            if (filter.getGt() != null)
                spec.and(GreaterThanSpec.spec(filter.getGt(), false, targetPath));
            else if (filter.getGte() != null)
                spec.and(GreaterThanSpec.spec(filter.getGt(), true, targetPath));

            if (filter.getLt() != null)
                spec.and(LessThanSpec.spec(filter.getLt(), false, targetPath));
            else if (filter.getLte() != null)
                spec.and(LessThanSpec.spec(filter.getLte(), true, targetPath));
        }
        return spec;
    }


    protected static <E> Specification<E> composeReadable(
            ReadableFilter<String> filter,
            PathSupplier<E, String> targetPath
    ) {
        Specification<E> spec = compose(filter, targetPath);
        if (spec == null) {
            if (filter.getLike() != null)
                return LikeSpec.spec(filter.getLike(), filter.isNegated(), targetPath);
        }
        return spec;
    }

}
