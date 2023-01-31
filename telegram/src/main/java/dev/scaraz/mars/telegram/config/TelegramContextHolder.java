package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

import static dev.scaraz.mars.telegram.config.InternalTelegram.CONTEXT_ATTRIBUTE;

public class TelegramContextHolder {

    public static TelegramProcessContext get() {
        return Optional.ofNullable(CONTEXT_ATTRIBUTE.get())
                .orElseThrow(() -> new IllegalStateException("No telegram update bounded to current thread"));
    }

    public static void getIfAvailable(Consumer<TelegramProcessContext> consumer) {
        try {
            consumer.accept(get());
        }
        catch (IllegalStateException ex) {
        }
    }

    public static void clear() {
        CONTEXT_ATTRIBUTE.remove();
    }

    public static boolean hasContext() {
        return CONTEXT_ATTRIBUTE.get() != null;
    }

    @Nullable
    public static Long getUserId() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        Message message = getMessage(context.getType(), context.getUpdate());
        return Optional.ofNullable(message)
                .map(Message::getFrom)
                .map(User::getId)
                .orElse(null);
    }

    @Nullable
    public static Long getChatId() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        Message message = getMessage(context.getType(), context.getUpdate());
        return Optional.ofNullable(message)
                .map(Message::getChatId)
                .orElse(null);
    }

    @Nullable
    public static ChatSource getChatSource() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        Message message = getMessage(context.getType(), context.getUpdate());
        return Optional.ofNullable(message)
                .map(Message::getChat)
                .map(Chat::getType)
                .map(ChatSource::fromType)
                .orElse(null);
    }

    private static Message getMessage(HandlerType type, Update update) {
        Message message;
        switch (type) {
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage();
            case MESSAGE:
                return update.getMessage();
        }
        return null;
    }
}
