package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;
import java.util.function.Consumer;

public interface InternalTelegram {

    ThreadLocal<TelegramProcessContext> CONTEXT_ATTRIBUTE = new InheritableThreadLocal<>();

    static void init(TelegramProcessContext context) {
        CONTEXT_ATTRIBUTE.set(context);
    }

    static void update(Consumer<TelegramProcessContext.TelegramProcessContextBuilder> consumer) {
        TelegramProcessContext.TelegramProcessContextBuilder b = Optional.ofNullable(CONTEXT_ATTRIBUTE.get())
                .map(TelegramProcessContext::toBuilder)
                .orElseGet(TelegramProcessContext::builder);

        consumer.accept(b);
        CONTEXT_ATTRIBUTE.set(b.build());
    }

    static User getUser(TelegramProcessor processor, Update update) {
        return switch (processor.type()) {
            case MESSAGE -> update.getMessage().getFrom();
            case CALLBACK_QUERY -> update.getCallbackQuery().getFrom();
            default -> null;
        };
    }

    static Long getChatId(TelegramProcessor processor, Update update) {
        return switch (processor.type()) {
            case CALLBACK_QUERY -> update.getCallbackQuery().getMessage().getChatId();
            case MESSAGE -> update.getMessage().getChatId();
            case null, default -> null;
        };
    }

    static ChatSource getChatSource(TelegramProcessor processor, Update update) {
        return switch (processor.type()) {
            case MESSAGE -> {
                Message message = update.getMessage();
                yield Optional.ofNullable(message)
                        .map(Message::getChat)
                        .map(Chat::getType)
                        .map(ChatSource::fromType)
                        .orElse(null);
            }
            default -> ChatSource.PRIVATE;
        };
    }

}
