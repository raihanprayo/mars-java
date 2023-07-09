package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import dev.scaraz.mars.core.config.event.app.AccountAccessEvent;
import dev.scaraz.mars.core.domain.credential.AccountEvent;
import dev.scaraz.mars.core.repository.db.credential.AccountEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventService {
    private final AccountEventRepo repo;

    @Async
    @EventListener(AccountAccessEvent.class)
    public void onAccessUpdate(AccountAccessEvent event) {
        DataSourceAuditor.setUsername(event.getUser());
        repo.save(AccountEvent.builder()
                .type(event.getType())
                .details(event.getDetails())
                .build());
        DataSourceAuditor.clear();
    }

}
