package dev.scaraz.mars.common.utils.spec;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

public class LikeSpec {
    private static Predicate like(CriteriaBuilder b, Path<String> path, boolean negate, String wrappedLike) {
        return negate ?
                b.notLike(b.lower(path), wrappedLike) :
                b.like(b.lower(path), wrappedLike);
    }

    public static <E, A1, A2, A3, A4> Specification<E> spec(
            String value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, A4> attr4,
            SingularAttribute<? super A4, String> valAttr
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            Path<String> tPath = r.get(attr1).get(attr2).get(attr3).get(attr4)
                    .get(valAttr);
            return like(b, tPath, negate, wrappedLike);
        };
    }

    public static <E, A1, A2, A3> Specification<E> spec(
            String value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, String> valAttr
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            Path<String> tPath = r.get(attr1).get(attr2).get(attr3)
                    .get(valAttr);
            return like(b, tPath, negate, wrappedLike);
        };
    }

    public static <E, A1, A2> Specification<E> spec(
            String value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, String> valAttr
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            Path<String> tPath = r.get(attr1).get(attr2)
                    .get(valAttr);
            return like(b, tPath, negate, wrappedLike);
        };
    }

    public static <E, A1> Specification<E> spec(
            String value,
            boolean negate,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, String> valAttr
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            Path<String> tPath = r.get(attr1)
                    .get(valAttr);
            return like(b, tPath, negate, wrappedLike);
        };
    }

    public static <E> Specification<E> spec(
            String value,
            boolean negate,
            SingularAttribute<? super E, String> valAttr
    ) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            Path<String> tPath = r.get(valAttr);
            return like(b, tPath, negate, wrappedLike);
        };
    }

}
