package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class InclusionSpec {

    private static <T> Predicate inclusion(
            CriteriaBuilder b,
            Expression<T> path,
            boolean negate,
            Collection<T> values
    ) {
        CriteriaBuilder.In<T> in = b.in(path);
        for (T value : values)
            in.value(value);

        return negate ? in.not() : in;
    }

    public static <T, E> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            PathSupplier<E, T> targetPath) {
        return (r, q, b) -> inclusion(b, targetPath.apply(r), negate, values);
    }

}
