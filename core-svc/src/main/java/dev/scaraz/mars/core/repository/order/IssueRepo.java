package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepo extends JpaRepository<Issue, String>, JpaSpecificationExecutor<Issue> {
    Optional<Issue> findByNameAndProduct(String name, Product product);
    boolean existsByNameAndProduct(String name, Product product);
}
