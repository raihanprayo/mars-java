package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.DlStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.Getter;

@Getter
public class LogDownloadCriteria implements Criteria {
    private LongFilter id;
    private DlStatusFilter status;
    private StringFilter filename;
    private StringFilter createdBy;
    private InstantFilter createdAt;

    public LogDownloadCriteria setId(LongFilter id) {
        this.id = id;
        return this;
    }

    public LogDownloadCriteria setStatus(DlStatusFilter status) {
        this.status = status;
        return this;
    }

    public LogDownloadCriteria setFilename(StringFilter filename) {
        this.filename = filename;
        return this;
    }

    public LogDownloadCriteria setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public LogDownloadCriteria setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}
