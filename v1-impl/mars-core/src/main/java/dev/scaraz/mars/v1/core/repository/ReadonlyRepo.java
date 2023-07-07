package dev.scaraz.mars.v1.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ReadonlyRepo<E, ID> extends Repository<E, ID> {

    List<E> findAll();
    List<E> findAll(Sort sort);
    Page<E> findAll(Pageable pageable);

    Optional<E> findById(ID id);

}
