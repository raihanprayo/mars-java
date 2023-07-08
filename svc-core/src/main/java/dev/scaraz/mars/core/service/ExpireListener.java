package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.repository.db.credential.AccountApprovalRepo;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor

@Component
public class ExpireListener {

    private final TicketFlowService ticketFlowService;

    private final AccountApprovalRepo accountApprovalRepo;

    private final TelegramBotService botService;

//    @Async
//    @EventListener(classes = RedisKeyExpiredEvent.class)
//    public void onCacheConfirmationExpired(RedisKeyExpiredEvent<StatusConfirm> event) {
//        if (!(event.getValue() instanceof StatusConfirm)) return;
//
//        StatusConfirm data = (StatusConfirm) event.getValue();
//        String ticketNo = data.getNo();
//        if (data.getStatus().equals(TcStatus.CLOSED.name()))
//            ticketFlowService.confirmClose(ticketNo, false, new TicketStatusFormDTO());
//        else if (data.getStatus().equals(TcStatus.PENDING.name()))
//            ticketFlowService.confirmPending(ticketNo, false, new TicketStatusFormDTO());
//    }

    @Async
    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void onRegistrationApprovalExpired(RedisKeyExpiredEvent<RegistrationApproval> event) throws TelegramApiException {
        if (!(event.getValue() instanceof RegistrationApproval)) return;

        RegistrationApproval data = (RegistrationApproval) event.getValue();
        AccountApproval approval = accountApprovalRepo.findById(data.getId())
                .orElseThrow();

        Long tgId = approval.getTg().getId();
        botService.getClient().execute(SendMessage.builder()
                .chatId(tgId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Maaf permintaan registrasimu sudah kadaluarsa, silahkan mengirim ulang registrasi"
                ))
                .build());
        accountApprovalRepo.delete(approval);
    }

}
