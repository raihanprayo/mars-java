package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
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
        switch (processor.type()) {
            case MESSAGE:
                return getMessage(processor.type(), update).getFrom();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom();
        }
        return null;
    }
    static Chat getChat(TelegramProcessor processor, Update update) {
        Message message = getMessage(processor.type(), update);
        return Optional.ofNullable(message)
                .map(Message::getChat)
                .orElse(null);
    }
    static ChatSource getChatSource(TelegramProcessor processor, Update update) {
        Message message = getMessage(processor.type(), update);
        return Optional.ofNullable(message)
                .map(Message::getChat)
                .map(Chat::getType)
                .map(ChatSource::fromType)
                .orElse(null);
    }

    static Message getMessage(HandlerType type, Update update) {
        switch (type) {
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage();
            case MESSAGE:
                return update.getMessage();
        }
        return null;
    }

}
