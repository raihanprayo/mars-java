package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.repository.order.IssueRepo;
import dev.scaraz.mars.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

@Slf4j
@RequiredArgsConstructor

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepo repo;

    @Override
    public Issue save(Issue issue) {
        return repo.save(issue);
    }

    @Override
    @Transactional
    public Issue create(String name, Product product, @Nullable String description) {
        if (repo.existsByNameAndProduct(name, product))
            throw BadRequestException.duplicateEntity(Issue.class, "name", name);

        log.info("CREATE NEW ISSUE WITH NAME {} AND PRODUCT {}", name, product);
        return save(Issue.builder()
                .name(name.toLowerCase())
                .product(product)
                .description(description)
                .build());
    }

}
