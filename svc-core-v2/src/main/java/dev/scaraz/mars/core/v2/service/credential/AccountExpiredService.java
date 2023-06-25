package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountExpired;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountExpiredService {

    private final AccountRepo accountRepo;
    private final ConfigService configService;

    @Transactional
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void checkExpiredAccount() {
        Instant now = Instant.now();
        List<Account> accounts = accountRepo.findAllByEnabledIsTrueAndExpiredActiveIsTrueAndExpiredDateLessThanEqual(now);

        for (Account account : accounts) {
            account.setEnabled(false);
            account.replace(AccountExpired.inactive());
            accountRepo.save(account);
        }
    }

}
