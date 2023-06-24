package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.core.v2.service.app.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountExpiredService {

    private final ConfigService configService;

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void checkExpiredAccount() {

    }

}
