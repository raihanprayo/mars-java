package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.TelegramProcessContext;

import java.util.Optional;
import java.util.function.Consumer;

import static dev.scaraz.mars.telegram.config.InternalTelegram.CONTEXT_ATTRIBUTE;

public class TelegramContextHolder {

    public static TelegramProcessContext get() {
        return Optional.ofNullable(CONTEXT_ATTRIBUTE.get())
                .orElseThrow(() -> new IllegalStateException("No update bounded to current thread"));
    }

    public static void getIfAvailable(Consumer<TelegramProcessContext> consumer) {
        try {
            consumer.accept(get());
        }
        catch (IllegalStateException ex) {}
    }

    public static void clear() {
        CONTEXT_ATTRIBUTE.remove();
    }

    public static boolean hasContext() {
        return CONTEXT_ATTRIBUTE.get() != null;
    }

}
