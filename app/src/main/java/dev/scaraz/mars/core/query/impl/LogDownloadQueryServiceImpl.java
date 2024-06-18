package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.LogDownload;
import dev.scaraz.mars.core.query.LogDownloadQueryService;
import dev.scaraz.mars.core.query.criteria.LogDownloadCriteria;
import dev.scaraz.mars.core.query.spec.LogDownloadSpecBuilder;
import dev.scaraz.mars.core.repository.db.LogDownloadRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogDownloadQueryServiceImpl implements LogDownloadQueryService {

    private final LogDownloadRepo repo;
    private final LogDownloadSpecBuilder specBuilder;

    @Override
    public Page<LogDownload> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<LogDownload> findAll(LogDownloadCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

}
