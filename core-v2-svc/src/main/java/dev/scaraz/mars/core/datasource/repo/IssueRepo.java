package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.datasource.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepo extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    boolean existsByNameAndProduct(String name, Product product);

}
