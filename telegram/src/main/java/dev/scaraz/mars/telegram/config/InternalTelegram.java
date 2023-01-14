package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.TelegramProcessContext;

import java.util.Optional;
import java.util.function.Consumer;

public interface InternalTelegram {

    ThreadLocal<TelegramProcessContext> CONTEXT_ATTRIBUTE = new InheritableThreadLocal<>();
    public static void update(Consumer<TelegramProcessContext.TelegramProcessContextBuilder> consumer) {
        TelegramProcessContext.TelegramProcessContextBuilder b = Optional.ofNullable(CONTEXT_ATTRIBUTE.get())
                .map(TelegramProcessContext::toBuilder)
                .orElseGet(TelegramProcessContext::builder);

        consumer.accept(b);
        CONTEXT_ATTRIBUTE.set(b.build());
    }

}
