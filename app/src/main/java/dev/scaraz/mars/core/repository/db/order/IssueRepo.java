package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.symptom.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepo extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    List<Issue> findAllByDeletedIsFalse();
    List<Issue> findAllByDeletedIsTrueAndIdIn(Collection<Long> ids);

    Optional<Issue> findByNameAndProduct(String name, Product product);
    boolean existsByNameAndProduct(String name, Product product);
}
