package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.AccountApproval;

public interface UserApprovalService {
    AccountApproval save(AccountApproval o);

    void delete(String idOrNo);

    void delete(AccountApproval approval);

    void deleteCache(String id);

    AccountApproval findByIdOrNo(String idOrNo);

    AccountApproval findByTelegramId(long telegramId);

    boolean existsByTelegramId(long telegramId);
}
