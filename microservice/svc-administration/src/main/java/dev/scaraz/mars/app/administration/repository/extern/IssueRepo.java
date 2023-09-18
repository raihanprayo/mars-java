package dev.scaraz.mars.app.administration.repository.extern;

import dev.scaraz.mars.app.administration.domain.extern.Issue;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepo extends JpaRepository<Issue, String> {

    List<Issue> findAllByDeletedIsFalseAndWitelIn(List<Witel> witels);

    Optional<Issue> findByCode(String code);
    Optional<Issue> findByCodeAndProduct(String code, Product product);

}
