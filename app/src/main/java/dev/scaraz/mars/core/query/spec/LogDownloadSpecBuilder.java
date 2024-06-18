package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.LogDownload;
import dev.scaraz.mars.core.domain.LogDownload_;
import dev.scaraz.mars.core.query.criteria.LogDownloadCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LogDownloadSpecBuilder extends QueryBuilder<LogDownload, LogDownloadCriteria> {

    @Override
    public Specification<LogDownload> createSpec(LogDownloadCriteria criteria) {
        return chain()
                .pick(LogDownload_.id, criteria.getId())
                .pick(LogDownload_.status, criteria.getStatus())
                .pick(LogDownload_.filename, criteria.getFilename())
                .pick(LogDownload_.createdBy, criteria.getCreatedBy())
                .pick(LogDownload_.createdAt, criteria.getCreatedAt())
                .specification();
    }

}
