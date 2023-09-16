package dev.scaraz.mars.app.witel.repository;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepo extends JpaRepository<Issue, String>, JpaSpecificationExecutor<Issue> {

    List<Issue> findAllByDeletedIsFalse();

    List<Issue> findAllByDeletedIsTrueAndIdIn(Collection<String> ids);

    Optional<Issue> findByNameAndProduct(String name, Product product);

    boolean existsByCodeAndProduct(String name, Product product);

    boolean existsByWitelAndCodeAndProduct(Witel witel, String name, Product product);
}
