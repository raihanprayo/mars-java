package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.spec.TicketSummarySpecBuilder;
import dev.scaraz.mars.core.repository.order.TicketSummaryRepo;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketSummaryQueryServiceImpl implements TicketSummaryQueryService {

    private final TicketSummaryRepo repo;
    private final TicketSummarySpecBuilder specBuilder;

    @Override
    public List<TicketSummary> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<TicketSummary> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<TicketSummary> findAll(TicketSummaryCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<TicketSummary> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(TicketSummaryCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public long countByProduct(Product product, boolean currentUser) {
        if (currentUser) {
            User usr = SecurityUtil.getCurrentUser();
            if (usr != null) return repo.countByProductAndWipById(product, usr.getId());
        }
        return repo.countByProduct(product);
    }
}
