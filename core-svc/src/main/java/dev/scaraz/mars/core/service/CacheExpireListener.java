package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.cache.StatusConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor

@Component
public class CacheExpireListener {

    private final TicketService ticketService;
    private final TicketFlowService ticketFlowService;
    private final TicketQueryService ticketQueryService;
    private final TicketSummaryQueryService summaryQueryService;

    @Async
    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void onCacheConfirmationExpired(RedisKeyExpiredEvent<StatusConfirm> event) {
        if (!(event.getValue() instanceof StatusConfirm)) return;

        StatusConfirm data = (StatusConfirm) event.getValue();
        String ticketNo = data.getNo();
        ticketFlowService.confirmClose(ticketNo, false, new TicketStatusFormDTO());
    }

}
