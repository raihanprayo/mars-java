package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class EqualSpec {
    private static <T> Predicate equals(CriteriaBuilder b, Path<T> path, boolean negate, T value) {
        return negate ?
                b.notEqual(path, value) :
                b.equal(path, value);
    }

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

    public static <T, E, A1, A2, A3, A4> Specification<E> spec(
            T value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, A4> attr4,
            SingularAttribute<? super A4, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3).get(attr4)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E, A1, A2, A3> Specification<E> spec(
            T value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E, A1, A2> Specification<E> spec(
            T value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E, A1> Specification<E> spec(
            T value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E> Specification<E> spec(
            T value,
            boolean negate,
            SingularAttribute<? super E, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    // Has Set Attribute
    public static <T, E, A1, A2> Specification<E> spec(
            T value,
            boolean negate,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(attr2)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E, A1> Specification<E> spec(
            T value,
            boolean negate,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    // Has List Attribute
    public static <T, E, A1, A2> Specification<E> spec(
            T value,
            boolean negate,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1).get(attr2)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }

    public static <T, E, A1> Specification<E> spec(
            T value,
            boolean negate,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.join(attr1)
                    .get(valAttr);
            return equals(b, path, negate, value);
        };
    }
}
