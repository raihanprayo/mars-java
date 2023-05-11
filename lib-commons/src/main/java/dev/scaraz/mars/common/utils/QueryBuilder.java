package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nullable;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class QueryBuilder<E, C extends Criteria> {

    abstract public Specification<E> createSpec(C criteria);

    protected <T> void consumeNonNull(@Nullable T o, Consumer<T> consume) {
        if (o != null) consume.accept(o);

    }

    // Plain Filter
    protected <T> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, T> attr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.create(filter, attr));
    }

    protected <T, A1> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.create(filter, attr1, valAttr));
    }

    protected <T, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.create(filter, attr1, attr2, valAttr));
    }

    protected <T, A1, A2, A3> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.create(filter, attr1, attr2, attr3, valAttr));
    }

    protected <T, A1> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(PluralQueryUtil.create(filter, attr1, valAttr));
    }

    protected <T, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            SetAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(PluralQueryUtil.create(filter, attr1, attr2, valAttr));
    }

    protected <T, A1> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(PluralQueryUtil.create(filter, attr1, valAttr));
    }

    protected <T, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            ListAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(PluralQueryUtil.create(filter, attr1, attr2, valAttr));
    }


    // Readable Filter
    protected Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, String> attr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createReadable(filter, attr));
    }

    protected <A1> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, String> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createReadable(filter, attr1, valAttr));
    }

    protected <A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, String> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createReadable(filter, attr1, attr2, valAttr));
    }

    protected <A1, A2, A3> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, String> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createReadable(filter, attr1, attr2, attr3, valAttr));
    }


    // Range Filter
    protected <T extends Comparable<? super T>> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, T> attr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createRange(filter, attr));
    }

    protected <T extends Comparable<? super T>, A1> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createRange(filter, attr1, valAttr));
    }

    protected <T extends Comparable<? super T>, A1, A2> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createRange(filter, attr1, attr2, valAttr));
    }

    protected <T extends Comparable<? super T>, A1, A2, A3> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<T> filter,
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> valAttr
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.createRange(filter, attr1, attr2, attr3, valAttr));
    }

    protected <T, E> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            Function<Root<E>, Expression<T>> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <E> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            Function<Root<E>, Expression<String>> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <E> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<String> filter,
            Function<Root<E>, Expression<String>> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <T, J> Specification<J> andNonNull(
            Specification<J> spec,
            Filter<T> filter,
            Function<Root<J>, Expression<T>> target
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.compose(filter, target));
    }

    protected <T, J> Specification<J> orNonNull(Specification<J> spec, Filter<T> filter, Function<Root<J>, Expression<T>> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.compose(filter, target));
    }

    protected <J> Specification<J> andNonNull(Specification<J> spec, ReadableFilter<String> filter, Function<Root<J>, Expression<String>> target) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.composeReadable(filter, target));
    }

    protected <J> Specification<J> orNonNull(Specification<J> spec, ReadableFilter<String> filter, Function<Root<J>, Expression<String>> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.composeReadable(filter, target));
    }

    protected <T extends Comparable<? super T>, J> Specification<J> andNonNull(Specification<J> spec, RangeFilter<T> filter, Function<Root<J>, Expression<T>> target) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.composeRange(filter, target));
    }

    protected <T extends Comparable<? super T>, J> Specification<J> orNonNull(Specification<J> spec, RangeFilter<T> filter, Function<Root<J>, Expression<T>> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.composeRange(filter, target));
    }

    protected <T, E> Function<Root<E>, Expression<T>> path(SingularAttribute<? super E, T> attr1) {
        return r -> r.get(attr1);
    }

    protected <T, E, A1> Function<Root<E>, Expression<T>> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> target
    ) {
        return r -> r.join(attr1).get(target);
    }

    protected <T, E, A1, A2> Function<Root<E>, Expression<T>> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> target
    ) {
        return r -> r.join(attr1).get(attr2).get(target);
    }

    protected <T, E, A1, A2, A3> Function<Root<E>, Expression<T>> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> target
    ) {
        return r -> r.join(attr1).get(attr2).get(attr3).get(target);
    }
}
