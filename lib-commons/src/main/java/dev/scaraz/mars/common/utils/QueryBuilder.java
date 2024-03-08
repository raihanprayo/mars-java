package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.lambda.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.*;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public abstract class QueryBuilder<E, C extends Criteria> {

    abstract public Specification<E> createSpec(C criteria);

    protected <T, E> Specification<E> nonNull(
            Specification<E> spec,
            Filter<T> filter,
            PathSupplier<E, T> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <E> Specification<E> nonNull(
            Specification<E> spec,
            ReadableFilter<String> filter,
            PathSupplier<E, String> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <E> Specification<E> nonNull(
            Specification<E> spec,
            RangeFilter<String> filter,
            PathSupplier<E, String> target
    ) {
        if (filter == null) return spec;

        Filter.Opt opt = Objects.requireNonNullElse(filter.getOpt(), Filter.Opt.AND);
        if (opt == Filter.Opt.OR) return orNonNull(spec, filter, target);
        return andNonNull(spec, filter, target);
    }

    protected <T, J> Specification<J> andNonNull(
            Specification<J> spec,
            Filter<T> filter,
            PathSupplier<J, T> target
    ) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.compose(filter, target));
    }

    protected <T, J> Specification<J> orNonNull(Specification<J> spec, Filter<T> filter, PathSupplier<J, T> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.compose(filter, target));
    }

    protected <J> Specification<J> andNonNull(Specification<J> spec, ReadableFilter<String> filter, PathSupplier<J, String> target) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.composeReadable(filter, target));
    }

    protected <J> Specification<J> orNonNull(Specification<J> spec, ReadableFilter<String> filter, PathSupplier<J, String> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.composeReadable(filter, target));
    }

    protected <T extends Comparable<? super T>, J> Specification<J> andNonNull(Specification<J> spec, RangeFilter<T> filter, PathSupplier<J, T> target) {
        if (filter == null) return spec;
        return spec.and(QueryFieldUtil.composeRange(filter, target));
    }

    protected <T extends Comparable<? super T>, J> Specification<J> orNonNull(Specification<J> spec, RangeFilter<T> filter, PathSupplier<J, T> target) {
        if (filter == null) return spec;
        return spec.or(QueryFieldUtil.composeRange(filter, target));
    }

    protected <T, E> PathSupplier<E, T> path(SingularAttribute<? super E, T> attr1) {
        return r -> r.get(attr1);
    }

    protected <T, E, A1> PathSupplier<E, T> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, T> target
    ) {
        return r -> r.join(attr1).get(target);
    }

    protected <T, E, A1, A2> PathSupplier<E, T> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, T> target
    ) {
        return r -> r.join(attr1).get(attr2).get(target);
    }

    protected <T, E, A1, A2, A3> PathSupplier<E, T> path(
            SingularAttribute<? super E, A1> attr1,
            SingularAttribute<? super A1, A2> attr2,
            SingularAttribute<? super A2, A3> attr3,
            SingularAttribute<? super A3, T> target
    ) {
        return r -> r.join(attr1).get(attr2).get(attr3).get(target);
    }

    protected <T, Z, C extends Collection<E>> PathSupplier<Z, T> path(
            PluralSupplier<E, Z, C> plural,
            SingularAttribute<E, T> attr
    ) {
        return r -> plural.join(r).get(attr);
    }

    protected SpecChain<E> chain() {
        return new SpecChain<>(this);
    }

    protected <Z> SpecSingleChain<E, Z> chain(Specification<Z> spec, SingularAttribute<Z, E> attr) {
        return new SpecSingleChain<>(spec, attr, this);
    }

    protected <Z, C extends Collection<E>> SpecJoinChain<E, Z, C> chain(
            Specification<Z> spec,
            PluralSupplier<E, Z, C> supplier
    ) {
        return new SpecJoinChain<>(spec, this, supplier);
    }

    public static class SpecChain<E> {

        private final QueryBuilder<E, ? extends Criteria> qb;
        private Specification<E> spec;

        private SpecChain(QueryBuilder<E, ? extends Criteria> qb) {
            this.qb = qb;
            this.spec = Specification.where(null);
        }

        public Specification<E> specification() {
            return spec;
        }

        public <T> SpecChain<E> pick(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }

        public <T> SpecChain<E> pick(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public <T extends Comparable<? super T>> SpecChain<E> pick(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }

        public <T extends Comparable<? super T>> SpecChain<E> pick(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public SpecChain<E> pick(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return pick(filter, r -> r.get(attr));
        }

        public SpecChain<E> pick(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public <T> SpecChain<E> and(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T> SpecChain<E> and(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }

        public SpecChain<E> and(SingularAttribute<? super E, String> attr, StringFilter filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public SpecChain<E> and(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }

        public <T extends Comparable<? super T>> SpecChain<E> and(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecChain<E> and(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }


        public <T> SpecChain<E> or(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T> SpecChain<E> or(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public SpecChain<E> or(SingularAttribute<? super E, String> attr, StringFilter filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public SpecChain<E> or(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public <T extends Comparable<? super T>> SpecChain<E> or(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecChain<E> or(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public SpecChain<E> extend(Function<Specification<E>, Specification<E>> extend) {
            spec = extend.apply(spec);
            return this;
        }
    }

    public static class SpecSingleChain<E, Z> {

        private final QueryBuilder<E, ? extends Criteria> qb;
        private final SingularAttribute<Z, E> attr;
        private Specification<Z> spec;

        private SpecSingleChain(Specification<Z> spec,
                                SingularAttribute<Z, E> attr,
                                QueryBuilder<E, ? extends Criteria> qb) {
            this.qb = qb;
            this.attr = attr;
            this.spec = spec;
        }

        public Specification<Z> specification() {
            return spec;
        }

        public <T> SpecSingleChain<E, Z> pick(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }

        public <T> SpecSingleChain<E, Z> pick(Filter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.nonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> pick(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> pick(RangeFilter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.nonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public SpecSingleChain<E, Z> pick(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return pick(filter, r -> r.get(attr));
        }

        public SpecSingleChain<E, Z> pick(StringFilter filter, PathSingleSupplier<String, E, Z> path) {
            this.spec = qb.nonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public <T> SpecSingleChain<E, Z> and(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(this.attr).get(attr));
            return this;
        }

        public <T> SpecSingleChain<E, Z> and(Filter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public SpecSingleChain<E, Z> and(SingularAttribute<? super E, String> attr, StringFilter filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(this.attr).get(attr));
            return this;
        }

        public SpecSingleChain<E, Z> and(StringFilter filter, PathSingleSupplier<String, E, Z> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> and(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(this.attr).get(attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> and(RangeFilter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }


        public <T> SpecSingleChain<E, Z> or(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return this.or(filter, r -> r.get(attr));
        }

        public <T> SpecSingleChain<E, Z> or(Filter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public SpecSingleChain<E, Z> or(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return this.or(filter, r -> r.get(attr));
        }

        public SpecSingleChain<E, Z> or(StringFilter filter, PathSingleSupplier<String, E, Z> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> or(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return this.or(filter, r -> r.get(attr));
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E, Z> or(RangeFilter<T> filter, PathSingleSupplier<T, E, Z> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(this.attr));
            return this;
        }

        public SpecSingleChain<E, Z> extend(
                BiFunction<Specification<Z>, SingleSupplier<E, Z>, Specification<Z>> extend
        ) {
            spec = extend.apply(spec, r -> r.get(this.attr));
            return this;
        }

    }

    public static class SpecJoinChain<E, Z, C extends Collection<E>> {

        private final PluralSupplier<E, Z, C> join;
        private final QueryBuilder<E, ? extends Criteria> qb;
        private Specification<Z> spec;

        private SpecJoinChain(
                Specification<Z> spec,
                QueryBuilder<E, ? extends Criteria> qb,
                PluralSupplier<E, Z, C> join
        ) {
            this.qb = qb;
            this.spec = spec;
            this.join = join;
        }

        public <T> SpecJoinChain<E, Z, C> and(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public <T> SpecJoinChain<E, Z, C> and(Filter<T> filter, PathPluralSupplier<T, E, Z, C> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(join));
            return this;
        }


        public SpecJoinChain<E, Z, C> and(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public SpecJoinChain<E, Z, C> and(StringFilter filter, PathPluralSupplier<String, E, Z, C> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(join));
            return this;
        }


        public <T extends Comparable<? super T>> SpecJoinChain<E, Z, C> and(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public <T extends Comparable<? super T>> SpecJoinChain<E, Z, C> and(RangeFilter<T> filter, PathPluralSupplier<T, E, Z, C> path) {
            this.spec = qb.andNonNull(this.spec, filter, path.compose(join));
            return this;
        }


        public <T> SpecJoinChain<E, Z, C> or(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public <T> SpecJoinChain<E, Z, C> or(Filter<T> filter, PathPluralSupplier<T, E, Z, C> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(join));
            return this;
        }

        public SpecJoinChain<E, Z, C> or(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public SpecJoinChain<E, Z, C> or(StringFilter filter, PathPluralSupplier<String, E, Z, C> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(join));
            return this;
        }

        public <T extends Comparable<? super T>> SpecJoinChain<E, Z, C> or(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return this.and(filter, plural -> plural.get(attr));
        }

        public <T extends Comparable<? super T>> SpecJoinChain<E, Z, C> or(RangeFilter<T> filter, PathPluralSupplier<T, E, Z, C> path) {
            this.spec = qb.orNonNull(this.spec, filter, path.compose(join));
            return this;
        }

        public SpecJoinChain<E, Z, C> extend(Function<Specification<Z>, Specification<Z>> extend) {
            spec = extend.apply(spec);
            return this;
        }

        public Specification<Z> specification() {
            return this.spec;
        }
    }

}
