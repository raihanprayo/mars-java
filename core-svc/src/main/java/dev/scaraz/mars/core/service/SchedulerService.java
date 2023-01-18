package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.cache.StatusConfirm;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.cache.StatusConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class SchedulerService {

    private final StringRedisTemplate stringRedisTemplate;
    private final StatusConfirmRepo statusConfirmRepo;
    private final TicketQueryService ticketQueryService;
    private final TicketFlowService ticketFlowService;

    private final AtomicLong invalidRunCounter = new AtomicLong();

//    @PostConstruct
//    private void init() {
//        checkInvalidConfirmationTicket();
//    }

    @Scheduled(cron = "* 0/53 * * * *")
    public void checkInvalidConfirmationTicket() {
        log.info("*** Check Any Invalid Confirmation Message {} ***", invalidRunCounter.incrementAndGet());
        BoundSetOperations<String, String> boundSet = stringRedisTemplate.boundSetOps(TC_CONFIRM_NS);
        Set<String> members = boundSet.members();
        if (members == null || members.isEmpty()) return;

        int count = 0;
        for (String member : members) {
            Long messageId = Long.valueOf(member);
            Optional<StatusConfirm> cache = statusConfirmRepo.findById(messageId);
            if (cache.isEmpty()) {
                count++;
                log.debug("REMOVING INVALID CONFIRMATION -- MESSAGE ID {}", messageId);
                boundSet.remove(member);
                Ticket tc = ticketQueryService.findByMessageId(messageId);
                ticketFlowService.confirmCloseAsync(tc.getNo(), false, new TicketStatusFormDTO());
            }
        }

        if (count > 0) log.info("Removed {} invalid message(s)", count);
    }

}
