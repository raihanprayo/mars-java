package dev.scaraz.mars.common.utils.spec;

import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.function.Function;

public class InclusionSpec {
    private static <T> Predicate inclusion(CriteriaBuilder b, Path<T> path, boolean negate, Collection<T> values) {
        CriteriaBuilder.In<T> in = b.in(path);

        for (T value : values)
            in.value(value);

        return negate ? in.not() : in;
    }

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

    public static <T, E, A1, A2, A3, A4> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, A4> attr4,
            SingularAttribute<? super A4, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.get(attr1).get(attr2).get(attr3).get(attr4)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E, A1, A2, A3> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.get(attr1).get(attr2).get(attr3)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E, A1, A2> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.get(attr1).get(attr2)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E, A1> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.get(attr1)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SingularAttribute<? super E, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }


    public static <T, E, A1> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.join(attr1)
                    .get(valAttr);

            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E, A1, A2> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.join(attr1).get(attr2)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }


    public static <T, E, A1> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.join(attr1)
                    .get(valAttr);

            return inclusion(b, tPath, negate, values);
        };
    }

    public static <T, E, A1, A2> Specification<E> spec(
            Collection<T> values,
            boolean negate,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr) {
        return (r, q, b) -> {
            Path<T> tPath = r.join(attr1).get(attr2)
                    .get(valAttr);
            return inclusion(b, tPath, negate, values);
        };
    }
}
