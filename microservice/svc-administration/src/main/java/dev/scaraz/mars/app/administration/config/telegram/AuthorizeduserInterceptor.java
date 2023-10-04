package dev.scaraz.mars.app.administration.config.telegram;

import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.web.dto.UserAccount;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramInterceptor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthorizeduserInterceptor implements TelegramInterceptor {
    public static final String ATTRIBUTE = "kc-user";

    private final UserService userService;

    @Override
    public boolean intercept(HandlerType type, Update update, TelegramHandler handler) {
        Optional<UserRepresentation> userOpt = userService.findByTelegramIdOpt(TelegramContextHolder.getUserId());
        userOpt.ifPresent(userRepresentation -> {
            UserAccount account = new UserAccount(userRepresentation);
            TelegramContextHolder.get().addAttribute(ATTRIBUTE, account);
        });
        return true;
    }
}
