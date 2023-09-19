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
    public static final String
            TG_USER = "tg-user",
            TG_CHAT = "tg-chat",
            TG_CHAT_SOURCE = "tg-chat-source";

    public static TelegramProcessContext get() {
        return Optional.ofNullable(CONTEXT_ATTRIBUTE.get())
                .orElseThrow(() -> new IllegalStateException("No telegram update bounded to current thread"));
    }

    public static Object getAttribute(String key) {
        return get().getAttribute(key);
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

    public static Update getUpdate() {
        return get().getUpdate();
    }

    @Nullable
    public static Long getUserId() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        return ((User) context.getAttribute(TG_USER)).getId();
    }

    @Nullable
    public static Long getChatId() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        return ((Chat) context.getAttribute(TG_CHAT)).getId();
//        Message message = getMessage(context.getType(), context.getUpdate());
//        return Optional.ofNullable(message)
//                .map(Message::getChatId)
//                .orElse(null);
    }

    @Nullable
    public static ChatSource getChatSource() {
        TelegramProcessContext context = CONTEXT_ATTRIBUTE.get();
        if (context == null) return null;

        return ((ChatSource) context.getAttribute(TG_CHAT_SOURCE));
    }

    private static Message getMessage(HandlerType type, Update update) {
        switch (type) {
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage();
            case MESSAGE:
                return update.getMessage();
        }
        return null;
    }
}
