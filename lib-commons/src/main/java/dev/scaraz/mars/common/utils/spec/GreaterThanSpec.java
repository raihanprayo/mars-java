package dev.scaraz.mars.common.utils.spec;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class GreaterThanSpec {

    private static <T extends Comparable<? super T>> Predicate greaterThan(CriteriaBuilder b, Path<T> path, boolean equality, T value) {
        return equality ?
                b.greaterThanOrEqualTo(path, value) :
                b.greaterThan(path, value);
    }

    private static <T extends Comparable<? super T>> Predicate greaterThan(
            CriteriaBuilder b,
            Expression<T> path,
            boolean equality,
            T value
    ) {
        return equality ?
                b.greaterThanOrEqualTo(path, value) :
                b.greaterThan(path, value);
    }

    public static <T extends Comparable<? super T>, E> Specification<E> spec(
            T value,
            boolean equality,
            Function<Root<E>, Expression<T>> targetPath
    ) {
        return (r, q, b) -> greaterThan(b, targetPath.apply(r), equality, value);
    }

    public static <T extends Comparable<? super T>, E, A1, A2, A3, A4> Specification<E> spec(
            T value,
            boolean equality,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, A4> attr4,
            SingularAttribute<? super A4, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3).get(attr4)
                    .get(valAttr);
            return greaterThan(b, path, equality, value);
        };
    }

    public static <T extends Comparable<? super T>, E, A1, A2, A3> Specification<E> spec(
            T value,
            boolean equality,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2).get(attr3)
                    .get(valAttr);
            return greaterThan(b, path, equality, value);
        };
    }

    public static <T extends Comparable<? super T>, E, A1, A2> Specification<E> spec(
            T value,
            boolean equality,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1).get(attr2)
                    .get(valAttr);
            return greaterThan(b, path, equality, value);
        };
    }

    public static <T extends Comparable<? super T>, E, A1> Specification<E> spec(
            T value,
            boolean equality,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(attr1)
                    .get(valAttr);
            return greaterThan(b, path, equality, value);
        };
    }

    public static <T extends Comparable<? super T>, E> Specification<E> spec(
            T value,
            boolean equality,
            SingularAttribute<? super E, T> valAttr
    ) {
        return (r, q, b) -> {
            Path<T> path = r.get(valAttr);
            return greaterThan(b, path, equality, value);
        };
    }
}
