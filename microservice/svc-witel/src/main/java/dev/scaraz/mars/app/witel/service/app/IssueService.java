package dev.scaraz.mars.app.witel.service.app;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;

public interface IssueService {
    Issue save(Issue issue);

    Issue create(CreateIssueDTO dto);

    Issue update(String id, UpdateIssueDTO dto);

    void deleteByIds(Iterable<String> ids);

    void restoreByIds(Iterable<String> ids);
}
