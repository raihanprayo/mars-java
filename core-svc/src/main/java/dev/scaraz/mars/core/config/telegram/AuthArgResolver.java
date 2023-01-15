package dev.scaraz.mars.core.config.telegram;

import dev.scaraz.mars.common.exception.telegram.TgUnauthorizedError;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthArgResolver implements TelegramAnnotationArgResolver {

    private final AuthService authService;

    @Override
    public List<HandlerType> handledFor() {
        return List.of(HandlerType.MESSAGE);
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(TgAuth.class);
    }

    @Override
    public User resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        TgAuth md = mp.getParameterAnnotation(TgAuth.class);
        long id = update.getMessage().getFrom().getId();
        try {
            return authService.authenticateFromBot(id);
        }
        catch (TgUnauthorizedError ex) {
            if (md.throwUnautorized()) throw ex;
        }
        return (User) null;
    }
}
