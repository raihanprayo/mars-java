package dev.scaraz.mars.app.witel.service.query;

import dev.scaraz.mars.common.tools.filter.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseQueryService<E> {
    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    long count();


}
