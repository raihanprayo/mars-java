package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import dev.scaraz.mars.common.tools.filter.ReadableRangeFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;

public class QueryFieldUtil {

    protected static <E, T> Specification<E> create(Filter<T> filter, SingularAttribute<? super E, T> attribute) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return Equals.spec(filter.getEq(), negated, attribute);
        if (filter.getIn() != null)
            return Inclusion.spec(filter.getIn(), negated, attribute);
        if (filter.getSpecified() != null)
            return Specified.spec(filter.getSpecified(), attribute);

        return null;
    }

    protected static <T, E, A1> Specification<E> create(
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return Equals.spec(filter.getEq(), negated, attr1, valAttr);
        if (filter.getIn() != null)
            return Inclusion.spec(filter.getIn(), negated, attr1, valAttr);
        if (filter.getSpecified() != null)
            return Specified.spec(filter.getSpecified(), attr1, valAttr);

        return null;
    }

    protected static <T, E, A1, A2> Specification<E> create(
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return Equals.spec(filter.getEq(), negated, attr1, attr2, valAttr);
        if (filter.getIn() != null)
            return Inclusion.spec(filter.getIn(), negated, attr1, attr2, valAttr);
        if (filter.getSpecified() != null)
            return Specified.spec(filter.getSpecified(), attr1, attr2, valAttr);

        return null;
    }

    protected static <T, E, A1> Specification<E> create(
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return Equals.spec(filter.getEq(), negated, attr1, valAttr);
        if (filter.getIn() != null)
            return Inclusion.spec(filter.getIn(), negated, attr1, valAttr);
        if (filter.getSpecified() != null)
            return Specified.spec(filter.getSpecified(), attr1, valAttr);

        return null;
    }

    protected static <T, E, A1, A2> Specification<E> create(
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return Equals.spec(filter.getEq(), negated, attr1, attr2, valAttr);
        if (filter.getIn() != null)
            return Inclusion.spec(filter.getIn(), negated, attr1, attr2, valAttr);
        if (filter.getSpecified() != null)
            return Specified.spec(filter.getSpecified(), attr1, attr2, valAttr);

        return null;
    }


    // Readable Filter
    protected static <E> Specification<E> createReadable(
            ReadableFilter<String> filter,
            SingularAttribute<? super E, String> attribute
    ) {
        Specification<E> spec = create(filter, attribute);
        if (spec == null) {
            if (filter.getLike() != null)
                return Like.spec(filter.getLike(), filter.isNegated(), attribute);
        }
        return spec;
    }

    protected static <E, A1> Specification<E> createReadable(
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, String> valAttr
    ) {
        Specification<E> spec = create(filter, attr1, valAttr);
        if (spec == null) {
            if (filter.getLike() != null)
                return Like.spec(filter.getLike(), filter.isNegated(), attr1, valAttr);
        }
        return spec;
    }

    protected static <E, A1, A2> Specification<E> createReadable(
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, String> valAttr
    ) {
        Specification<E> spec = create(filter, attr1, attr2, valAttr);
        if (spec == null) {
            if (filter.getLike() != null)
                return Like.spec(filter.getLike(), filter.isNegated(), attr1, attr2, valAttr);
        }
        return spec;
    }


    // Range Filter
    protected static <E, T extends Comparable<? super T>> Specification<E> createRange(
            RangeFilter<T> filter,
            SingularAttribute<? super E, T> attribute
    ) {
        Specification<E> spec = create(filter, attribute);
        if (spec == null) {
            spec = Specification.where(null);

            if (filter.getGt() != null) spec = spec.and(GreaterThan.spec(filter.getGt(), false, attribute));
            else if (filter.getGte() != null) spec = spec.and(GreaterThan.spec(filter.getGte(), true, attribute));

            if (filter.getLt() != null) spec = spec.and(LessThan.spec(filter.getLt(), false, attribute));
            else if (filter.getLte() != null) spec = spec.and(LessThan.spec(filter.getLte(), true, attribute));
        }
        return spec;
    }

    protected static <T extends Comparable<? super T>, E, A1> Specification<E> createRange(
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        Specification<E> spec = create(filter, attr1, valAttr);
        if (spec == null) {
            spec = Specification.where(null);

            if (filter.getGt() != null) spec.and(GreaterThan.spec(filter.getGt(), false, attr1, valAttr));
            else if (filter.getGte() != null) spec.and(GreaterThan.spec(filter.getGt(), true, attr1, valAttr));

            if (filter.getLt() != null) spec.and(LessThan.spec(filter.getLt(), false, attr1, valAttr));
            else if (filter.getLte() != null) spec.and(LessThan.spec(filter.getLte(), true, attr1, valAttr));
        }
        return spec;
    }

    protected static <T extends Comparable<? super T>, E, A1, A2> Specification<E> createRange(
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        Specification<E> spec = create(filter, attr1, attr2, valAttr);
        if (spec == null) {
            spec = Specification.where(null);

            if (filter.getGt() != null) spec.and(GreaterThan.spec(filter.getGt(), false, attr1, attr2, valAttr));
            else if (filter.getGte() != null) spec.and(GreaterThan.spec(filter.getGt(), true, attr1, attr2, valAttr));

            if (filter.getLt() != null) spec.and(LessThan.spec(filter.getLt(), false, attr1, attr2, valAttr));
            else if (filter.getLte() != null) spec.and(LessThan.spec(filter.getLte(), true, attr1, attr2, valAttr));
        }
        return spec;
    }

    public static class Equals {
        private static <T> Predicate equals(CriteriaBuilder b, Path<T> path, boolean negate, T value) {
            return negate ?
                    b.notEqual(path, value) :
                    b.equal(path, value);
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
    }

    public static class Inclusion {
        private static <T> Predicate inclusion(CriteriaBuilder b, Path<T> path, boolean negate, Collection<T> values) {
            CriteriaBuilder.In<T> in = b.in(path);
            for (T value : values) in.value(value);
            return negate ? in.not() : in;
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
    }

    public static class Like {
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

    public static class GreaterThan {

        private static <T extends Comparable<? super T>> Predicate greaterThan(CriteriaBuilder b, Path<T> path, boolean equality, T value) {
            return equality ?
                    b.greaterThanOrEqualTo(path, value) :
                    b.greaterThan(path, value);
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

    public static class LessThan {

        private static <T extends Comparable<? super T>> Predicate lessThan(CriteriaBuilder b, Path<T> path, boolean equality, T value) {
            return equality ?
                    b.lessThanOrEqualTo(path, value) :
                    b.lessThan(path, value);
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
                return lessThan(b, path, equality, value);
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
                return lessThan(b, path, equality, value);
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
                return lessThan(b, path, equality, value);
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
                return lessThan(b, path, equality, value);
            };
        }

        public static <T extends Comparable<? super T>, E> Specification<E> spec(
                T value,
                boolean equality,
                SingularAttribute<? super E, T> valAttr
        ) {
            return (r, q, b) -> {
                Path<T> path = r.get(valAttr);
                return lessThan(b, path, equality, value);
            };
        }
    }

    public interface Specified {
        private static <E> Predicate nullable(CriteriaBuilder b, Path<E> path, boolean specified) {
            return specified ? b.isNotNull(path) : b.isNull(path);
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
    }

}
