package dev.scaraz.mars.app.witel.service.query;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.web.criteria.IssueCriteria;

public interface IssueQueryService extends CriteriaQueryService<Issue, IssueCriteria> {

    Issue findById(String idOrCode);
}
