package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;
import dev.scaraz.mars.core.repository.order.AgentWorklogRepo;
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
public class AgentWorklogQueryServiceImpl implements AgentWorklogQueryService {

    private final AgentWorklogRepo repo;

    @Override
    public List<AgentWorklog> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<AgentWorklog> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<AgentWorklog> findAll(AgentWorklogCriteria criteria) {
        return null;
    }

    @Override
    public Page<AgentWorklog> findAll(AgentWorklogCriteria criteria, Pageable pageable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public long count(AgentWorklogCriteria criteria) {
        return 0;
    }
}
