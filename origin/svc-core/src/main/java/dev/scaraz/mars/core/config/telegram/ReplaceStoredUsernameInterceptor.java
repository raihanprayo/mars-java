package dev.scaraz.mars.core.config.telegram;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramInterceptor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@RequiredArgsConstructor
@Order(Integer.MIN_VALUE)
public class ReplaceStoredUsernameInterceptor implements TelegramInterceptor {

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;

    @Override
    public boolean intercept(HandlerType type, Update update, TelegramHandler handler) {
        try {
            User user = (User) TelegramContextHolder.get().getAttribute(TelegramContextHolder.TG_USER);
            if (!user.getIsBot()) {
                Account account = accountQueryService.findByTelegramId(user.getId());

                // Tidak ada ubahan
                if (account.getTg().getUsername().equals(user.getUserName()))
                    return true;

                account.getTg().setUsername(user.getUserName());
                accountService.save(account);
            }
            else return false;
        }
        catch (Exception ex) {
        }
        return true;
    }

}
