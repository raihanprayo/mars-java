package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@Slf4j
public abstract class QueryBuilder<E, C extends Criteria> {

    abstract public Specification<E> createSpec(C criteria);

    // Plain Filter
    protected <T> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, T> attr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.create(filter, attr);
    }

    protected <T, A1> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.create(filter, attr1, valAttr);
    }

    protected <T, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.create(filter, attr1, attr2, valAttr);
    }

    protected <T, A1> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.create(filter, attr1, valAttr);
    }

    protected <T, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.create(filter, attr1, attr2, valAttr);
    }


    // Readable Filter
    protected Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, String> attr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createReadable(filter, attr);
    }

    protected <A1> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, String> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createReadable(filter, attr1, valAttr);
    }

    protected <A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, String> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createReadable(filter, attr1, attr2, valAttr);
    }


    // Range Filter
    protected <T extends Comparable<? super T>> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, T> attr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createRange(filter, attr);
    }

    protected <T extends Comparable<? super T>, A1> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createRange(filter, attr1, valAttr);
    }

    protected <T extends Comparable<? super T>, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return QueryFieldUtil.createRange(filter, attr1, attr2, valAttr);
    }

}
