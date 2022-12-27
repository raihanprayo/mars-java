package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import dev.scaraz.mars.common.tools.filter.ReadableRangeFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class EntityFieldUtil {

    public static <E, T> Specification<E> equalSpec(T value, String attribute, boolean negate) {
        return (r, q, b) -> {
            Path<T> path = r.get(attribute);
            return negate ?
                    b.notEqual(path, value) :
                    b.equal(path, value);
        };
    }

    public static <E, T> Specification<E> inclusionSpec(Collection<T> values, String attribute, boolean negate) {
        return (r, q, b) -> {
            CriteriaBuilder.In<T> in = b.in(r.get(attribute));
            for (T value : values) in.value(value);
            return negate ? in.not() : in;
        };
    }

    public static <E, T> Specification<E> likeSpec(T value, String attribute, boolean negate) {
        String wrappedLike = ("%" + value + "%").toLowerCase();
        return (r, q, b) -> {
            return negate ?
                    b.notLike(b.lower(r.get(attribute)), wrappedLike) :
                    b.like(b.lower(r.get(attribute)), wrappedLike);
        };
    }

    public static <E, T extends Comparable> Specification<E> gtSpec(T value, String attribute, boolean equality) {
        return (r, q, b) -> equality ?
                b.greaterThanOrEqualTo(r.get(attribute), value) :
                b.greaterThan(r.get(attribute), value);
    }

    public static <E, T extends Comparable> Specification<E> ltSpec(T value, String attribute, boolean equality) {
        return (r, q, b) -> equality ?
                b.lessThanOrEqualTo(r.get(attribute), value) :
                b.lessThan(r.get(attribute), value);
    }

}
