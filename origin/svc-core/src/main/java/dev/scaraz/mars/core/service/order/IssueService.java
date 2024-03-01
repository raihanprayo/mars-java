package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Issue;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface IssueService {
    Issue save(Issue issue);

    @Transactional
    Issue create(String name, Product product, @Nullable String description);

    @Transactional
    Issue create(CreateIssueDTO dto);

    @Transactional
    Issue update(long id, UpdateIssueDTO dto);

    void deleteByIds(Iterable<Long> ids);

    void restoreByIds(Iterable<Long> ids);
}
