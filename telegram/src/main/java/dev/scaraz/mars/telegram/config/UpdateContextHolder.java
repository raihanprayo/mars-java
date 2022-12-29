package dev.scaraz.mars.telegram.config;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public class UpdateContextHolder {
    private UpdateContextHolder() {}

    private static final ThreadLocal<Update> updateAttribute = new InheritableThreadLocal<>();

    public static Update get() {
        return Optional.ofNullable(updateAttribute.get())
                .orElseThrow(() -> new IllegalStateException("No update bounded to current thread"));
    }
    public static void set(Update update) {
        updateAttribute.set(update);
    }

    public static void clear() {
        updateAttribute.remove();
    }

}
