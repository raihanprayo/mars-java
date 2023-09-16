package dev.scaraz.mars.app.administration.service.app;

import dev.scaraz.mars.app.administration.domain.db.UserApproval;

public interface UserApprovalService {
    UserApproval save(UserApproval approval);

    void deleteById(String id);

    boolean isInApprovalWaitList(long id);
}
