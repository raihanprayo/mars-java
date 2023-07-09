package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.repository.cache.RegistrationApprovalRepo;
import dev.scaraz.mars.core.repository.db.credential.AccountApprovalRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.credential.AccountApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static dev.scaraz.mars.common.utils.AppConstants.Cache.USR_APPROVAL_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class AccountApprovalServiceImpl implements AccountApprovalService {

    private final AccountApprovalRepo repo;
    private final RegistrationApprovalRepo cacheRepo;
    private final StringRedisTemplate stringRedisTemplate;

    @Lazy
    private final AppConfigService appConfigService;

    @Override
    public AccountApproval save(AccountApproval o) {
        AccountApproval save = repo.save(o);
        int duration = appConfigService.getApprovalDurationHour_drt()
                .getAsNumber()
                .intValue();
        cacheRepo.save(new RegistrationApproval(save.getId(), duration));
        return save;
    }

    @Override
    public void delete(String id) {
        try {
            repo.deleteById(id);
            cacheRepo.deleteById(id);
        }
        catch (Exception ex) {}
    }

    @Override
    public void delete(AccountApproval approval) {
        repo.deleteById(approval.getId());
        cacheRepo.deleteById(approval.getId());
    }

    @Override
    public void deleteCache(String id) {
        cacheRepo.deleteById(id);
    }

    @Override
    public AccountApproval findByIdOrNo(String idOrNo) {
        return repo.findByIdOrNo(idOrNo, idOrNo)
                .orElseThrow(() -> NotFoundException.entity(AccountApproval.class, "id/no", idOrNo));
    }

    @Override
    public AccountApproval findByTelegramId(long telegramId) {
        return repo.findByTgId(telegramId)
                .orElseThrow(() -> NotFoundException.entity(AccountApproval.class, "telegramId", telegramId));
    }

    @Override
    public boolean existsByTelegramId(long telegramId) {
        return repo.existsByTgId(telegramId);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("* Check invalid approvals");
        BoundSetOperations<String, String> bounded = stringRedisTemplate.boundSetOps(USR_APPROVAL_NS);
        Set<String> members = bounded.members();

        if (members == null || members.isEmpty()) {
            log.info("No invalid approval(s)");
            return;
        }

        List<String> ids = new ArrayList<>();
        for (String id : members) {
            Optional<RegistrationApproval> optCache = cacheRepo.findById(id);
            if (optCache.isEmpty()) {
                ids.add(id);
                delete(id);
                bounded.remove(id);
            }
        }

        if (!ids.isEmpty()) log.info("Found {} invalid approval(s)", ids.size());
    }
}
