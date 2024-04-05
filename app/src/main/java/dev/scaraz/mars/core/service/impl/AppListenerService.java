package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.repository.db.credential.AccountApprovalRepo;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.security.authentication.identity.MarsWebToken;
import dev.scaraz.mars.security.authentication.token.MarsWebAuthenticationToken;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.session.Session;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppListenerService {

    private final AuthService authService;
    private final AccountQueryService accountQueryService;
    private final AccountApprovalRepo accountApprovalRepo;
    private final TelegramBotService botService;

    @Async
    @EventListener(SessionExpiredEvent.class)
    public void onSessionExpired(SessionExpiredEvent event) {
        Session session = event.getSession();
        Object authentication = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (authentication instanceof MarsWebAuthenticationToken) {
            log.debug("Session Expired");

            MarsWebToken principal = ((MarsWebAuthenticationToken) authentication).getPrincipal();
            Account account = accountQueryService.findById(principal.getId());

            if (account.hasAnyRole(AuthorityConstant.AGENT_ROLE))
                authService.logout(account, true);
        }
    }

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
