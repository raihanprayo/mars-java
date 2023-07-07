package dev.scaraz.mars.v1.admin.services.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseQueryService<E> {
    List<E> findAll();
    Page<E> findAll(Pageable pageable);
}
