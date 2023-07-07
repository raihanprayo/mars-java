package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.UserApproval;

public interface UserApprovalService {
    UserApproval save(UserApproval o);

    void delete(String idOrNo);

    void delete(UserApproval approval);

    void deleteCache(String id);

    UserApproval findByIdOrNo(String idOrNo);

    UserApproval findByTelegramId(long telegramId);

    boolean existsByTelegramId(long telegramId);
}
