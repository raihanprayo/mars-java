package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Component
public final class TextArgResolver implements TelegramAnnotationArgResolver {

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(Text.class);
    }

    @Override
    public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        switch (ctx.getScope()) {
            case MESSAGE: {
                return mc.getArgument().orElse("");
            }
            case CALLBACK_QUERY: {
                return update.getCallbackQuery().getMessage().getText();
            }
        }
        return null;
    }

}
