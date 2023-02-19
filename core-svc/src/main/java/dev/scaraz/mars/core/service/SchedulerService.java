package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketConfirmService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class SchedulerService {

    private final StringRedisTemplate stringRedisTemplate;
    private final TicketConfirmRepo ticketConfirmRepo;
    private final TicketConfirmService ticketConfirmService;

    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;

    private final AtomicLong invalidRunCounter = new AtomicLong();

//    @PostConstruct
//    private void init() {
//        checkInvalidConfirmationTicket();
//    }

    @Scheduled(cron = "0 0/53 * * * *")
    public void checkInvalidConfirmationTicket() {
        log.info("*** Check Any Invalid Confirmation Message {} ***", invalidRunCounter.incrementAndGet());
        BoundSetOperations<String, String> boundSet = stringRedisTemplate.boundSetOps(TC_CONFIRM_NS);

        List<TicketConfirm> all = ticketConfirmRepo.findAll();
        if (all.isEmpty()) return;

        for (TicketConfirm confirm : all) {
            String messageIdStr = confirm.getId() + "";

            if (Boolean.TRUE.equals(boundSet.isMember(messageIdStr))) continue;

            switch (confirm.getStatus()) {
                case "CLOSED":
                    closeFlowService.confirmCloseAsync(confirm.getValue(), false, new TicketStatusFormDTO());
                    break;
                case "PENDING":
                    pendingFlowService.confirmPendingAsync(confirm.getValue(), false, new TicketStatusFormDTO());
                    break;
            }

            ticketConfirmService.deleteById(confirm.getId());
        }
    }

}
