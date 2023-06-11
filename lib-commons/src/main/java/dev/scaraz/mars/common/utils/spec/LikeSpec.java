package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class LikeSpec {

    private static Predicate like(CriteriaBuilder b, Expression<String> path, boolean negate, String wrappedLike) {
        return negate ?
                b.notLike(b.lower(path), wrappedLike) :
                b.like(b.lower(path), wrappedLike);
    }

    public static <E> Specification<E> spec(
            String value,
            boolean negate,
            PathSupplier<E, String> targetPath
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> like(b, targetPath.apply(r), negate, wrappedLike);
    }

}
