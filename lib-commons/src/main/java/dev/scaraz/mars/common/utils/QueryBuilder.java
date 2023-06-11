package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.Filter;
import dev.scaraz.mars.common.tools.filter.RangeFilter;
import dev.scaraz.mars.common.tools.filter.ReadableFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.lambda.PathPluralSupplier;
import dev.scaraz.mars.common.utils.lambda.PathSupplier;
import dev.scaraz.mars.common.utils.lambda.PluralSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nullable;
import javax.persistence.metamodel.*;
import java.util.Collection;
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

    protected SpecSingleChain<E> chain() {
        return new SpecSingleChain<>(this);
    }

    protected <Z, C extends Collection<E>> SpecJoinChain<E, Z, C> chain(
            Specification<Z> spec,
            PluralSupplier<E, Z, C> supplier
    ) {
        return new SpecJoinChain<>(spec, this, supplier);
    }

    public static class SpecSingleChain<E> {

        private final QueryBuilder<E, ? extends Criteria> qb;
        private Specification<E> spec;

        private SpecSingleChain(QueryBuilder<E, ? extends Criteria> qb) {
            this.qb = qb;
            this.spec = Specification.where(null);
        }

        public Specification<E> specification() {
            return spec;
        }

        public <T> SpecSingleChain<E> pick(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }
        public <T> SpecSingleChain<E> pick(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E> pick(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            return pick(filter, r -> r.get(attr));
        }
        public <T extends Comparable<? super T>> SpecSingleChain<E> pick(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public SpecSingleChain<E> pick(SingularAttribute<? super E, String> attr, StringFilter filter) {
            return pick(filter, r -> r.get(attr));
        }
        public SpecSingleChain<E> pick(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.nonNull(this.spec, filter, path);
            return this;
        }

        public <T> SpecSingleChain<E> and(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }
        public <T> SpecSingleChain<E> and(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }
        public SpecSingleChain<E> and(SingularAttribute<? super E, String> attr, StringFilter filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }
        public SpecSingleChain<E> and(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }
        public <T extends Comparable<? super T>> SpecSingleChain<E> and(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            this.spec = qb.andNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }
        public <T extends Comparable<? super T>> SpecSingleChain<E> and(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.andNonNull(this.spec, filter, path);
            return this;
        }


        public <T> SpecSingleChain<E> or(SingularAttribute<? super E, T> attr, Filter<T> filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T> SpecSingleChain<E> or(Filter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public SpecSingleChain<E> or(SingularAttribute<? super E, String> attr, StringFilter filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public SpecSingleChain<E> or(StringFilter filter, PathSupplier<E, String> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E> or(SingularAttribute<? super E, T> attr, RangeFilter<T> filter) {
            this.spec = qb.orNonNull(this.spec, filter, r -> r.get(attr));
            return this;
        }

        public <T extends Comparable<? super T>> SpecSingleChain<E> or(RangeFilter<T> filter, PathSupplier<E, T> path) {
            this.spec = qb.orNonNull(this.spec, filter, path);
            return this;
        }

        public SpecSingleChain<E> extend(Function<Specification<E>, Specification<E>> extend) {
            spec = extend.apply(spec);
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
