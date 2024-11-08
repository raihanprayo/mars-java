package dev.scaraz.mars.core.config.telegram;

import dev.scaraz.mars.common.exception.telegram.TgUnauthorizedError;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthArgResolver implements TelegramAnnotationArgResolver {

    private final AuthService authService;

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(TgAuth.class);
    }

    @Override
    public Account resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        log.debug("Resolving parameter of {}.{} at index {}",
                mp.getMethod().getDeclaringClass().getSimpleName(),
                mp.getMethod().getName(),
                mp.getParameterIndex());

        log.debug("Param annotations, {}", (Object) mp.getParameterAnnotations());
        TgAuth md = mp.getParameterAnnotation(TgAuth.class);
        try {
            return authService.authenticateFromBot(TelegramContextHolder.getUserId());
        }
        catch (TgUnauthorizedError ex) {
            ex.printStackTrace();
            if (md.throwUnautorized()) throw ex;
        }
        return null;
    }

    private long getTelegramId(HandlerType type, Update update) {
        switch (type) {
            case MESSAGE:
                return update.getMessage().getFrom().getId();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom().getId();
        }
        throw new IllegalStateException("Unknown handler type");
    }

}
