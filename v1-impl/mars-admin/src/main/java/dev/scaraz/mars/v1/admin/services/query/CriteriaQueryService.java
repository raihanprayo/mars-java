package dev.scaraz.mars.v1.admin.services.query;

import dev.scaraz.mars.common.tools.filter.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CriteriaQueryService<E, C extends Criteria> extends BaseQueryService<E> {
    List<E> findAll(C criteria);
    Page<E> findAll(C criteria, Pageable pageable);
}
