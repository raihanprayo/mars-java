package dev.scaraz.mars.app.administration.repository.extern;

import dev.scaraz.mars.app.administration.domain.extern.IssueParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueParamRepo extends JpaRepository<IssueParam, String> {

    List<IssueParam> findAllByIssueId(String issueId);

}
