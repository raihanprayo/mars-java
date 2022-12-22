package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Issue;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface IssueService {
    Issue save(Issue issue);

    @Transactional
    Issue create(String name, Product product, @Nullable String description);
}
