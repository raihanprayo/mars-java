package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.TelegramProcessContext;

import java.util.Optional;
import java.util.function.Consumer;

public class ProcessContextHolder {

    private static final ThreadLocal<TelegramProcessContext> ctxAttribute = new InheritableThreadLocal<>();

    public static TelegramProcessContext get() {
        return Optional.ofNullable(ctxAttribute.get())
                .orElseThrow(() -> new IllegalStateException("No update bounded to current thread"));
    }

    public static void getIfAvailable(Consumer<TelegramProcessContext> consumer) {
        try {
            consumer.accept(get());
        }
        catch (IllegalStateException ex) {}
    }

    public static void clear() {
        ctxAttribute.remove();
    }

    public static void update(Consumer<TelegramProcessContext.TelegramProcessContextBuilder> consumer) {
        TelegramProcessContext.TelegramProcessContextBuilder b = Optional.ofNullable(ctxAttribute.get())
                .map(TelegramProcessContext::toBuilder)
                .orElseGet(TelegramProcessContext::builder);

        consumer.accept(b);
        ctxAttribute.set(b.build());
    }

}
