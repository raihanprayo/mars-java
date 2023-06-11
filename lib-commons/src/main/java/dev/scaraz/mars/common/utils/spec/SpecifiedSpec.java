package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public interface SpecifiedSpec {

    private static <E> Predicate nullable(CriteriaBuilder b, Expression<E> path, boolean specified) {
        return specified ? b.isNotNull(path) : b.isNull(path);
    }

    static <T, E> Specification<E> spec(
            boolean specified,
            PathSupplier<E, T> targetPath
    ) {
        return (r, q, b) -> nullable(b, targetPath.apply(r), specified);
    }

}
