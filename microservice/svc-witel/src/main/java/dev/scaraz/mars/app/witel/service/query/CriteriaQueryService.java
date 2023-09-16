package dev.scaraz.mars.app.witel.service.query;

import dev.scaraz.mars.common.tools.filter.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CriteriaQueryService<E, C extends Criteria> {
    List<E> findAll();

    List<E> findAll(C criteria);

    Page<E> findAll(Pageable pageable);

    Page<E> findAll(C criteria, Pageable pageable);

    long count();

    long count(C criteria);

}
