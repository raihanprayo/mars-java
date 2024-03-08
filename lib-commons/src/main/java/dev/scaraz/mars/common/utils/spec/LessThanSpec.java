package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class LessThanSpec {

    private static <T extends Comparable<? super T>> Predicate lessThan(CriteriaBuilder b, Expression<T> path, boolean equality, T value) {
        return equality ?
                b.lessThanOrEqualTo(path, value) :
                b.lessThan(path, value);
    }

    public static <T extends Comparable<? super T>, E> Specification<E> spec(
            T value,
            boolean equality,
            PathSupplier<E, T> targetPath
    ) {
        return (r, q, b) -> lessThan(b, targetPath.apply(r), equality, value);
    }

}
