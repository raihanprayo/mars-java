package dev.scaraz.mars.core.v2.query;

import dev.scaraz.mars.common.tools.filter.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpecificationQueryService<E, C extends Criteria> extends NonSpecificationQueryService<E> {

    @Override
    List<E> findAll();
    List<E> findAll(C criteria);

    @Override
    Page<E> findAll(Pageable pageable);
    Page<E> findAll(C criteria, Pageable pageable);

    @Override
    long count();
    long count(C criteria);

}
