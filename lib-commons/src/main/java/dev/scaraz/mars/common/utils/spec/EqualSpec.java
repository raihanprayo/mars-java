package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class EqualSpec {

    private static <T> Predicate equals(CriteriaBuilder b, Expression<T> path, boolean negate, T value) {
        return negate ?
                b.notEqual(path, value) :
                b.equal(path, value);
    }

    public static <T, E> Specification<E> spec(
            T value,
            boolean negate,
            PathSupplier<E, T> targetPath
    ) {
        return (r, q, b) -> equals(b, targetPath.apply(r), negate, value);
    }

}
