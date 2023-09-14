package dev.scaraz.mars.app.api.service.app;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class NoQueueService {

    private static final String DATE_FORMAT = "yyMMdd";
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final AtomicInteger PREVIOUS = new AtomicInteger();

    @Scheduled(cron = "0 0 * * * *")
    private void resetCounter() {
        COUNTER.set(0);
    }

    synchronized String generateNo() {
        int index = COUNTER.incrementAndGet();
        return String.join(
                "",
                LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                StringUtils.leftPad(String.valueOf(index), 6, "0")
        );
    }

}
