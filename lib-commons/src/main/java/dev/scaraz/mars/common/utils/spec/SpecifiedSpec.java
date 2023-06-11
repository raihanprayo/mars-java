package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public interface SpecifiedSpec {
    private static <E> Predicate nullable(CriteriaBuilder b, Path<E> path, boolean specified) {
        return specified ? b.isNotNull(path) : b.isNull(path);
    }
    private static <E> Predicate nullable(CriteriaBuilder b, Expression<E> path, boolean specified) {
        return specified ? b.isNotNull(path) : b.isNull(path);
    }

    static <T, E> Specification<E> spec(
            boolean specified,
            PathSupplier<E, T> targetPath
    ) {
        return (r, q, b) -> nullable(b, targetPath.apply(r), specified);
    }

    static <T, E, A1, A2, A3, A4> Specification<E> spec(
            boolean specified,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, A4> attr4,
            SingularAttribute<? super A4, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3).get(attr4)
                    .get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E, A1, A2, A3> Specification<E> spec(
            boolean specified,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3)
                    .get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E, A1, A2> Specification<E> spec(
            boolean specified,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2)
                    .get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E, A1> Specification<E> spec(
            boolean specified,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E> Specification<E> spec(
            boolean specified,
            SingularAttribute<? super E, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(valAttr);
            return nullable(b, path, specified);
        };
    }


    static <T, E, A1, A2> Specification<E> spec(
            boolean specified,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(attr2)
                    .get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E, A1> Specification<E> spec(
            boolean specified,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(valAttr);
            return nullable(b, path, specified);
        };
    }


    static <T, E, A1, A2> Specification<E> spec(
            boolean specified,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(attr2)
                    .get(valAttr);
            return nullable(b, path, specified);
        };
    }

    static <T, E, A1> Specification<E> spec(
            boolean specified,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(valAttr);
            return nullable(b, path, specified);
        };
    }
}
