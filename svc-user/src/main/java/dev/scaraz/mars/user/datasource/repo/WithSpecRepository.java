package dev.scaraz.mars.user.datasource.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface WithSpecRepository<E, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
}
