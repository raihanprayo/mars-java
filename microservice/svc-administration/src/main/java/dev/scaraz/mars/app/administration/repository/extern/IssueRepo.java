package dev.scaraz.mars.app.administration.repository.extern;

import dev.scaraz.mars.app.administration.domain.extern.Issue;
import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepo extends JpaRepository<Issue, String> {

    List<Issue> findAllByDeletedIsFalseAndWitelIn(List<Witel> witels);

}
