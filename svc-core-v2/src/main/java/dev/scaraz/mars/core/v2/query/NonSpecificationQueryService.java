package dev.scaraz.mars.core.v2.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NonSpecificationQueryService<E> {

    List<E> findAll();

    Page<E> findAll(Pageable pageable);

    long count();

}
