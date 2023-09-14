package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.repository.db.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.ConfirmService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class SchedulerService {

    private final StringRedisTemplate stringRedisTemplate;
    private final TicketConfirmRepo ticketConfirmRepo;
    private final ConfirmService confirmService;

    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;

    private final AtomicLong invalidRunCounter = new AtomicLong();

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        checkInvalidConfirmationTicket();
    }

    @Scheduled(cron = "0 0/53 * * * *")
    public void checkInvalidConfirmationTicket() {
        log.info("Check Any Invalid Confirmation Message ({})", invalidRunCounter.getAndIncrement());
        BoundSetOperations<String, String> boundSet = stringRedisTemplate.boundSetOps(TC_CONFIRM_NS);

        List<TicketConfirm> all = ticketConfirmRepo.findAll();
        if (all.isEmpty()) return;

        log.info("Found {} invalid confirmation", all.size());
        Set<String> members = Objects.requireNonNullElse(boundSet.members(), new HashSet<>());
        for (TicketConfirm confirm : all) {
            String messageIdStr = String.valueOf(confirm.getId());

            boolean included = members.contains(messageIdStr) &&
                    Optional.ofNullable(stringRedisTemplate
                                    .opsForValue()
                                    .get(AppConstants.Cache.j(TC_CONFIRM_NS, messageIdStr)))
                            .isPresent();

            log.debug("Still has running expire cache ({}) ? {}", messageIdStr, included);
            if (included) {
                switch (confirm.getStatus()) {
                    case TicketConfirm.CLOSED:
                        closeFlowService.confirmCloseAsync(confirm.getValue(), false, new TicketStatusFormDTO());
                        confirmService.deleteById(confirm.getId());
                        break;
                    case TicketConfirm.PENDING:
                    case TicketConfirm.POST_PENDING:
                    case TicketConfirm.POST_PENDING_CONFIRMATION:
                        pendingFlowService.confirmPendingAsync(confirm.getValue(), false, new TicketStatusFormDTO());
                        confirmService.deleteById(confirm.getId());
                        break;
                }
            }
            else confirmService.deleteById(confirm.getId());
        }
    }

}
