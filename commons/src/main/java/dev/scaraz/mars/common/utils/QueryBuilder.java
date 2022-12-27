package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.CriteriaFieldMetadata;
import dev.scaraz.mars.common.tools.annotation.CriteriaField;
import dev.scaraz.mars.common.tools.filter.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static dev.scaraz.mars.common.utils.EntityFieldUtil.*;

@Slf4j
public class QueryBuilder {

    protected <E> Specification<E> create(Filter<?> filter, String attribute) {
        boolean negated = filter.isNegated();

        if (filter.getEq() != null)
            return equalSpec(filter.getEq(), attribute, negated);
        if (filter.getIn() != null)
            return inclusionSpec(filter.getIn(), attribute, negated);

        return null;
    }

    protected <E> Specification<E> createReadable(ReadableFilter<?> filter, String attribute) {
        Specification<E> spec = create(filter, attribute);
        if (spec == null) {
            if (filter.getLike() != null)
                return likeSpec(filter.getLike(), attribute, filter.isNegated());
        }
        return spec;
    }

    protected <E> Specification<E> createRange(RangeFilter<?> filter, String attribute) {
        Specification<E> spec = create(filter, attribute);
        if (spec == null) {
            spec = Specification.where(null);

            if (filter.getGt() != null) spec.and(gtSpec(filter.getGt(), attribute, false));
            else if (filter.getGte() != null) spec.and(gtSpec(filter.getGte(), attribute, true));

            if (filter.getLt() != null) spec.and(ltSpec(filter.getLt(), attribute, false));
            else if (filter.getLte() != null) spec.and(ltSpec(filter.getLte(), attribute, true));
        }
        return spec;
    }

    protected <E> Specification<E> createReadableRange(ReadableRangeFilter<?> filter, String attribute) {
        return Specification.<E>where(null)
                .and(createReadable(filter, attribute))
                .and(createRange(filter, attribute));
    }

    protected <E, C extends Criteria> Specification<E> createSpecification(C criteria) {
        Specification<E> spec = Specification.where(null);

        Set<CriteriaFieldMetadata> mds = travelCriteria((Class<Criteria>) criteria.getClass(), criteria);
        log.debug("CRITERIA FIELD COUNT {}", mds.size());
        log.debug("CRITERIA FIELDS {}", mds);

        for (CriteriaFieldMetadata md : mds) {
            String attribute = md.getAttribute();
            Filter<?> filter = md.getFilter();

            Class<? extends Filter> filterClass = filter.getClass();
            if (ReadableRangeFilter.class.isAssignableFrom(filterClass)) {
                spec = spec.and(createReadableRange((ReadableRangeFilter<?>) filter, attribute));
            }
            else if (ReadableFilter.class.isAssignableFrom(filterClass)) {
                spec = spec.and(createReadable((ReadableFilter<?>) filter, attribute));
            }
            else if (RangeFilter.class.isAssignableFrom(filterClass)) {
                spec = spec.and(createRange((RangeFilter<?>) filter, attribute));
            }
            else {
                spec = spec.and(create(filter, attribute));
            }
        }
        return spec;
    }

    protected Set<CriteriaFieldMetadata> travelCriteria(Class<Criteria> clazz, Criteria criteria) {
        Set<CriteriaFieldMetadata> mds = new HashSet<>();
        if (criteria == null) return mds;

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();

            Object value = getFieldValue(field, criteria);
            if (value == null) continue;

            String attribute = getAttribute(field);
            if (Filter.class.isAssignableFrom(fieldType)) {
                mds.add(CriteriaFieldMetadata.builder()
                        .depth(attribute.split("\\.").length)
                        .attribute(attribute)
                        .filter((Filter<?>) value)
                        .build());
            }
            else if (Criteria.class.isAssignableFrom(fieldType)) {
                Criteria subCriteria = (Criteria) value;
                mds.addAll(travelCriteria(attribute, (Class<Criteria>) subCriteria.getClass(), subCriteria));
            }
        }

        Class<? super Criteria> superclass = clazz.getSuperclass();
        if (Criteria.class.isAssignableFrom(superclass)) {
            mds.addAll(travelCriteria((Class<Criteria>) superclass, criteria));
        }

        return mds;
    }

    protected Set<CriteriaFieldMetadata> travelCriteria(String prefix, Class<Criteria> clazz, Criteria criteria) {
        Set<CriteriaFieldMetadata> mds = new HashSet<>();
        if (criteria == null) return mds;

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();

            Object value = getFieldValue(field, criteria);
            if (value == null) continue;

            String attribute = String.join(".", prefix, getAttribute(field));

            if (Filter.class.isAssignableFrom(fieldType)) {
                mds.add(CriteriaFieldMetadata.builder()
                        .depth(attribute.split("\\.").length)
                        .attribute(attribute)
                        .filter((Filter<?>) value)
                        .build());
            }
            else if (Criteria.class.isAssignableFrom(fieldType)) {
                Criteria subCriteria = (Criteria) value;
                mds.addAll(travelCriteria(attribute, (Class<Criteria>) subCriteria.getClass(), subCriteria));
            }
        }

        Class<? super Criteria> superclass = clazz.getSuperclass();
        if (Criteria.class.isAssignableFrom(superclass)) {
            mds.addAll(travelCriteria(prefix, (Class<Criteria>) superclass, criteria));
        }
        return mds;
    }

    private String getAttribute(Field field) {
        CriteriaField ant = field.getAnnotation(CriteriaField.class);
        if (ant != null && StringUtils.isNoneBlank(ant.value())) return ant.value();
        return field.getName();
    }

    private Object getFieldValue(Field field, Object source) {
        try {
            field.setAccessible(true);
            Object o = field.get(source);
            field.setAccessible(false);
            return o;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

//    private Object getFilter(CriteriaFieldMetadata md, Criteria criteria) throws IllegalAccessException {
//        md.getField().setAccessible(true);
//        Object o = md.isNestedField() ?
//                md.getField().get(md.getSource()) :
//                md.getField().get(criteria);
//
//        md.getField().setAccessible(false);
//        return o;
//    }

}
