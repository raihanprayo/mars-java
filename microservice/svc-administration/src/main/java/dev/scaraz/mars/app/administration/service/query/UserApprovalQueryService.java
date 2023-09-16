package dev.scaraz.mars.app.administration.service.query;

import dev.scaraz.mars.app.administration.domain.db.UserApproval;

public interface UserApprovalQueryService {
    UserApproval findByIdOrNo(String idOrNo);
}
