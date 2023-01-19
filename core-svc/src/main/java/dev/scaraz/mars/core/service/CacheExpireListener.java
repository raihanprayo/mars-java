package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.cache.StatusConfirm;
import dev.scaraz.mars.core.domain.credential.UserApproval;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.repository.cache.RegistrationApprovalRepo;
import dev.scaraz.mars.core.repository.credential.UserApprovalRepo;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor

@Component
public class CacheExpireListener {

    private final TicketFlowService ticketFlowService;

    private final UserApprovalRepo userApprovalRepo;

    private final TelegramBotService botService;

    @Async
    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void onCacheConfirmationExpired(RedisKeyExpiredEvent<StatusConfirm> event) {
        if (!(event.getValue() instanceof StatusConfirm)) return;

        StatusConfirm data = (StatusConfirm) event.getValue();
        String ticketNo = data.getNo();
        ticketFlowService.confirmClose(ticketNo, false, new TicketStatusFormDTO());
    }

    @Async
    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void onRegistrationApprovalExpired(RedisKeyExpiredEvent<RegistrationApproval> event) throws TelegramApiException {
        if (!(event.getValue() instanceof RegistrationApproval)) return;

        RegistrationApproval data = (RegistrationApproval) event.getValue();
        UserApproval approval = userApprovalRepo.findById(data.getId())
                .orElseThrow();

        Long tgId = approval.getTg().getId();
        botService.getClient().execute(SendMessage.builder()
                .chatId(tgId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Maaf permintaan registrasimu sudah kadaluarsa, silahkan mengirim ulang registrasi"
                ))
                .build());
        userApprovalRepo.delete(approval);
    }

}
