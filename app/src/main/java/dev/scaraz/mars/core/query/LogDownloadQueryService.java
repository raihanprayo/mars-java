package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.LogDownload;
import dev.scaraz.mars.core.query.criteria.LogDownloadCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogDownloadQueryService {
    Page<LogDownload> findAll(Pageable pageable);

    Page<LogDownload> findAll(LogDownloadCriteria criteria, Pageable pageable);
}
