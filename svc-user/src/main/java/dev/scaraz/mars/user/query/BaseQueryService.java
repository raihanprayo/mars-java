package dev.scaraz.mars.user.query;

import dev.scaraz.mars.common.tools.filter.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseQueryService<E, C extends Criteria> {

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    List<E> findAll(C criteria);

    Page<E> findAll(C criteria, Pageable pageable);

}
